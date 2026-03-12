package com.acme.scheduler.appointments.idempotency;

public record IdempotencyResult(int statusCode, String responseBody) {}
