// This java module file was generated by WebFx

module webfx.tutorial.helloworld.application {

    // Direct dependencies modules
    requires javafx.controls;
    requires javafx.graphics;

    // Exported packages
    exports webfx.tutorial.helloworld;

    // Provided services
    provides javafx.application.Application with webfx.tutorial.helloworld.HelloWorldApplication;

}