// This java module file was generated by WebFx

module webfx.framework.shared.querypush {

    // Direct dependencies modules
    requires java.base;
    requires webfx.platform.shared.buscall;
    requires webfx.platform.shared.datasource;
    requires webfx.platform.shared.json;
    requires webfx.platform.shared.log;
    requires webfx.platform.shared.query;
    requires webfx.platform.shared.serial;
    requires webfx.platform.shared.util;

    // Exported packages
    exports webfx.framework.shared.services.querypush;
    exports webfx.framework.shared.services.querypush.diff;
    exports webfx.framework.shared.services.querypush.diff.impl;
    exports webfx.framework.shared.services.querypush.spi;
    exports webfx.framework.shared.services.querypush.spi.impl;

    // Used services
    uses webfx.framework.shared.services.querypush.spi.QueryPushServiceProvider;

    // Provided services
    provides webfx.platform.shared.services.buscall.spi.BusCallEndpoint with webfx.framework.shared.services.querypush.ExecuteQueryPushBusCallEndpoint;
    provides webfx.platform.shared.services.serial.spi.SerialCodec with webfx.framework.shared.services.querypush.QueryPushArgument.ProvidedSerialCodec, webfx.framework.shared.services.querypush.QueryPushResult.ProvidedSerialCodec, webfx.framework.shared.services.querypush.diff.impl.QueryResultTranslation.ProvidedSerialCodec;

}