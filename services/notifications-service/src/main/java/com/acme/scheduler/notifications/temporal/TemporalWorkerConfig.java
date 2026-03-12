package com.acme.scheduler.notifications.temporal;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.WorkerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "temporal", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TemporalWorkerConfig {

    @Bean
    public WorkflowServiceStubs workflowServiceStubs(TemporalProperties props) {
        return WorkflowServiceStubs.newServiceStubs(
                WorkflowServiceStubsOptions.newBuilder()
                        .setTarget(props.target())
                        .build()
        );
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs stubs, TemporalProperties props) {
        return WorkflowClient.newInstance(
                stubs,
                WorkflowClientOptions.newBuilder()
                        .setNamespace(props.namespace())
                        .build()
        );
    }

    @Bean
    public WorkerFactory workerFactory(WorkflowClient client) {
        WorkerFactory factory = WorkerFactory.newInstance(client);
        // NOTE: if you register workers here, do it the same way you already do.
        // factory.newWorker(props.taskQueue())...
        factory.start();
        return factory;
    }
}
