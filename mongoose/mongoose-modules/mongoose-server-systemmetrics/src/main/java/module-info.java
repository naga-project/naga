// This java module file was generated by WebFx

module mongoose.server.systemmetrics {

    // Direct dependencies modules
    requires java.base;
    requires mongoose.shared.domain;
    requires mongoose.shared.entities;
    requires webfx.framework.shared.domain;
    requires webfx.framework.shared.entity;
    requires webfx.platform.shared.appcontainer;
    requires webfx.platform.shared.log;
    requires webfx.platform.shared.scheduler;
    requires webfx.platform.shared.update;
    requires webfx.platform.shared.util;

    // Exported packages
    exports mongoose.server.jobs.systemmetrics;
    exports mongoose.server.services.systemmetrics;
    exports mongoose.server.services.systemmetrics.spi;

    // Used services
    uses mongoose.server.services.systemmetrics.spi.SystemMetricsServiceProvider;

    // Provided services
    provides webfx.platform.shared.services.appcontainer.spi.ApplicationJob with mongoose.server.jobs.systemmetrics.SystemMetricsRecorderJob;

}