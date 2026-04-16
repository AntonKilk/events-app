import type { CreateEventInput, EventItem, RegisterInput } from "./types";

const base = "/api";

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const res = await fetch(`${base}${path}`, {
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...(init.headers ?? {}),
    },
    ...init,
  });
  if (!res.ok) {
    let message = `${res.status} ${res.statusText}`;
    try {
      const body = await res.json();
      if (body?.message) message = body.message;
      else if (body?.error) message = body.error;
    } catch {
    }
    throw new Error(message);
  }
  if (res.status === 204) return undefined as T;
  const text = await res.text();
  return text ? (JSON.parse(text) as T) : (undefined as T);
}

export const api = {
  listEvents: () => request<EventItem[]>("/events"),
  createEvent: (input: CreateEventInput) =>
    request<EventItem>("/events", { method: "POST", body: JSON.stringify(input) }),
  register: (eventId: number, input: RegisterInput) =>
    request<void>(`/events/${eventId}/registrations`, {
      method: "POST",
      body: JSON.stringify(input),
    }),
  login: (email: string, password: string) =>
    request<{ ok: boolean }>("/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    }),
  logout: () => request<void>("/auth/logout", { method: "POST" }),
  me: () => request<{ isAdmin: boolean }>("/auth/me"),
};
