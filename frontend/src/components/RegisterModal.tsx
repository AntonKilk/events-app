import { useState } from "react";
import { api } from "../api";
import type { EventItem } from "../types";
import { formatDateTime } from "../lib/format";
import { Modal } from "./Modal";
import { inputClass, labelClass } from "../lib/styles";

type Props = {
  event: EventItem | null;
  onClose: () => void;
  onDone: () => void;
};

export function RegisterModal({ event, onClose, onDone }: Props) {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [idNumber, setIdNumber] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const [done, setDone] = useState(false);

  if (!event) return null;

  const close = () => {
    setFirstName("");
    setLastName("");
    setIdNumber("");
    setError(null);
    setDone(false);
    onClose();
  };

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setError(null);
    try {
      await api.register(event.id, {
        firstName: firstName.trim(),
        lastName: lastName.trim(),
        idNumber: idNumber.trim(),
      });
      setDone(true);
      onDone();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Registration failed");
    } finally {
      setBusy(false);
    }
  };

  return (
    <Modal open={!!event} onClose={close} title={done ? "Registration confirmed" : event.name}>
      {done ? (
        <div className="space-y-5">
          <p className="text-sm text-ink">
            You're registered for <span className="font-medium">{event.name}</span>.
          </p>
          <div className="flex justify-end">
            <button
              onClick={close}
              className="px-4 py-2 bg-accent text-white text-sm font-medium rounded-lg hover:bg-accent-dark transition-colors duration-150"
            >
              Done
            </button>
          </div>
        </div>
      ) : (
        <form onSubmit={submit} className="space-y-4">
          <p className="text-xs text-ink-soft -mt-1">{formatDateTime(event.startsAt)}</p>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className={labelClass}>First name</label>
              <input
                type="text"
                required
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                className={inputClass}
                placeholder="Jane"
              />
            </div>
            <div>
              <label className={labelClass}>Last name</label>
              <input
                type="text"
                required
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                className={inputClass}
                placeholder="Doe"
              />
            </div>
          </div>
          <div>
            <label className={labelClass}>ID number</label>
            <input
              type="text"
              required
              value={idNumber}
              onChange={(e) => setIdNumber(e.target.value)}
              className={inputClass}
              placeholder="AA-00000-00"
            />
          </div>
          {error && <p className="text-xs text-error">{error}</p>}
          <div className="flex justify-end pt-1">
            <button
              type="submit"
              disabled={busy}
              className="px-4 py-2 bg-accent text-white text-sm font-medium rounded-lg hover:bg-accent-dark transition-colors duration-150 disabled:opacity-40 disabled:cursor-not-allowed"
            >
              {busy ? "Registering…" : "Register"}
            </button>
          </div>
        </form>
      )}
    </Modal>
  );
}
