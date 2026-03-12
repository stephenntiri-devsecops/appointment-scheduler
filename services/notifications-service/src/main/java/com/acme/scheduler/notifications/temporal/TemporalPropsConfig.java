package com.acme.scheduler.notifications.temporal;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TemporalProperties.class)
public class TemporalPropsConfig {}
