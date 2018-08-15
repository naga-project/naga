package naga.framework.activity.combinations.domainpresentationlogic.impl;

import naga.framework.activity.domain.DomainActivityContext;
import naga.framework.activity.presentation.logic.impl.PresentationLogicActivityContextBase;
import naga.framework.orm.domainmodel.DataSourceModel;
import naga.framework.activity.ActivityContext;

/**
 * @author Bruno Salmon
 */
public final class DomainPresentationLogicActivityContextFinal<PM>
        extends PresentationLogicActivityContextBase<DomainPresentationLogicActivityContextFinal<PM>, PM>
        implements DomainActivityContext<DomainPresentationLogicActivityContextFinal<PM>> {

    public DomainPresentationLogicActivityContextFinal(ActivityContext parentContext) {
        super(parentContext, DomainPresentationLogicActivityContextFinal::new);
    }

    private DataSourceModel dataSourceModel;

    @Override
    public DomainPresentationLogicActivityContextFinal<PM> setDataSourceModel(DataSourceModel dataSourceModel) {
        this.dataSourceModel = dataSourceModel;
        return this;
    }

    @Override
    public DataSourceModel getDataSourceModel() {
        if (dataSourceModel != null)
            return dataSourceModel;
        ActivityContext parentContext = getParentContext();
        if (parentContext instanceof DomainActivityContext)
            return ((DomainActivityContext) parentContext).getDataSourceModel();
        return null;
    }
}
