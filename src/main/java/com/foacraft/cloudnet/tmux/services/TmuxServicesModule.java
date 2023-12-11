package com.foacraft.cloudnet.tmux.services;

import dev.derklaro.aerogel.Element;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.cloudnetservice.node.service.CloudServiceManager;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;

/**
 * cloudnet-tmux-services
 * com.foacraft.cloudnet.tmux.services.TmuxServicesModule
 *
 * @author scorez
 * @since 12/11/23 23:48.
 */
@Singleton
public class TmuxServicesModule extends DriverModule {

    @ModuleTask(order = 22)
    public void registerServiceFactory(
        @NonNull CloudServiceManager serviceManager,
        @NonNull @Named("module") InjectionLayer<?> moduleInjectionLayer
    ) {
        // construct the factory instance & register it in the service manager
        var factory = moduleInjectionLayer.instance(TmuxLocalCloudServiceFactory.class);
        serviceManager.addCloudServiceFactory("tmux-jvm", factory);
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STOPPED)
    public void unregisterServiceFactory(@NonNull CloudServiceManager cloudServiceManager) {
        cloudServiceManager.removeCloudServiceFactory("tmux-jvm");
    }
}
