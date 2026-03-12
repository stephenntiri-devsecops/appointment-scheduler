"use client";

import { useEffect, useMemo, useState } from "react";
import { useParams } from "next/navigation";
import { loadStripe } from "@stripe/stripe-js";
import { Elements, PaymentElement, useElements, useStripe } from "@stripe/react-stripe-js";

const apiBase = process.env.NEXT_PUBLIC_API_BASE || "http://10.0.0.114:8080";
const stripePromise = loadStripe(
  process.env.NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY || ""
);

function CheckoutForm() {
  const stripe = useStripe();
  const elements = useElements();
  const [message, setMessage] = useState("");

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!stripe || !elements) return;

    setMessage("Confirming payment...");

    const result = await stripe.confirmPayment({
      elements,
      redirect: "if_required",
      confirmParams: {
        return_url: window.location.href,
      },
    });

    if (result.error) {
      setMessage(`Payment failed: ${result.error.message}`);
      return;
    }

    setMessage("Payment submitted successfully.");
  }

  return (
    <form onSubmit={onSubmit} style={{ display: "grid", gap: 16 }}>
      <div style={styles.paymentBox}>
        <PaymentElement />
      </div>

      <button type="submit" disabled={!stripe} style={styles.primaryButton}>
        Confirm payment
      </button>

      {message ? <div style={styles.infoBox}>{message}</div> : null}

      <div style={styles.testCardBox}>
        <strong>Test card:</strong> 4242 4242 4242 4242
        <br />
        Any future date, any CVC, any ZIP
      </div>
    </form>
  );
}

export default function PayPage() {
  const params = useParams<{ appointmentId: string }>();
  const appointmentId = useMemo(() => params?.appointmentId ?? "", [params]);

  const [clientSecret, setClientSecret] = useState("");
  const [err, setErr] = useState("");

  useEffect(() => {
    if (!appointmentId) return;

    (async () => {
      setErr("");
      try {
        const res = await fetch(
          `${apiBase}/api/v1/appointments/${appointmentId}/payments/intent`,
          {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ amountCents: 5000, currency: "usd" }),
          }
        );

        const text = await res.text();
        const data = text ? JSON.parse(text) : null;

        if (!res.ok) {
          throw new Error(data?.message || `Intent failed: ${res.status}`);
        }

        const secret = data?.stripe?.clientSecret;
        if (!secret) throw new Error("No clientSecret returned by backend.");

        setClientSecret(secret);
      } catch (e: any) {
        setErr(e?.message || "Failed to create payment intent");
      }
    })();
  }, [appointmentId]);

  return (
    <main style={styles.page}>
      <div style={styles.shell}>
        <div style={styles.hero}>
          <div>
            <p style={styles.kicker}>Secure checkout</p>
            <h1 style={styles.title}>Complete your payment</h1>
            <p style={styles.subtitle}>
              Your appointment has been created. Enter payment details below to continue.
            </p>
          </div>
          <div style={styles.heroBadge}>Step 2 of 2</div>
        </div>

        <div style={styles.card}>
          <div style={styles.summaryBox}>
            <div>
              <div style={styles.summaryLabel}>Appointment ID</div>
              <div style={styles.summaryValue}>{appointmentId || "(missing)"}</div>
            </div>
            <div>
              <div style={styles.summaryLabel}>Amount</div>
              <div style={styles.summaryValue}>$50.00</div>
            </div>
          </div>

          {err ? <div style={styles.errorBox}>{err}</div> : null}
          {!clientSecret && !err ? <div style={styles.infoBox}>Preparing secure payment form...</div> : null}

          {clientSecret ? (
            <Elements stripe={stripePromise} options={{ clientSecret }}>
              <CheckoutForm />
            </Elements>
          ) : null}
        </div>
      </div>
    </main>
  );
}

const styles: Record<string, React.CSSProperties> = {
  page: {
    minHeight: "100vh",
    background:
      "linear-gradient(180deg, #f5f7fb 0%, #eef2ff 100%)",
    padding: "40px 16px",
    fontFamily: "Inter, Arial, sans-serif",
  },
  shell: {
    maxWidth: 840,
    margin: "0 auto",
  },
  hero: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "flex-start",
    gap: 16,
    marginBottom: 24,
    flexWrap: "wrap",
  },
  kicker: {
    margin: 0,
    color: "#4f46e5",
    fontWeight: 700,
    fontSize: 13,
    textTransform: "uppercase",
    letterSpacing: 0.8,
  },
  title: {
    margin: "8px 0 8px",
    fontSize: 36,
    lineHeight: 1.1,
    color: "#111827",
  },
  subtitle: {
    margin: 0,
    color: "#4b5563",
    maxWidth: 620,
    fontSize: 16,
  },
  heroBadge: {
    background: "#111827",
    color: "#fff",
    borderRadius: 999,
    padding: "10px 14px",
    fontWeight: 600,
    fontSize: 14,
  },
  card: {
    background: "#fff",
    borderRadius: 20,
    boxShadow: "0 20px 50px rgba(15, 23, 42, 0.08)",
    border: "1px solid #e5e7eb",
    padding: 28,
    display: "grid",
    gap: 20,
  },
  summaryBox: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))",
    gap: 16,
    background: "#f9fafb",
    border: "1px solid #e5e7eb",
    borderRadius: 16,
    padding: 18,
  },
  summaryLabel: {
    fontSize: 12,
    textTransform: "uppercase",
    color: "#6b7280",
    fontWeight: 700,
    letterSpacing: 0.6,
    marginBottom: 6,
  },
  summaryValue: {
    fontSize: 16,
    fontWeight: 700,
    color: "#111827",
    wordBreak: "break-word",
  },
  paymentBox: {
    border: "1px solid #e5e7eb",
    borderRadius: 16,
    padding: 18,
    background: "#fff",
  },
  primaryButton: {
    border: "none",
    borderRadius: 12,
    background: "#4f46e5",
    color: "#fff",
    padding: "13px 18px",
    fontWeight: 700,
    cursor: "pointer",
    fontSize: 15,
  },
  errorBox: {
    background: "#fef2f2",
    color: "#b91c1c",
    border: "1px solid #fecaca",
    borderRadius: 12,
    padding: 14,
    whiteSpace: "pre-wrap",
  },
  infoBox: {
    background: "#eef2ff",
    color: "#3730a3",
    border: "1px solid #c7d2fe",
    borderRadius: 12,
    padding: 14,
  },
  testCardBox: {
    background: "#f9fafb",
    color: "#374151",
    border: "1px solid #e5e7eb",
    borderRadius: 12,
    padding: 14,
    fontSize: 14,
    lineHeight: 1.5,
  },
};
