package com.foacraft.cloudnet.tmux.services;

import com.foacraft.cloudnet.tmux.services.config.TmuxConfiguration;
import eu.cloudnetservice.common.concurrent.Task;
import eu.cloudnetservice.common.util.StringUtil;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.service.ServiceConfiguration;
import eu.cloudnetservice.node.TickLoop;
import eu.cloudnetservice.node.config.Configuration;
import eu.cloudnetservice.node.event.service.CloudServicePostProcessStartEvent;
import eu.cloudnetservice.node.service.CloudServiceManager;
import eu.cloudnetservice.node.service.ServiceConfigurationPreparer;
import eu.cloudnetservice.node.service.defaults.JVMService;
import eu.cloudnetservice.node.version.ServiceVersionProvider;
import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * cloudnet-tmux-services
 * com.foacraft.cloudnet.tmux.services.TmuxService
 *
 * @author scorez
 * @since 12/11/23 23:54.
 */
public class TmuxService extends JVMService {

    protected final TmuxConfiguration tmuxConfiguration;
    protected volatile String sessionId;

    public TmuxService(
        @NonNull TickLoop tickLoop,
        @NonNull Configuration nodeConfig,
        @NonNull ServiceConfiguration configuration,
        @NonNull CloudServiceManager manager,
        @NonNull EventManager eventManager,
        @NonNull ServiceVersionProvider versionProvider,
        @NonNull ServiceConfigurationPreparer serviceConfigurationPreparer,
        @NonNull TmuxConfiguration tmuxConfiguration
    ) {
        super(tickLoop, nodeConfig, configuration, manager, eventManager, versionProvider, serviceConfigurationPreparer);
        this.tmuxConfiguration = tmuxConfiguration;
    }

    @Override
    public boolean alive() {
        if (sessionId != null) {
            ProcessBuilder processBuilder = new ProcessBuilder("tmux", "has-session", "-t", sessionId);
            try {
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                return exitCode == 0;
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public void runCommand(@NonNull String command) {
        try {
            new ProcessBuilder("tmux", "send-keys", "-t", sessionId, command, "C-m").start().waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doStartProcess(@NonNull List<String> arguments, @NonNull Path wrapperPath, @NonNull Path applicationFilePath) {
        try {
            this.sessionId = this.serviceId().name() + "(" + this.serviceId().uniqueId() + ")";
            var command = new ArrayList<String>();
            command.add("tmux");
            command.add("new-session");
            command.add("-Ad");
            command.add("-s");
            command.add(this.sessionId);
            command.addAll(arguments);

            // prepare the builder and apply the environment variables to it
            var builder = new ProcessBuilder(command).directory(this.serviceDirectory.toFile());
            for (var entry : this.serviceConfiguration().environmentVariables().entrySet()) {
                // there is no consensus forcing the key of an environment variable to be uppercase
                // however, docker rejects environment variables with a non-uppercase key, so to keep
                // consistency between service types we force the uppercase keys here as well
                builder.environment().put(StringUtil.toUpper(entry.getKey()), entry.getValue());
            }

            // start the process and fire the post start event
            this.process = builder.start();
            this.eventManager.callEvent(new CloudServicePostProcessStartEvent(this));
        } catch (IOException exception) {
            LOGGER.severe("Unable to start process in %s with command line %s",
                exception,
                this.serviceDirectory,
                String.join(" ", arguments));
        }
    }

    @Override
    protected void stopProcess() {
        if (sessionId == null) {
            return;
        }
        try {
            // exit the copy mode anyway.
            new ProcessBuilder("tmux", "send-keys", "-t", sessionId, "Escape").start().waitFor();
            new ProcessBuilder("tmux", "send-keys", "-t", sessionId, "C-c").start().waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Task.runAsync(() -> {
            if (this.alive()) {
                try {
                    new ProcessBuilder("tmux", "send-keys", "-t", sessionId, "Escape").start().waitFor();
                    new ProcessBuilder("tmux", "send-keys", "-t", sessionId, "C-c").start().waitFor();
                    Thread.sleep(3 * 1000);
                    new ProcessBuilder("tmux", "send-keys", "-t", sessionId, "Escape").start().waitFor();
                    new ProcessBuilder("tmux", "send-keys", "-t", sessionId, "C-c").start().waitFor();
                    Thread.sleep(1 * 1000);
                    new ProcessBuilder("tmux", "kill-session", "-t", sessionId).start().waitFor();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                LOGGER.severe(
                    tmuxConfiguration.messages().get("service-stop-timeout")
                        .replace("%service_uniqueid%", serviceId().uniqueId().toString())
                        .replace("%service_task%", serviceId().taskName())
                        .replace("%service_name%", sessionId)
                );
            }
        }, Task.delayedExecutor(10, TimeUnit.SECONDS));
    }

    @Override
    public @NonNull String runtime() {
        return tmuxConfiguration.factoryName();
    }
}
