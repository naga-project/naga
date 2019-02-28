// This java module file was generated by WebFx

module mongoose.backend.activities.bookings {

    // Direct dependencies modules
    requires java.base;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires mongoose.backend.activities.bookings.routing;
    requires mongoose.backend.activities.cloneevent.routing;
    requires mongoose.client.activity;
    requires mongoose.client.businesslogic;
    requires mongoose.frontend.activities.options.routing;
    requires mongoose.shared.domain;
    requires mongoose.shared.entities;
    requires webfx.framework.client.action;
    requires webfx.framework.client.layouts;
    requires webfx.framework.client.uifilter;
    requires webfx.framework.client.uirouter;
    requires webfx.framework.shared.domain;
    requires webfx.framework.shared.expression;
    requires webfx.framework.shared.router;
    requires webfx.platform.shared.json;
    requires webfx.platform.shared.log;
    requires webfx.platform.shared.util;

    // Implicit
    requires webfx.framework.client.activity;

    // Exported packages
    exports mongoose.backend.activities.bookings;

    // Provided services
    provides webfx.framework.client.operations.route.RouteRequestEmitter with mongoose.backend.activities.bookings.RouteToBookingsRequestEmitter;
    provides webfx.framework.client.ui.uirouter.UiRoute with mongoose.backend.activities.bookings.BookingsUiRoute;

}