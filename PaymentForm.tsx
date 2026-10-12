import { useState, type FormEvent } from "react";
import { Loader2 } from "lucide-react";
import type { PaymentInput, PaymentMethod } from "../types";
import { formatINR, isMoney, METHOD_LABELS, todayISO } from "../utils/format";
import { Field } from "./ui";

export default function PaymentForm({
  outstanding,
  onSubmit,
  onCancel,
}: {
  outstanding: string;
  onSubmit: (data: PaymentInput) => Promise<void>;
  onCancel: () => void;
}) {
  const [form, setForm] = useState<PaymentInput>({
    amount: "",
    payment_date: todayISO(),
    method: "UPI",
    reference: "",
    notes: "",
  });
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);

  const set = (k: keyof PaymentInput) => (e: { target: { value: string } }) =>
    setForm((f) => ({ ...f, [k]: e.target.value }));

  async function submit(e: FormEvent) {
    e.preventDefault();
    const amt = form.amount.trim();
    // Quick checks for a friendly message; the backend checks again and is the final authority.
    if (!isMoney(amt) || Number(amt) <= 0) return setError("Enter an amount greater than zero (max 2 decimals)");
    if (Number(amt) > Number(outstanding)) return setError(`Amount cannot exceed the outstanding ${formatINR(outstanding)}`);
    if (!form.payment_date) return setError("Payment date is required");
    setError("");
    setSaving(true);
    try {
      await onSubmit({ ...form, amount: amt });
    } finally {
      setSaving(false);
    }
  }

  return (
    <form onSubmit={submit} className="space-y-4" noValidate>
      <div className="rounded-lg bg-indigo-50 p-3 text-sm text-indigo-900">
        Outstanding: <span className="font-semibold">{formatINR(outstanding)}</span>
      </div>
      <Field label="Amount (₹) *" error={error}>
        <div className="flex gap-2">
          <input className={`input ${error ? "input-error" : ""}`} inputMode="decimal" value={form.amount} onChange={set("amount")} placeholder="0.00" autoFocus />
          <button type="button" className="btn-secondary whitespace-nowrap" onClick={() => setForm((f) => ({ ...f, amount: String(Number(outstanding)) }))}>
            Full amount
          </button>
        </div>
      </Field>
      <div className="grid gap-4 sm:grid-cols-2">
        <Field label="Payment date *">
          <input className="input" type="date" value={form.payment_date} onChange={set("payment_date")} max={todayISO()} />
        </Field>
        <Field label="Method *">
          <select className="input" value={form.method} onChange={(e) => setForm((f) => ({ ...f, method: e.target.value as PaymentMethod }))}>
            {Object.entries(METHOD_LABELS).map(([v, l]) => (
              <option key={v} value={v}>
                {l}
              </option>
            ))}
          </select>
        </Field>
      </div>
      <Field label="Reference" hint="UTR / cheque no. / transaction ID">
        <input className="input" value={form.reference} onChange={set("reference")} />
      </Field>
      <Field label="Notes">
        <textarea className="input" rows={2} value={form.notes} onChange={set("notes")} />
      </Field>
      <div className="flex justify-end gap-2 pt-2">
        <button type="button" className="btn-secondary" onClick={onCancel}>
          Cancel
        </button>
        <button type="submit" className="btn-primary" disabled={saving}>
          {saving && <Loader2 className="h-4 w-4 animate-spin" />}
          Record payment
        </button>
      </div>
    </form>
  );
}
