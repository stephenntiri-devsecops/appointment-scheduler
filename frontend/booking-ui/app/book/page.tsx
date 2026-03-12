"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

const apiBase = process.env.NEXT_PUBLIC_API_BASE || "http://10.0.0.114:8080";

type Contact = {
  email: string;
  phone: string;
};

type FormState = {
  tenantId: string;
  clinicId: string;
  providerId: string;
  patientId: string;
  startTime: string;
  endTime: string;
  timeZone: string;
  contact: Contact;
};

function getDefaultTimes() {
  const start = new Date();
  start.setMinutes(start.getMinutes() + 60);
  start.setSeconds(0);
  start.setMilliseconds(0);

  const end = new Date(start.getTime() + 30 * 60 * 1000);

  return {
    startTime: start.toISOString().slice(0, 16),
    endTime: end.toISOString().slice(0, 16),
  };
}

export default function BookPage() {
  const router = useRouter();
  const times = getDefaultTimes();

  const [form, setForm] = useState<FormState>({
    tenantId: "t-001",
    clinicId: "c-001",
    providerId: "p-001",
    patientId: "pt-001",
    startTime: times.startTime,
    endTime: times.endTime,
    timeZone: "America/New_York",
    contact: {
      email: "patient@example.com",
      phone: "+12025550123",
    },
  });

  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");
  const [result, setResult] = useState<{ id: string; status: string } | null>(null);

  function updateField<K extends keyof FormState>(key: K, value: FormState[K]) {
    setForm((prev) => ({ ...prev, [key]: value }));
  }

  function updateContact<K extends keyof Contact>(key: K, value: Contact[K]) {
    setForm((prev) => ({
      ...prev,
      contact: {
        ...prev.contact,
        [key]: value,
      },
    }));
  }

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    setErr("");
    setResult(null);

    try {
      const payload = {
        ...form,
        startTime: new Date(form.startTime).toISOString(),
        endTime: new Date(form.endTime).toISOString(),
      };

      const res = await fetch(`${apiBase}/api/v1/appointments`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      const text = await res.text();
      let data: any = null;

      try {
        data = text ? JSON.parse(text) : null;
      } catch {
        data = text;
      }

      if (!res.ok) {
        const message =
          typeof data === "object" && data?.message ? data.message : text;

        if (message.includes("uq_provider_start")) {
          throw new Error(
            "This provider is already booked for that time. Please choose another time."
          );
        }

        throw new Error(message || `Request failed: ${res.status}`);
      }

      const id = data?.id;
      const status = data?.status ?? "UNKNOWN";

      if (!id) {
        throw new Error("Appointment created but no id was returned.");
      }

      setResult({ id, status });
      router.push(`/pay/${id}`);
    } catch (error: any) {
      setErr(error?.message ?? "Failed to create appointment");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main style={styles.page}>
      <div style={styles.shell}>
        <div style={styles.hero}>
          <div>
            <p style={styles.kicker}>B2B SaaS Appointment Scheduler</p>
            <h1 style={styles.title}>Book an appointment</h1>
            <p style={styles.subtitle}>
              Create the appointment first, then continue to secure payment.
            </p>
          </div>
          <div style={styles.heroBadge}>Step 1 of 2</div>
        </div>

        <div style={styles.card}>
          <form onSubmit={onSubmit} style={styles.form}>
            <section>
              <h2 style={styles.sectionTitle}>Booking details</h2>
              <div style={styles.grid2}>
                <Field
                  label="Tenant ID"
                  value={form.tenantId}
                  onChange={(v) => updateField("tenantId", v)}
                />
                <Field
                  label="Clinic ID"
                  value={form.clinicId}
                  onChange={(v) => updateField("clinicId", v)}
                />
                <Field
                  label="Provider ID"
                  value={form.providerId}
                  onChange={(v) => updateField("providerId", v)}
                />
                <Field
                  label="Patient ID"
                  value={form.patientId}
                  onChange={(v) => updateField("patientId", v)}
                />
              </div>
            </section>

            <section>
              <h2 style={styles.sectionTitle}>Time slot</h2>
              <div style={styles.grid2}>
                <Field
                  label="Start time"
                  type="datetime-local"
                  value={form.startTime}
                  onChange={(v) => updateField("startTime", v)}
                />
                <Field
                  label="End time"
                  type="datetime-local"
                  value={form.endTime}
                  onChange={(v) => updateField("endTime", v)}
                />
              </div>
              <div style={{ marginTop: 16 }}>
                <Field
                  label="Time zone"
                  value={form.timeZone}
                  onChange={(v) => updateField("timeZone", v)}
                />
              </div>
            </section>

            <section>
              <h2 style={styles.sectionTitle}>Patient contact</h2>
              <div style={styles.grid2}>
                <Field
                  label="Email"
                  type="email"
                  value={form.contact.email}
                  onChange={(v) => updateContact("email", v)}
                />
                <Field
                  label="Phone"
                  value={form.contact.phone}
                  onChange={(v) => updateContact("phone", v)}
                />
              </div>
            </section>

            {err ? <div style={styles.errorBox}>{err}</div> : null}

            {result ? (
              <div style={styles.successBox}>
                Appointment created. Redirecting to payment...
                <br />
                <strong>ID:</strong> {result.id}
              </div>
            ) : null}

            <div style={styles.actions}>
              <button type="submit" disabled={loading} style={styles.primaryButton}>
                {loading ? "Booking..." : "Book appointment"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </main>
  );
}

function Field({
  label,
  value,
  onChange,
  type = "text",
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  type?: string;
}) {
  return (
    <label style={styles.labelWrap}>
      <span style={styles.label}>{label}</span>
      <input
        type={type}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        style={styles.input}
      />
    </label>
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
    maxWidth: 920,
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
  },
  form: {
    display: "grid",
    gap: 28,
  },
  sectionTitle: {
    margin: "0 0 14px",
    fontSize: 18,
    color: "#111827",
  },
  grid2: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
    gap: 16,
  },
  labelWrap: {
    display: "grid",
    gap: 8,
  },
  label: {
    fontSize: 14,
    fontWeight: 600,
    color: "#374151",
  },
  input: {
    width: "100%",
    padding: "12px 14px",
    borderRadius: 12,
    border: "1px solid #d1d5db",
    outline: "none",
    fontSize: 15,
    background: "#fff",
    boxSizing: "border-box",
  },
  actions: {
    display: "flex",
    justifyContent: "flex-end",
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
  successBox: {
    background: "#ecfdf5",
    color: "#065f46",
    border: "1px solid #a7f3d0",
    borderRadius: 12,
    padding: 14,
  },
};
