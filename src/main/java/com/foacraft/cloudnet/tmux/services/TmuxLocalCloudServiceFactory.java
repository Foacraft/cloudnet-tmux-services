package com.foacraft.cloudnet.tmux.services;

import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.service.ServiceConfiguration;
import eu.cloudnetservice.node.TickLoop;
import eu.cloudnetservice.node.config.Configuration;
import eu.cloudnetservice.node.service.CloudService;
import eu.cloudnetservice.node.service.CloudServiceManager;
import eu.cloudnetservice.node.service.defaults.factory.BaseLocalCloudServiceFactory;
import eu.cloudnetservice.node.version.ServiceVersionProvider;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;

/**
 * cloudnet-tmux-services
 * com.foacraft.cloudnet.tmux.services.TmuxLocalCloudServiceFactory
 *
 * @author scorez
 * @since 12/11/23 23:51.
 */
@Singleton
public class TmuxLocalCloudServiceFactory extends BaseLocalCloudServiceFactory {

    protected final TickLoop mainThread;
    protected final EventManager eventManager;
    protected final CloudServiceManager cloudServiceManager;

    @Inject
    public TmuxLocalCloudServiceFactory(
        @NonNull TickLoop tickLoop,
        @NonNull Configuration nodeConfig,
        @NonNull CloudServiceManager cloudServiceManager,
        @NonNull EventManager eventManager,
        @NonNull ServiceVersionProvider versionProvider
    ) {
        super(nodeConfig, versionProvider);
        this.mainThread = tickLoop;
        this.eventManager = eventManager;
        this.cloudServiceManager = cloudServiceManager;
    }

    @Override
    public @NonNull CloudService createCloudService(@NonNull CloudServiceManager manager, @NonNull ServiceConfiguration configuration) {
        // validates the settings of the configuration
        var config = this.validateConfiguration(manager, configuration);
        // select the configuration preparer for the environment
        var preparer = manager.servicePreparer(config.serviceId().environment());
        // create the service
        return new TmuxService(
            this.mainThread,
            this.configuration,
            config,
            manager,
            this.eventManager,
            this.versionProvider,
            preparer
        );
    }

    @Override
    public @NonNull String name() {
        return "tmux-jvm";
    }
}
