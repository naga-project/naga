// This java module file was generated by WebFx

module mongoose.backend.activities.letters {

    // Direct dependencies modules
    requires mongoose.backend.activities.letter.routing;
    requires mongoose.backend.activities.letters.routing;
    requires mongoose.client.activity;
    requires webfx.framework.client.uifilter;
    requires webfx.framework.client.uirouter;
    requires webfx.framework.shared.domain;
    requires webfx.framework.shared.router;

    // Implicit
    requires webfx.platform.shared.util;
    requires webfx.framework.client.activity;
    requires webfx.framework.shared.entity;

    // Exported packages
    exports mongoose.backend.activities.letters;

    // Provided services
    provides webfx.framework.client.operations.route.RouteRequestEmitter with mongoose.backend.activities.letters.RouteToLettersRequestEmitter;
    provides webfx.framework.client.ui.uirouter.UiRoute with mongoose.backend.activities.letters.LettersUiRoute;

}