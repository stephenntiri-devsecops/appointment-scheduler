package com.acme.scheduler.notifications.temporal;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "temporal")
public record TemporalProperties(
        boolean enabled,
        String target,
        String namespace,
        String taskQueue
) {}
