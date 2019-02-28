// This java module file was generated by WebFx

module mongoose.client.sessionrecorder {

    // Direct dependencies modules
    requires java.base;
    requires mongoose.client.authn;
    requires mongoose.shared.domain;
    requires webfx.framework.client.push;
    requires webfx.framework.client.uirouter;
    requires webfx.framework.shared.entity;
    requires webfx.fxkit.launcher;
    requires webfx.platform.client.storage;
    requires webfx.platform.shared.appcontainer;
    requires webfx.platform.shared.bus;
    requires webfx.platform.shared.log;

    // Implicit
    requires javafx.base;
    requires webfx.platform.shared.update;
    requires  webfx.platform.shared.util;

    // Exported packages
    exports mongoose.client.jobs.sessionrecorder;

    // Provided services
    provides webfx.platform.shared.services.appcontainer.spi.ApplicationJob with mongoose.client.jobs.sessionrecorder.ClientSessionRecorderJob;

}