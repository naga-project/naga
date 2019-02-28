// This java module file was generated by WebFx

module webfx.platform.shared.query.java {

    // Direct dependencies modules
    requires webfx.platform.providers.java.queryupdate;
    requires webfx.platform.shared.datasource;
    requires webfx.platform.shared.query;

    // Exported packages
    exports webfx.platform.shared.services.query.spi.impl.java;

    // Provided services
    provides webfx.platform.shared.services.query.spi.QueryServiceProvider with webfx.platform.shared.services.query.spi.impl.java.JdbcQueryServiceProvider;

}