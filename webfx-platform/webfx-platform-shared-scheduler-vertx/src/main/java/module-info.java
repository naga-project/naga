// This java module file was generated by WebFx

module webfx.platform.shared.scheduler.vertx {

    // Direct dependencies modules
    requires vertx.core;
    requires webfx.platform.providers.vertx.instance;
    requires webfx.platform.shared.scheduler;

    // Exported packages
    exports webfx.platform.shared.services.scheduler.spi.impl.vertx;

    // Provided services
    provides webfx.platform.shared.services.scheduler.spi.SchedulerProvider with webfx.platform.shared.services.scheduler.spi.impl.vertx.VertxSchedulerProvider;

}