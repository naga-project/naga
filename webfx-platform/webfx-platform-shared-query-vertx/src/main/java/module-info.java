// This java module file was generated by WebFx

module webfx.platform.shared.query.vertx {

    // Direct dependencies modules
    requires webfx.platform.providers.vertx.queryupdate;
    requires webfx.platform.shared.datasource;
    requires webfx.platform.shared.query;

    // Exported packages
    exports webfx.platform.shared.services.query.spi.impl.vertx;

    // Provided services
    provides webfx.platform.shared.services.query.spi.QueryServiceProvider with webfx.platform.shared.services.query.spi.impl.vertx.VertxQueryServiceProvider;

}