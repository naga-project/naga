// Generated by WebFx

module webfx.extras.visual.controls.charts.peers.javafx {

    // Direct dependencies modules
    requires java.base;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.web;
    requires webfx.extras.type;
    requires webfx.extras.visual;
    requires webfx.extras.visual.controls.charts;
    requires webfx.extras.visual.controls.charts.peers.base;
    requires webfx.extras.visual.controls.charts.registry;
    requires webfx.kit.javafx;
    requires webfx.kit.javafxgraphics.peers;
    requires webfx.platform.shared.util;

    // Exported packages
    exports webfx.extras.visual.controls.charts.peers.javafx;
    exports webfx.extras.visual.controls.charts.registry.spi.impl.javafx;

    // Provided services
    provides webfx.extras.visual.controls.charts.registry.spi.VisualChartsRegistryProvider with webfx.extras.visual.controls.charts.registry.spi.impl.javafx.JavaFxVisualChartsRegistryProvider;

}