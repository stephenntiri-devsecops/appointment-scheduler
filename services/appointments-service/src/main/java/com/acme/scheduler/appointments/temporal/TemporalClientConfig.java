package com.acme.scheduler.appointments.temporal;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalClientConfig {

    @Bean
    public WorkflowServiceStubs workflowServiceStubs(@Value("${temporal.target:localhost:7233}") String target) {
        return WorkflowServiceStubs.newServiceStubs(
                WorkflowServiceStubsOptions.newBuilder()
                        .setTarget(target)
                        .build()
        );
    }

    @Bean
    public WorkflowClient workflowClient(
            WorkflowServiceStubs stubs,
            @Value("${temporal.namespace:default}") String namespace
    ) {
        return WorkflowClient.newInstance(
                stubs,
                WorkflowClientOptions.newBuilder()
                        .setNamespace(namespace)
                        .build()
        );
    }
}
