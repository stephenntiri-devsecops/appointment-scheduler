package com.acme.scheduler.appointments.api;

import com.acme.scheduler.appointments.payments.StripePaymentsService;
import com.acme.scheduler.appointments.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
public class PaymentsController {

  private final AppointmentService appointmentService;
  private final StripePaymentsService stripe;

  public PaymentsController(AppointmentService appointmentService, StripePaymentsService stripe) {
    this.appointmentService = appointmentService;
    this.stripe = stripe;
  }

  public record CreateIntentRequest(long amountCents, String currency) {}

  @PostMapping("/{id}/payments/intent")
  public ResponseEntity<?> createIntent(@PathVariable UUID id, @RequestBody CreateIntentRequest req) throws Exception {
    var appt = appointmentService.getOrThrow(id);

    String currency = (req.currency() == null || req.currency().isBlank()) ? "usd" : req.currency().toLowerCase();
    long amount = req.amountCents();

    if (amount <= 0) {
      return ResponseEntity.badRequest().body(Map.of(
          "error", "Bad Request",
          "message", "amountCents must be > 0",
          "status", 400
      ));
    }

    var pi = stripe.createPaymentIntent(appt.id.toString(), amount, currency, appt.contactEmail);
    return ResponseEntity.ok(Map.of(
        "appointmentId", appt.id.toString(),
        "stripe", StripePaymentsService.response(pi)
    ));
  }
}
