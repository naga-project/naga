// This java module file was generated by WebFx

module webfx.platform.shared.shutdown.java {

    // Direct dependencies modules
    requires webfx.platform.shared.shutdown;

    // Exported packages
    exports webfx.platform.shared.services.shutdown.spi.impl.java;

    // Provided services
    provides webfx.platform.shared.services.shutdown.spi.ShutdownProvider with webfx.platform.shared.services.shutdown.spi.impl.java.JavaShutdownProvider;

}