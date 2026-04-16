import { useState } from "react";
import { api } from "../api";
import { Modal } from "./Modal";
import { inputClass, labelClass } from "../lib/styles";
import * as React from "react";

type Props = {
  open: boolean;
  onClose: () => void;
  onCreated: () => void;
};

const timeOptions = Array.from({ length: 48 }, (_, i) => {
  const h = Math.floor(i / 2).toString().padStart(2, "0");
  const m = i % 2 === 0 ? "00" : "30";
  return `${h}:${m}`;
});

export function CreateEventModal({ open, onClose, onCreated }: Props) {
  const [name, setName] = useState("");
  const [date, setDate] = useState("");
  const [time, setTime] = useState("09:00");
  const [max, setMax] = useState("50");
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const reset = () => {
    setName("");
    setDate("");
    setTime("09:00");
    setMax("50");
    setError(null);
  };

  const submit = async (e: React.SyntheticEvent) => {
    e.preventDefault();
    const startsAt = new Date(`${date}T${time}`);
    if (startsAt <= new Date()) {
      setError("Event start time must be in the future.");
      return;
    }
    setBusy(true);
    setError(null);
    try {
      await api.createEvent({
        name: name.trim(),
        startsAt: startsAt.toISOString(),
        maxParticipants: Number(max),
      });
      reset();
      onCreated();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not create event");
    } finally {
      setBusy(false);
    }
  };

  return (
    <Modal open={open} onClose={onClose} title="Create event">
      <form onSubmit={submit} className="space-y-4">
        <div>
          <label className={labelClass}>Event name</label>
          <input
            type="text"
            required
            value={name}
            onChange={(e) => setName(e.target.value)}
            className={inputClass}
            placeholder="Team offsite"
          />
        </div>
        <div className="grid grid-cols-[1fr_130px_110px] gap-3">
          <div>
            <label className={labelClass}>Date</label>
            <input
              type="date"
              required
              value={date}
              onChange={(e) => setDate(e.target.value)}
              className={inputClass}
            />
          </div>
          <div>
            <label className={labelClass}>Time</label>
            <select
              required
              value={time}
              onChange={(e) => setTime(e.target.value)}
              className={inputClass}
            >
              {timeOptions.map((t) => (
                <option key={t} value={t}>{t}</option>
              ))}
            </select>
          </div>
          <div>
            <label className={labelClass}>Capacity</label>
            <input
              type="number"
              min={1}
              required
              value={max}
              onChange={(e) => setMax(e.target.value)}
              className={inputClass}
            />
          </div>
        </div>
        {error && <p className="text-xs text-error">{error}</p>}
        <div className="flex justify-end pt-1">
          <button
            type="submit"
            disabled={busy}
            className="px-4 py-2 bg-accent text-white text-sm font-medium rounded-lg hover:bg-accent-dark transition-colors duration-150 disabled:opacity-40 disabled:cursor-not-allowed"
          >
            {busy ? "Creating…" : "Create event"}
          </button>
        </div>
      </form>
    </Modal>
  );
}
