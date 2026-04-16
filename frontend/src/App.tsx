import { useEffect, useState } from "react";
import { api } from "./api";
import type { EventItem } from "./types";
import { formatDateTime } from "./lib/format";
import { LoginModal } from "./components/LoginModal";
import { CreateEventModal } from "./components/CreateEventModal";
import { RegisterModal } from "./components/RegisterModal";

type Modal =
  | { kind: "login" }
  | { kind: "create" }
  | { kind: "register"; event: EventItem }
  | null;

function App() {
  const [events, setEvents] = useState<EventItem[] | null>(null);
  const [isAdmin, setIsAdmin] = useState(false);
  const [modal, setModal] = useState<Modal>(null);

  const fetchEvents = async () => {
    try {
      const list = await api.listEvents();
      list.sort((a, b) => new Date(a.startsAt).getTime() - new Date(b.startsAt).getTime());
      setEvents(list);
    } catch {
      setEvents([]);
    }
  };

  useEffect(() => {
    void fetchEvents();
    api.me().then((m) => setIsAdmin(m.isAdmin)).catch(() => {});
  }, []);

  const handleLogout = () => {
    api.logout().catch(() => {});
    setIsAdmin(false);
  };

  return (
    <>
      <header className="sticky top-0 z-10 bg-surface border-b border-rule">
        <div className="max-w-3xl mx-auto px-6 py-4 flex items-center justify-between gap-4">
          <h1 className="text-base font-semibold text-ink tracking-tight">Events</h1>
          <div className="flex items-center gap-4">
            {isAdmin && (
              <button
                onClick={() => setModal({ kind: "create" })}
                className="text-sm font-medium text-accent hover:text-accent-dark transition-colors duration-150"
              >
                + Create event
              </button>
            )}
            {isAdmin ? (
              <button
                onClick={handleLogout}
                className="text-xs text-ink-soft hover:text-ink transition-colors duration-150"
              >
                Log out
              </button>
            ) : (
              <button
                onClick={() => setModal({ kind: "login" })}
                className="text-xs px-3 py-1.5 rounded-lg border border-rule text-ink-soft hover:text-ink hover:border-ink/20 transition-colors duration-150"
              >
                Admin
              </button>
            )}
          </div>
        </div>
      </header>

      <main className="max-w-3xl mx-auto px-6 py-8">
        {events === null && (
          <p className="py-20 text-center text-sm text-ink-soft">Loading…</p>
        )}

        {events !== null && events.length === 0 && (
          <p className="py-20 text-center text-sm text-ink-soft">
            No events scheduled yet.
          </p>
        )}

        {events !== null && events.length > 0 && (
          <ul className="divide-y divide-rule">
            {events.map((event) => {
              const full = event.registrationsCount >= event.maxParticipants;
              return (
                <li
                  key={event.id}
                  className="flex items-start justify-between gap-6 py-5"
                >
                  <div className="min-w-0">
                    <p className="text-sm font-semibold text-ink">{event.name}</p>
                    <p className="mt-1 text-xs text-ink-soft tnum">
                      {formatDateTime(event.startsAt)}
                      <span className="mx-2 opacity-40">·</span>
                      {event.registrationsCount}/{event.maxParticipants} spots
                    </p>
                  </div>
                  <div className="shrink-0 pt-0.5">
                    {full ? (
                      <span className="text-xs text-ink-soft">Full</span>
                    ) : (
                      <button
                        onClick={() => setModal({ kind: "register", event })}
                        className="text-xs font-medium text-accent hover:text-accent-dark transition-colors duration-150"
                      >
                        Register →
                      </button>
                    )}
                  </div>
                </li>
              );
            })}
          </ul>
        )}
      </main>

      <LoginModal
        open={modal?.kind === "login"}
        onClose={() => setModal(null)}
        onSuccess={() => {
          setIsAdmin(true);
          setModal(null);
        }}
      />
      <CreateEventModal
        open={modal?.kind === "create"}
        onClose={() => setModal(null)}
        onCreated={() => {
          setModal(null);
          void fetchEvents();
        }}
      />
      <RegisterModal
        event={modal !== null && modal.kind === "register" ? modal.event : null}
        onClose={() => setModal(null)}
        onDone={fetchEvents}
      />
    </>
  );
}

export default App;
