// Generated by WebFx

module mongoose.backend.activities.letter {

    // Direct dependencies modules
    requires javafx.base;
    requires javafx.graphics;
    requires mongoose.backend.multilangeditor;
    requires mongoose.client.util;
    requires webfx.framework.client.activity;
    requires webfx.framework.client.controls;
    requires webfx.framework.client.domain;
    requires webfx.framework.client.uirouter;
    requires webfx.kit.util;
    requires webfx.platform.client.windowhistory;
    requires webfx.platform.shared.util;

    // Exported packages
    exports mongoose.backend.activities.letter;
    exports mongoose.backend.activities.letter.routing;
    exports mongoose.backend.operations.routes.letter;

    // Provided services
    provides webfx.framework.client.ui.uirouter.UiRoute with mongoose.backend.activities.letter.LetterUiRoute;

}