import { useState } from "react";
import { api } from "../api";
import { Modal } from "./Modal";
import { inputClass, labelClass } from "../lib/styles";
import * as React from "react";

type Props = {
  open: boolean;
  onClose: () => void;
  onSuccess: () => void;
};

export function LoginModal({ open, onClose, onSuccess }: Props) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const submit = async (e: React.SyntheticEvent) => {
    e.preventDefault();
    setBusy(true);
    setError(null);
    try {
      await api.login(email, password);
      setEmail("");
      setPassword("");
      onSuccess();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Authentication failed");
    } finally {
      setBusy(false);
    }
  };

  return (
    <Modal open={open} onClose={onClose} title="Admin login">
      <form onSubmit={submit} className="space-y-4">
        <div>
          <label className={labelClass}>Email</label>
          <input
            type="email"
            autoComplete="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className={inputClass}
            placeholder="admin@example.com"
          />
        </div>
        <div>
          <label className={labelClass}>Password</label>
          <input
            type="password"
            autoComplete="current-password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className={inputClass}
            placeholder="••••••••"
          />
        </div>
        {error && <p className="text-xs text-error">{error}</p>}
        <div className="flex justify-end pt-1">
          <button
            type="submit"
            disabled={busy}
            className="px-4 py-2 bg-accent text-white text-sm font-medium rounded-lg hover:bg-accent-dark transition-colors duration-150 disabled:opacity-40 disabled:cursor-not-allowed"
          >
            {busy ? "Logging in…" : "Log in"}
          </button>
        </div>
      </form>
    </Modal>
  );
}
