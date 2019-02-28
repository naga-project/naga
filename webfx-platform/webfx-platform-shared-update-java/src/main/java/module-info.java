// This java module file was generated by WebFx

module webfx.platform.shared.update.java {

    // Direct dependencies modules
    requires webfx.platform.providers.java.queryupdate;
    requires webfx.platform.shared.datasource;
    requires webfx.platform.shared.update;

    // Exported packages
    exports webfx.platform.shared.services.update.spi.impl.java;

    // Provided services
    provides webfx.platform.shared.services.update.spi.UpdateServiceProvider with webfx.platform.shared.services.update.spi.impl.java.JdbcUpdateServiceProvider;

}