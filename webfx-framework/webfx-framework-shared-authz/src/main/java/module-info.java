// This java module file was generated by WebFx

module webfx.framework.shared.authz {

    // Direct dependencies modules
    requires java.base;
    requires javafx.base;
    requires webfx.framework.shared.operation;
    requires webfx.platform.client.uischeduler;
    requires webfx.platform.shared.util;

    // Exported packages
    exports webfx.framework.shared.operation.authz;
    exports webfx.framework.shared.services.authz;
    exports webfx.framework.shared.services.authz.mixin;
    exports webfx.framework.shared.services.authz.spi;
    exports webfx.framework.shared.services.authz.spi.impl;
    exports webfx.framework.shared.services.authz.spi.impl.inmemory;
    exports webfx.framework.shared.services.authz.spi.impl.inmemory.parser;

    // Used services
    uses webfx.framework.shared.services.authz.spi.AuthorizationServiceProvider;

}