// This java module file was generated by WebFx

module mongoose.frontend.activities.program {

    // Direct dependencies modules
    requires javafx.graphics;
    requires mongoose.client.bookingcalendar;
    requires mongoose.client.bookingprocess;
    requires mongoose.client.businesslogic;
    requires mongoose.client.icons;
    requires mongoose.client.sectionpanel;
    requires mongoose.frontend.activities.program.routing;
    requires webfx.framework.client.layouts;
    requires webfx.framework.client.uirouter;
    requires webfx.framework.shared.domain;
    requires webfx.platform.shared.log;

    // Implicit
    requires webfx.platform.shared.util;
    requires webfx.framework.client.activity;

    // Exported packages
    exports mongoose.frontend.activities.program;

    // Provided services
    provides webfx.framework.client.ui.uirouter.UiRoute with mongoose.frontend.activities.program.ProgramUiRoute;

}