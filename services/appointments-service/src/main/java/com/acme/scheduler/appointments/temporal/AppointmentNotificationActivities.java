package com.acme.scheduler.appointments.temporal;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface AppointmentNotificationActivities {

    @ActivityMethod
    void sendAppointmentBooked(AppointmentNotificationInput input);
}
