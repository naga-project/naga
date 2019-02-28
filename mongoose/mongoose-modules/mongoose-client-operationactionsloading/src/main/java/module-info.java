// This java module file was generated by WebFx

module mongoose.client.operationactionsloading {

    // Direct dependencies modules
    requires mongoose.shared.domain;
    requires webfx.framework.client.action;
    requires webfx.framework.client.uirouter;
    requires webfx.framework.shared.domain;
    requires webfx.framework.shared.entity;
    requires webfx.platform.shared.appcontainer;
    requires webfx.platform.shared.log;

    // Implicit
    requires  webfx.platform.shared.util;

    // Exported packages
    exports mongoose.client.operationactionsloading;

    // Provided services
    provides webfx.platform.shared.services.appcontainer.spi.ApplicationModuleInitializer with mongoose.client.operationactionsloading.OperationActionsLoadingModuleInitializer;

}