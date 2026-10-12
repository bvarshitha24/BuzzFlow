import { useState, type FormEvent } from "react";
import { Loader2 } from "lucide-react";
import type { Customer, CustomerInput } from "../types";
import { Field } from "./ui";

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export default function CustomerForm({
  initial,
  onSubmit,
  onCancel,
}: {
  initial?: Customer | null;
  onSubmit: (data: CustomerInput) => Promise<void>;
  onCancel: () => void;
}) {
  const [form, setForm] = useState<CustomerInput>({
    name: initial?.name ?? "",
    email: initial?.email ?? "",
    phone: initial?.phone ?? "",
    address: initial?.address ?? "",
    gstin: initial?.gstin ?? "",
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);

  const set = (k: keyof CustomerInput) => (e: { target: { value: string } }) =>
    setForm((f) => ({ ...f, [k]: e.target.value }));

  async function submit(e: FormEvent) {
    e.preventDefault();
    const errs: Record<string, string> = {};
    if (!form.name.trim()) errs.name = "Customer name is required";
    if (form.email && !EMAIL_RE.test(form.email)) errs.email = "Enter a valid email address";
    if (form.phone && !/^[+\d\s-]{6,20}$/.test(form.phone)) errs.phone = "Enter a valid phone number";
    setErrors(errs);
    if (Object.keys(errs).length) return;
    setSaving(true);
    try {
      await onSubmit(form);
    } finally {
      setSaving(false);
    }
  }

  return (
    <form onSubmit={submit} className="space-y-4" noValidate>
      <Field label="Name *" error={errors.name}>
        <input className={`input ${errors.name ? "input-error" : ""}`} value={form.name} onChange={set("name")} autoFocus />
      </Field>
      <div className="grid gap-4 sm:grid-cols-2">
        <Field label="Email" error={errors.email}>
          <input className={`input ${errors.email ? "input-error" : ""}`} type="email" value={form.email ?? ""} onChange={set("email")} />
        </Field>
        <Field label="Phone" error={errors.phone}>
          <input className={`input ${errors.phone ? "input-error" : ""}`} value={form.phone ?? ""} onChange={set("phone")} />
        </Field>
      </div>
      <Field label="Address">
        <textarea className="input" rows={2} value={form.address ?? ""} onChange={set("address")} />
      </Field>
      <Field label="GSTIN" hint="Optional tax registration number">
        <input className="input" value={form.gstin ?? ""} onChange={set("gstin")} maxLength={20} />
      </Field>
      <div className="flex justify-end gap-2 pt-2">
        <button type="button" className="btn-secondary" onClick={onCancel}>
          Cancel
        </button>
        <button type="submit" className="btn-primary" disabled={saving}>
          {saving && <Loader2 className="h-4 w-4 animate-spin" />}
          {initial ? "Save changes" : "Add customer"}
        </button>
      </div>
    </form>
  );
}
