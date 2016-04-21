package naga.core.orm.expressionsqlcompiler.sql;

import naga.core.orm.expression.Expression;
import naga.core.orm.expressionsqlcompiler.lci.CompilerDomainModelReader;
import naga.core.orm.expressionsqlcompiler.term.Options;
import naga.core.orm.mapping.SqlColumnToEntityFieldMapping;
import naga.core.orm.mapping.SqlRowToEntityMapping;
import naga.core.util.Numbers;
import naga.core.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Bruno Salmon
 */
public class SqlBuild {
    /**
     * Final fields built by sql compilation in order to generate a SqlCompiled object **
     */
    private String sql;
    private ArrayList<String> parameterNames = new ArrayList<>();
    private boolean isQuery;
    private String[] autoGeneratedKeyColumnNames;
    private String countSql; // select count(*) ... used to return the total row numbers when the query is truncated by the limit clause

    private Object selectDomainClass;
    private ArrayList<SqlColumnToEntityFieldMapping> columnMappings = new ArrayList<>(); // first one = id column

    private Expression sqlUncompilableCondition;
    private boolean cacheable = true;

    /**
     * Temporary fields used during sql compilation **
     */
    private SqlBuild parent;
    private String tableName;
    private String tableAlias;
    private Object compilingClass;
    private String compilingTableAlias;
    private SqlColumnToEntityFieldMapping leftJoinMapping;
    private DbmsSqlSyntaxOptions dbmsSyntax;

    private boolean distinct;

    private HashMap<SqlClause, StringBuilder> sqlClauseBuilders = new HashMap<>();
    private HashMap<String /* table alias */, Map<Join, Join> /* joins */ > joins = new HashMap<>();

    private HashMap<String, String> tableAliases = new HashMap<>();   // tableAlias => tableName
    private HashMap<String, String> logicalAliases = null; // logicalAlias => sqlAlias
    private List<String> orderedAliases = new ArrayList<>();
    private HashMap<String, SqlColumnToEntityFieldMapping> fullColumnNameToColumnMappings = new HashMap<>(); // tableAlias.columnName => columnMapping
    //private int fromTablesCount;

    public SqlBuild(SqlBuild parent, Object selectDomainClass, String tableAlias, SqlClause clause, DbmsSqlSyntaxOptions dbmsSyntax, CompilerDomainModelReader modelReader) {
        this.parent = parent;
        this.compilingClass = this.selectDomainClass = selectDomainClass;
        tableName = modelReader.getDomainClassSqlTableName(selectDomainClass);
        this.compilingTableAlias = this.tableAlias = getNewTableAlias(tableName, tableAlias, false);
        this.dbmsSyntax = dbmsSyntax;
        prepareAppend(clause, ""); // just for marking the clause
    }

    public Object getSelectDomainClass() {
        return selectDomainClass;
    }

