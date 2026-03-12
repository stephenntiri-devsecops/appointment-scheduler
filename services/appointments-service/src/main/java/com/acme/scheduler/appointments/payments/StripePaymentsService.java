package com.acme.scheduler.appointments.payments;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripePaymentsService {

  public StripePaymentsService(@Value("${stripe.api-key:}") String apiKey) {
    if (apiKey == null || apiKey.isBlank()) {
      throw new IllegalStateException("stripe.api-key is missing. Set STRIPE_API_KEY env var (or stripe.api-key in application.yml).");
    }
    Stripe.apiKey = apiKey;
  }

  public PaymentIntent createPaymentIntent(String appointmentId, long amountCents, String currency, String customerEmail) throws Exception {
    if (amountCents <= 0) throw new IllegalArgumentException("amountCents must be > 0");
    if (currency == null || currency.isBlank()) throw new IllegalArgumentException("currency is required");

    PaymentIntentCreateParams.Builder builder = PaymentIntentCreateParams.builder()
        .setAmount(amountCents)
        .setCurrency(currency.toLowerCase())
        .setAutomaticPaymentMethods(
            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                .setEnabled(true)
                .build()
        );

    if (customerEmail != null && !customerEmail.isBlank()) {
      builder.setReceiptEmail(customerEmail);
    }

    // Helpful metadata
    builder.putMetadata("appointmentId", appointmentId);

    return PaymentIntent.create(builder.build());
  }

  public static Map<String, Object> response(PaymentIntent pi) {
    Map<String, Object> m = new HashMap<>();
    m.put("id", pi.getId());
    m.put("clientSecret", pi.getClientSecret());
    m.put("status", pi.getStatus());
    m.put("amount", pi.getAmount());
    m.put("currency", pi.getCurrency());
    return m;
  }
}
