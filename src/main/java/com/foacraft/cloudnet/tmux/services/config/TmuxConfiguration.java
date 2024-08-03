package com.foacraft.cloudnet.tmux.services.config;

import com.google.common.collect.ImmutableMap;
import lombok.NonNull;

import java.util.Map;

/**
 * CloudNet-Tmux-Services
 * com.foacraft.cloudnet.tmux.services.config.TmuxConfiguration
 *
 * @author scorez
 * @since 8/3/24 11:08.
 */
public record TmuxConfiguration(
    @NonNull String factoryName,
    @NonNull int stopTimeout,
    @NonNull Map<String, String> messages
) {

    public static final Map<String, String> DEFAULT_MESSAGES = ImmutableMap.of(
        "service-stop-timeout", "CloudService [uniqueId=%service_uniqueid% task=%service_task% name=%service_name%] will be killed cause timeout right now."
    );

}
