package com.foacraft.cloudnet.tmux.services;

import com.foacraft.cloudnet.tmux.services.config.TmuxConfiguration;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.language.I18n;
import eu.cloudnetservice.driver.registry.Service;
import eu.cloudnetservice.driver.service.ServiceConfiguration;
import eu.cloudnetservice.node.config.Configuration;
import eu.cloudnetservice.node.impl.service.InternalCloudServiceManager;
import eu.cloudnetservice.node.impl.service.defaults.factory.BaseLocalCloudServiceFactory;
import eu.cloudnetservice.node.impl.service.defaults.log.ProcessServiceLogReadScheduler;
import eu.cloudnetservice.node.impl.tick.DefaultTickLoop;
import eu.cloudnetservice.node.impl.version.ServiceVersionProvider;
import eu.cloudnetservice.node.service.CloudService;
import eu.cloudnetservice.node.service.CloudServiceManager;
import eu.cloudnetservice.node.tick.TickLoop;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * cloudnet-tmux-services
 * com.foacraft.cloudnet.tmux.services.TmuxLocalCloudServiceFactory
 *
 * @author scorez
 * @since 12/11/23 23:51.
 */
@Singleton
public class TmuxLocalCloudServiceFactory extends BaseLocalCloudServiceFactory {

    protected final I18n i18n;
    protected final DefaultTickLoop mainThread;
    protected final EventManager eventManager;
    protected final CloudServiceManager cloudServiceManager;
    protected final ProcessServiceLogReadScheduler processLogReadScheduler;
    protected final TmuxConfiguration tmuxConfiguration;

    @Inject
    public TmuxLocalCloudServiceFactory(
        @NonNull @Service I18n i18n,
        @NonNull DefaultTickLoop tickLoop,
        @NonNull Configuration nodeConfig,
        @NonNull CloudServiceManager cloudServiceManager,
        @NonNull EventManager eventManager,
        @NonNull ServiceVersionProvider versionProvider,
        @NonNull ProcessServiceLogReadScheduler processLogReadScheduler,
        @NonNull TmuxConfiguration tmuxConfiguration
    ) {
        super(nodeConfig, versionProvider);
        this.i18n = i18n;
        this.mainThread = tickLoop;
        this.eventManager = eventManager;
        this.cloudServiceManager = cloudServiceManager;
        this.processLogReadScheduler = processLogReadScheduler;
        this.tmuxConfiguration = tmuxConfiguration;
    }

    @Override
    public @NonNull CloudService createCloudService(@NonNull CloudServiceManager manager, @NonNull ServiceConfiguration configuration) {
        // validates the settings of the configuration
        var config = this.validateConfiguration(manager, configuration);
        // select the configuration preparer for the environment
        var preparer = manager.servicePreparer(config.serviceId().environment());
        // create the service
        return new TmuxService(
            this.i18n,
            this.mainThread,
            this.configuration,
            config,
            (InternalCloudServiceManager)  manager,
            this.eventManager,
            this.versionProvider,
            preparer,
            this.processLogReadScheduler,
            tmuxConfiguration
        );
    }

    @Override
    public @NonNull String name() {
        return tmuxConfiguration.factoryName();
    }
}
