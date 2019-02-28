// This java module file was generated by WebFx

module mongoose.client.navigationarrows.java {

    // Direct dependencies modules
    requires webfx.framework.client.uirouter;
    requires webfx.framework.shared.router;

    // Exported packages
    exports mongoose.client.navigationarrows;

    // Provided services
    provides webfx.framework.client.operations.route.RouteRequestEmitter with mongoose.client.navigationarrows.RouteBackwardRequestEmitter, mongoose.client.navigationarrows.RouteForwardRequestEmitter;

}