package webfx.platforms.core.services.appcontainer.spi;

import webfx.platforms.core.services.appcontainer.spi.impl.ApplicationModuleInitializerManager;
import webfx.platforms.core.services.appcontainer.spi.impl.SimpleApplicationJobManager;
import webfx.platforms.core.services.shutdown.Shutdown;

/**
 * @author Bruno Salmon
 */
public interface ApplicationContainerProvider {

    default void initialize() {
        ApplicationModuleInitializerManager.initialize();
        Shutdown.addShutdownHook(SimpleApplicationJobManager::shutdown);
        Shutdown.addShutdownHook(ApplicationModuleInitializerManager::shutdown);
    }

    default void startApplicationJob(ApplicationJob applicationJob) {
        SimpleApplicationJobManager.startApplicationJob(applicationJob);
    }

    default void stopApplicationJob(ApplicationJob applicationJob) {
        SimpleApplicationJobManager.stopApplicationJob(applicationJob);
    }

}
