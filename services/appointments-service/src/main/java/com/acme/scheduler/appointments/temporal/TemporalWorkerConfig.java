package com.acme.scheduler.appointments.temporal;

import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalWorkerConfig {

    private final WorkflowClient workflowClient;
    private final AppointmentNotificationActivitiesImpl activitiesImpl;
    private final String taskQueue;

    public TemporalWorkerConfig(
            WorkflowClient workflowClient,
            AppointmentNotificationActivitiesImpl activitiesImpl,
            @Value("${app.temporal.taskQueue}") String taskQueue
    ) {
        this.workflowClient = workflowClient;
        this.activitiesImpl = activitiesImpl;
        this.taskQueue = taskQueue;
    }

    @Bean
    public WorkerFactory workerFactory() {
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);

        Worker worker = factory.newWorker(taskQueue);
        worker.registerWorkflowImplementationTypes(AppointmentNotificationWorkflowImpl.class);
        worker.registerActivitiesImplementations(activitiesImpl);

        factory.start();
        return factory;
    }
}