    public DbmsSqlSyntaxOptions getDbmsSyntax() {
        return dbmsSyntax;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public int getParameterCount() {
        return parameterNames.size() + (parent == null ? 0 : parent.getParameterCount());
    }

    public void setAutoGeneratedKeyColumnNames(String[] autoGeneratedKeyColumnNames) {
        this.autoGeneratedKeyColumnNames = autoGeneratedKeyColumnNames;
        if (autoGeneratedKeyColumnNames != null && dbmsSyntax == DbmsSqlSyntaxOptions.HSQL_SYNTAX) // HSQL error if not uppercase
            for (int i = 0; i < autoGeneratedKeyColumnNames.length; i++)
                autoGeneratedKeyColumnNames[i] = autoGeneratedKeyColumnNames[i].toUpperCase();
    }

    public Expression getSqlUncompilableCondition() {
        return sqlUncompilableCondition;
    }

    public void setSqlUncompilableCondition(Expression sqlUncompilableCondition) {
        this.sqlUncompilableCondition = sqlUncompilableCondition;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
        if (!cacheable && parent != null)
            parent.setCacheable(false);
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public SqlCompiled toSqlCompiled() {
        int n = columnMappings.size();
        SqlColumnToEntityFieldMapping[] nonIdColumnMappings = new SqlColumnToEntityFieldMapping[n - 1];
        for (int i = 1; i < n; i++)
            nonIdColumnMappings[i - 1] = columnMappings.get(i);
        SqlRowToEntityMapping queryMapping = new SqlRowToEntityMapping(0, selectDomainClass, nonIdColumnMappings);
        return new SqlCompiled(toSql(), toCountSql(), parameterNames, isQuery, autoGeneratedKeyColumnNames, queryMapping, sqlUncompilableCondition, cacheable);
    }

    public String toSql() {
        if (sql == null) {
            StringBuilder sb = new StringBuilder();
            StringBuilder select = getClauseBuilder(SqlClause.SELECT);
            StringBuilder insert = getClauseBuilder(SqlClause.INSERT);
            isQuery = select != null;
            if (isQuery)
                sb.append("select ").append(_if(distinct, "distinct ")).append(select).append(" from ");
            else if (getClauseBuilder(SqlClause.UPDATE) != null)
                sb.append("update ");
            else if (insert != null)
                sb.append("insert into ");
            else if (getClauseBuilder(SqlClause.DELETE) != null)
                sb.append("delete ").append(_if(dbmsSyntax.isRepeatDeleteAlias(), tableAlias)).append(" from ");
            //sb.append(_if(fromTablesCount > 1, "(")); // select * from (t1, t2) join ... (marche pas avec postgres)
            boolean first = true;
            for (String tableAlias : orderedAliases) {
                if (!isJoinTableAlias(tableAlias)) {
                    String tableName = tableAliases.get(tableAlias);
                    if (!first)
                        sb.append(", ");
                    sb.append(tableName); // tableName
                    if (insert == null) // no alias allowed in insert sql statement
                        sb.append(" as ").append(tableAlias); // may need " as " instead of ' ' for some dbms
                    first = false;
                }
                Join.appendJoins(joins.get(tableAlias), sb);
            }
            // temporary hack to make the statistics ceremony filter work for right dates
            Join.appendNotYetAppendJoins(joins.get("a"), sb); // looking for alias a = Attendance and add it if present but not added (the join is missing because it refers to the parent select)
            sb//.append(_if(fromTablesCount > 1, ") "))
                    .append(_if(" set ", getClauseBuilder(SqlClause.UPDATE), "", sb))
                    .append(_if(" (", insert, ")", sb))
                    .append(_if(" values (", getClauseBuilder(SqlClause.VALUES), ")", sb))
                    .append(_if(" where ", getClauseBuilder(SqlClause.WHERE), "", sb))
                    .append(_if(" group by ", getClauseBuilder(SqlClause.GROUP_BY), "", sb))
                    .append(_if(" having ", getClauseBuilder(SqlClause.HAVING), "", sb))
                    .append(_if(" order by ", getClauseBuilder(SqlClause.ORDER_BY), "", sb))
                    .append(_if(" limit ", getClauseBuilder(SqlClause.LIMIT), "", sb));
            sql = sb.toString();
        }
        return sql;
    }

    public String toCountSql() {
        if (countSql == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("select count(*) from ");
            boolean first = true;
            for (String tableAlias : orderedAliases) {
                if (!isJoinTableAlias(tableAlias)) {
                    String tableName = tableAliases.get(tableAlias);
                    if (!first)
                        sb.append(", ");
                    sb.append(tableName); // tableName
                    sb.append(" as ").append(tableAlias); // may need " as " instead of ' ' for some dbms
                    first = false;
                }
                Join.appendJoins(joins.get(tableAlias), sb);
            }
            sb//.append(_if(fromTablesCount > 1, ") "))
                    .append(_if(" where ", getClauseBuilder(SqlClause.WHERE), "", sb))
                    .append(_if(" group by ", getClauseBuilder(SqlClause.GROUP_BY), "", sb))
                    .append(_if(" having ", getClauseBuilder(SqlClause.HAVING), "", sb));
            countSql = sb.toString();
        }
        return countSql;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public Object getCompilingClass() {
        return compilingClass;
    }

    public void setCompilingClass(Object compilingClass) {
        this.compilingClass = compilingClass;
    }

    public String getCompilingTableAlias() {
        return compilingTableAlias;
    }

    public void setCompilingTableAlias(String compilingTableAlias) {
        this.compilingTableAlias = compilingTableAlias;
    }

    public String getClassAlias(Object domainClass, CompilerDomainModelReader modelReader) {
        if  (domainClass == compilingClass)
            return compilingTableAlias;
        String tableName = modelReader.getDomainClassSqlTableName(domainClass);
        for (Map.Entry<String, String> entry : tableAliases.entrySet()) {
            if (tableName.equals(entry.getValue()))
                return entry.getKey();
        }
        if (parent != null)
            return parent.getClassAlias(domainClass, modelReader);
        return null;
    }

    public SqlColumnToEntityFieldMapping getLeftJoinMapping() {
        return leftJoinMapping;
    }

    public void setLeftJoinMapping(SqlColumnToEntityFieldMapping leftJoinMapping) {
        this.leftJoinMapping = leftJoinMapping;
    }

    public StringBuilder getClauseBuilder(SqlClause clause) {
        return sqlClauseBuilders.get(clause);
    }

    public int evaluateLimit() {
        StringBuilder limit = getClauseBuilder(SqlClause.LIMIT);
        return Strings.isEmpty(limit) ? -1 : Numbers.intValue(limit);
    }


    /* Building methods */

    public StringBuilder prepareAppend(Options o) {
        return prepareAppend(o.clause, o.separator);
    }

    public StringBuilder prepareAppend(SqlClause clause, String separator) {
        StringBuilder clauseBuilder = sqlClauseBuilders.get(clause);
        if (clauseBuilder == null)
            sqlClauseBuilders.put(clause, clauseBuilder = new StringBuilder());
        if (Strings.isNotEmpty(separator) && !endsWith(clauseBuilder, separator) && !endsWith(clauseBuilder, "(") && !endsWith(clauseBuilder, "["))
            clauseBuilder.append(separator);
        return clauseBuilder;
    }

    private static boolean endsWith(StringBuilder sb, String s) {
        int sbLength = sb.length();
        int sLength = s.length();
        if (sLength == 0 || sbLength < sLength)
            return false;
        for (int i = 0; i < sLength; i++)
            if (sb.charAt(i + sbLength - sLength) != s.charAt(i))
                return false;
        return true;
    }

    public String getNewTableAlias(String tableName, String tableAlias, boolean join) {
        if (tableAlias == null) {
            char c = join ? 'j' : 't';
            StringBuilder sb = new StringBuilder();
            for (SqlBuild b = this; b != null; b = b.parent)
                sb.append(c);
            sb.append(tableAliases.size() + 1);
            tableAlias = sb.toString();
        }
        tableAliases.put(tableAlias, tableName);
        orderedAliases.add(tableAlias);
        return tableAlias;
    }

    private boolean isJoinTableAlias(String tableAlias) {
        return tableAlias.charAt(0) == 'j';
    }

    public String getSqlAlias(String logicalAlias) {
        String sqlAlias = logicalAliases == null ? null : logicalAliases.get(logicalAlias);
        if (sqlAlias == null && parent != null)
            sqlAlias = parent.getSqlAlias(logicalAlias);
        return sqlAlias != null ? sqlAlias : logicalAlias;
    }

    private void recordLogicalAlias(String logicalAlias, String sqlAlias) {
        if (logicalAliases == null)
            logicalAliases = new HashMap<>();
        logicalAliases.put(logicalAlias, sqlAlias);
    }

    public SqlColumnToEntityFieldMapping addColumnInClause(String tableAlias, String columnName, Object fieldId, Object foreignFieldClassId, SqlClause clause, String separator, boolean grouped, boolean isBoolean, boolean generateQueryMapping) {
        tableAlias = getSqlAlias(tableAlias);
        SqlColumnToEntityFieldMapping sqlColumnToEntityFieldMapping = null;
        if (clause != SqlClause.SELECT) {
            if (clause == SqlClause.INSERT || clause == SqlClause.VALUES || clause == SqlClause.UPDATE /* Postgres doesn't like alias in set clause */)
                tableAlias = null;
            String fullColumnName = columnName;
            if (tableAlias != null)
                fullColumnName = tableAlias + '.' + columnName;
            if (grouped && clause == SqlClause.ORDER_BY)
                fullColumnName = (isBoolean ? "first(" : "min(") + fullColumnName + ")"; // min is much faster (native) than first (written in sql) but min doesn't work for boolean in postgres
            prepareAppend(clause, separator).append(fullColumnName);
        } else {
            String fullColumnName = columnName;
            if (tableAlias != null)
                fullColumnName = tableAlias + '.' + columnName;
            sqlColumnToEntityFieldMapping = fullColumnNameToColumnMappings.get(fullColumnName);
            if (sqlColumnToEntityFieldMapping == null) {
                if (generateQueryMapping) {
                    fullColumnNameToColumnMappings.put(fullColumnName, sqlColumnToEntityFieldMapping = new SqlColumnToEntityFieldMapping(fullColumnNameToColumnMappings.size(), fieldId, foreignFieldClassId, leftJoinMapping));
                    columnMappings.add(sqlColumnToEntityFieldMapping);
                }
                if (grouped)
                    fullColumnName = (isBoolean ? "first(" : "min(") + fullColumnName + ")"; // min is much faster (native) than first (written in sql) but min doesn't work for boolean for postgres
                prepareAppend(clause, separator).append(fullColumnName);
            }
            if (sqlColumnToEntityFieldMapping != null && !generateQueryMapping /*&& !grouped*/) // always append column when not generateQueryMapping (ex: function call)
                prepareAppend(clause, separator).append(fullColumnName);
        }
        return sqlColumnToEntityFieldMapping;
    }

    public String addJoinCondition(String table1Alias, String column1Name, String table2Alias, String table2Name, String column2Name, boolean leftOuter) {
        table1Alias = getSqlAlias(table1Alias);
        Map<Join, Join> table1Joins = joins.get(table1Alias);
        if (table1Joins == null)
            joins.put(table1Alias, table1Joins =  new HashMap<>());
        Join join = new Join(table1Alias, column1Name, table2Name, column2Name, null, leftOuter);
        Join existingJoin = table1Joins.get(join); // fetching the map to see if the join already exists (see Join.equals())
        if (existingJoin != null) {
            join = existingJoin;
            join.leftOuter &= leftOuter;
        } else {
            join.table2Alias = getNewTableAlias(table2Name, null, true);
            table1Joins.put(join, join);
        }
        if (table2Alias != null && !table2Alias.equals(join.table2Alias)) {
            recordLogicalAlias(table2Alias, join.table2Alias);
            return table2Alias;
        }
        return join.table2Alias;
    }

    private static class Join {
        // Join identifying fields (to include in equals and hash)
        String table1Alias;
        String column1Name;
        String table2Name;
        String column2Name;

        // Join attributes (not to include in equals and hash)
        String table2Alias;
        boolean leftOuter;

        private Join(String table1Alias, String column1Name, String table2Name, String column2Name, String table2Alias, boolean leftOuter) {
            this.table1Alias = table1Alias;
            this.column1Name = column1Name;
            this.table2Name = table2Name;
            this.column2Name = column2Name;
            this.table2Alias = table2Alias;
            this.leftOuter = leftOuter;
        }

        void appendTo(StringBuilder sb) {
            if (leftOuter)
                sb.append(" left");
            sb.append(" join ").append(table2Name).append(' ').append(table2Alias);
            if (column1Name.equals(column2Name)) // 'using' syntax when column names are identical
                sb.append(" using ").append(column1Name);
            else { // 'on' syntax
                sb.append(" on ");
                if (table1Alias != null)
                    sb.append(table1Alias).append('.');
                sb.append(column1Name).append('=').append(table2Alias).append('.').append(column2Name);
            }
        }

        static void appendJoins(Map<Join, Join> joinMap, StringBuilder sb) {
            if (joinMap != null)
                for (Join join : joinMap.keySet())
                    join.appendTo(sb);
        }

        static void appendNotYetAppendJoins(Map<Join, Join> joinMap, StringBuilder sb) {
            if (joinMap != null)
                for (Join join : joinMap.keySet()) {
                    StringBuilder sb2 = new StringBuilder();
                    join.appendTo(sb2);
                    String s2 = sb2.toString();
                    if (sb.toString().indexOf(s2) == -1) // J2ME CLDC doesn't support contains()
                        sb.append(s2);
                }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Join join = (Join) o;

            if (!column1Name.equals(join.column1Name)) return false;
            if (!column2Name.equals(join.column2Name)) return false;
            if (!table1Alias.equals(join.table1Alias)) return false;
            if (!table2Name.equals(join.table2Name)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = table1Alias.hashCode();
            result = 31 * result + column1Name.hashCode();
            result = 31 * result + table2Name.hashCode();
            result = 31 * result + column2Name.hashCode();
            return result;
        }
    }


    // Some Strings static method helpers

    private static String _if(boolean condition, String s) {
        return condition && s != null ? s : "";
    }

    private static String _if(String before, StringBuilder s, String after, StringBuilder sb) {
        if (Strings.isNotEmpty(s)) {
            if (before != null)
                sb.append(before);
            sb.append(s);
            if (after != null)
                sb.append(after);
        }
        return "";
    }
}
