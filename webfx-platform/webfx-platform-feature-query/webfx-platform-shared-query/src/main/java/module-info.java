// Generated by WebFx

module webfx.platform.shared.query {

    // Direct dependencies modules
    requires java.base;
    requires webfx.platform.shared.buscall;
    requires webfx.platform.shared.datascope;
    requires webfx.platform.shared.datasource;
    requires webfx.platform.shared.json;
    requires webfx.platform.shared.log;
    requires webfx.platform.shared.serial;
    requires webfx.platform.shared.util;

    // Exported packages
    exports webfx.platform.shared.services.query;
    exports webfx.platform.shared.services.query.compression;
    exports webfx.platform.shared.services.query.compression.repeat;
    exports webfx.platform.shared.services.query.spi;
    exports webfx.platform.shared.services.query.spi.impl;

    // Used services
    uses webfx.platform.shared.services.query.spi.QueryServiceProvider;

    // Provided services
    provides webfx.platform.shared.services.buscall.spi.BusCallEndpoint with webfx.platform.shared.services.query.ExecuteQueryBusCallEndpoint, webfx.platform.shared.services.query.ExecuteQueryBatchBusCallEndpoint;
    provides webfx.platform.shared.services.serial.spi.SerialCodec with webfx.platform.shared.services.query.QueryArgument.ProvidedSerialCodec, webfx.platform.shared.services.query.QueryResult.ProvidedSerialCodec;

}