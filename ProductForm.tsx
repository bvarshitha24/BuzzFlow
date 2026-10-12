import { useState, type FormEvent } from "react";
import { Loader2 } from "lucide-react";
import type { Product, ProductInput } from "../types";
import { isMoney } from "../utils/format";
import { Field } from "./ui";

export default function ProductForm({
  initial,
  onSubmit,
  onCancel,
}: {
  initial?: Product | null;
  onSubmit: (data: ProductInput) => Promise<void>;
  onCancel: () => void;
}) {
  const [form, setForm] = useState<ProductInput>({
    name: initial?.name ?? "",
    description: initial?.description ?? "",
    unit_price: initial ? String(Number(initial.unit_price)) : "",
    tax_rate: initial ? String(Number(initial.tax_rate)) : "18",
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);

  const set = (k: keyof ProductInput) => (e: { target: { value: string } }) =>
    setForm((f) => ({ ...f, [k]: e.target.value }));

  async function submit(e: FormEvent) {
    e.preventDefault();
    const errs: Record<string, string> = {};
    if (!form.name.trim()) errs.name = "Name is required";
    if (!isMoney(form.unit_price)) errs.unit_price = "Enter a price like 1500 or 1500.50";
    if (!isMoney(form.tax_rate) || Number(form.tax_rate) > 100) errs.tax_rate = "Tax must be between 0 and 100";
    setErrors(errs);
    if (Object.keys(errs).length) return;
    setSaving(true);
    try {
      await onSubmit({ ...form, unit_price: form.unit_price.trim(), tax_rate: form.tax_rate.trim() });
    } finally {
      setSaving(false);
    }
  }

  return (
    <form onSubmit={submit} className="space-y-4" noValidate>
      <Field label="Name *" error={errors.name}>
        <input className={`input ${errors.name ? "input-error" : ""}`} value={form.name} onChange={set("name")} autoFocus />
      </Field>
      <Field label="Description">
        <textarea className="input" rows={2} value={form.description} onChange={set("description")} />
      </Field>
      <div className="grid gap-4 sm:grid-cols-2">
        <Field label="Unit price (₹) *" error={errors.unit_price}>
          <input className={`input ${errors.unit_price ? "input-error" : ""}`} inputMode="decimal" value={form.unit_price} onChange={set("unit_price")} placeholder="0.00" />
        </Field>
        <Field label="Tax rate (%) *" error={errors.tax_rate}>
          <input className={`input ${errors.tax_rate ? "input-error" : ""}`} inputMode="decimal" value={form.tax_rate} onChange={set("tax_rate")} />
        </Field>
      </div>
      {initial && (
        <p className="rounded-lg bg-slate-50 p-3 text-xs text-slate-500">
          Changing the price only affects new invoices. Existing invoices keep the price they were created with.
        </p>
      )}
      <div className="flex justify-end gap-2 pt-2">
        <button type="button" className="btn-secondary" onClick={onCancel}>
          Cancel
        </button>
        <button type="submit" className="btn-primary" disabled={saving}>
          {saving && <Loader2 className="h-4 w-4 animate-spin" />}
          {initial ? "Save changes" : "Add product"}
        </button>
      </div>
    </form>
  );
}
