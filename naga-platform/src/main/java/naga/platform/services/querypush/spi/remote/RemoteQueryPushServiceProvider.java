package naga.platform.services.querypush.spi.remote;

import naga.platform.bus.call.BusCallServerActivity;
import naga.platform.bus.call.BusCallService;
import naga.platform.services.datasource.ConnectionDetails;
import naga.platform.services.datasource.LocalDataSourceRegistry;
import naga.platform.services.log.Logger;
import naga.platform.services.query.QueryResultSet;
import naga.platform.services.querypush.PulseArgument;
import naga.platform.services.querypush.QueryPushArgument;
import naga.platform.services.querypush.QueryPushService;
import naga.platform.services.querypush.spi.QueryPushServiceProvider;
import naga.util.async.Future;
import naga.util.function.Consumer;

import java.util.HashMap;
import java.util.Map;

import static naga.platform.services.querypush.spi.remote.LocalQueryPushServiceProviderRegistry.getLocalConnectedProvider;
import static naga.platform.services.querypush.spi.remote.LocalQueryPushServiceProviderRegistry.registerLocalConnectedProvider;

/**
 * @author Bruno Salmon
 */
public class RemoteQueryPushServiceProvider implements QueryPushServiceProvider {

    @Override
    public Future<Object> executeQueryPush(QueryPushArgument argument) {
        QueryPushServiceProvider localConnectedProvider = getOrCreateLocalConnectedProvider(argument.getDataSourceId());
        if (localConnectedProvider != null)
            return localConnectedProvider.executeQueryPush(argument);
        return executeRemoteQueryPush(argument);
    }

    @Override
    public void requestPulse(PulseArgument argument) {
        QueryPushServiceProvider localConnectedProvider = getOrCreateLocalConnectedProvider(argument.getDataSourceId());
        if (localConnectedProvider == null)
            throw new UnsupportedOperationException("requestPulse() shouldn't be called on this RemoteQueryPushServiceProvider");
        localConnectedProvider.requestPulse(argument);
    }

    protected QueryPushServiceProvider getOrCreateLocalConnectedProvider(Object dataSourceId) {
        QueryPushServiceProvider localConnectedProvider = getLocalConnectedProvider(dataSourceId);
        if (localConnectedProvider == null) {
            ConnectionDetails connectionDetails = LocalDataSourceRegistry.getLocalDataSourceConnectionDetails(dataSourceId);
            if (connectionDetails != null) {
                localConnectedProvider = createLocalConnectedProvider(connectionDetails);
                registerLocalConnectedProvider(dataSourceId, localConnectedProvider);
            }
        }
        return localConnectedProvider;
    }

    protected QueryPushServiceProvider createLocalConnectedProvider(ConnectionDetails connectionDetails) {
        throw new UnsupportedOperationException("This platform doesn't provide local QueryPushServiceProvider");
    }

    protected <T> Future<T> executeRemoteQueryPush(QueryPushArgument argument) {
        Future<T> call = BusCallService.call(BusCallServerActivity.QUERY_PUSH_SERVICE_ADDRESS, argument);
        if (argument.getQueryResultSetConsumer() != null)
            call = call.map(queryStreamId -> {
                queryResultConsumers.put(queryStreamId, argument.getQueryResultSetConsumer());
                return queryStreamId;
            });
        return call;
    }

    private final static Map<Object, Consumer<QueryResultSet>> queryResultConsumers = new HashMap<>();

    static {
        QueryPushService.registerQueryPushClientConsumer(qpr -> {
            Consumer<QueryResultSet> queryResultSetConsumer = queryResultConsumers.get(qpr.getQueryStreamId());
            if (queryResultSetConsumer != null)
                queryResultSetConsumer.accept(qpr.getQueryResultSet());
            else
                Logger.log("QueryPushResult received with undeclared queryStreamId = " + qpr.getQueryStreamId());
        });
    }
}
