import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { getNotifications, markAsRead } from "../services/notificationService";

function MoonLogo() {
  return (
    <div className="w-7 h-7 text-white flex-shrink-0">
      <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg" className="w-full h-full">
        <circle cx="20" cy="20" r="13" stroke="currentColor" strokeWidth="1" strokeDasharray="3 2" />
        <path d="M20 8 C14 8 10 13 10 20 C10 27 14 32 20 32 C17 28 16 24 16 20 C16 16 17 12 20 8Z" fill="currentColor" />
      </svg>
    </div>
  );
}

function NavItem({ label, to, active = false }: { label: string; to: string; active?: boolean }) {
  return (
    <Link
      to={to}
      className={`relative font-elite text-[0.65rem] tracking-[0.18em] uppercase cursor-pointer pb-1 transition-colors duration-200 after:absolute after:bottom-[-4px] after:left-0 after:right-0 after:h-px after:bg-white after:transition-transform after:duration-200 ${
        active ? "text-white after:scale-x-100" : "text-white/45 hover:text-white after:scale-x-0 hover:after:scale-x-100"
      }`}
    >
      {label}
    </Link>
  );
}

function TypeBadge({ value }: { value: string }) {
  const colors: Record<string, string> = {
    EVIDENCE_ADDED: "border-sky-500/40 text-sky-400/80",
    TASK_ASSIGNED:  "border-violet-500/40 text-violet-400/80",
    TASK_OVERDUE:   "border-red-500/40 text-red-400/80",
  };
  return (
    <span className={`font-elite text-[0.58rem] tracking-[0.15em] uppercase px-2 py-1 border ${colors[value] ?? "border-white/20 text-white/40"}`}>
      {value?.replace(/_/g, " ")}
    </span>
  );
}

import { hasRole } from "../auth/roles.utils";

export default function NotificationsPage() {
  const { user, logout, loading, authenticated } = useAuth();
  const [notifications, setNotifications] = useState<any[]>([]);
  const [loadingData, setLoadingData] = useState(true);
  const [filter, setFilter] = useState<"all" | "unread">("all");

  async function load() {
    try {
      setLoadingData(true);
      setNotifications(await getNotifications());
    } catch (e) {
      console.error(e);
    } finally {
      setLoadingData(false);
    }
  }

  useEffect(() => { if (authenticated) load(); }, [authenticated]);

  async function handleMarkRead(id: number) {
    try { await markAsRead(id); load(); } catch (e) { console.error(e); }
  }

  if (loading) return <div className="min-h-screen bg-black flex items-center justify-center text-white/50 font-elite tracking-widest text-xs uppercase">Verifying credentials...</div>;
  if (!authenticated) return null;

  const displayName = user?.name || [user?.given_name, user?.family_name].filter(Boolean).join(" ") || user?.preferred_username || "Unknown Agent";
  const initials = displayName.split(" ").slice(0, 2).map((w: string) => w[0]?.toUpperCase() ?? "").join("");

  const displayed = filter === "unread" ? notifications.filter((n) => !n.read) : notifications;
  const unreadCount = notifications.filter((n) => !n.read).length;

  return (
    <div className="min-h-screen flex flex-col bg-black relative">
      <div className="fixed inset-0 pointer-events-none z-0">
        <div className="absolute inset-0 opacity-[0.03] bg-[url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIzMDAiIGhlaWdodD0iMzAwIj48ZmlsdGVyIGlkPSJub2lzZSI+PGZlVHVyYnVsZW5jZSB0eXBlPSJmcmFjdGFsTm9pc2UiIGJhc2VGcmVxdWVuY3k9IjAuNjUiIG51bU9jdGF2ZXM9IjMiIHN0aXRjaFRpbGVzPSJzdGl0Y2giLz48L2ZpbHRlcj48cmVjdCB3aWR0aD0iMzAwIiBoZWlnaHQ9IjMwMCIgZmlsdGVyPSJ1cmwoI25vaXNlKSIgb3BhY2l0eT0iMSIvPjwvc3ZnPg==')]" />
        <div className="absolute inset-0 bg-[radial-gradient(ellipse_60%_50%_at_50%_50%,rgba(255,255,255,0.05)_0%,transparent_70%)]" />
        <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.025)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.025)_1px,transparent_1px)] bg-[size:48px_48px]" />
      </div>

      <header className="relative z-20 flex items-center gap-6 px-10 h-16 border-b border-white/10 bg-black/90 backdrop-blur-sm animate-slide-down">
        <div className="flex items-center gap-3 flex-shrink-0">
          <MoonLogo />
          <span className="font-playfair text-base font-bold text-white whitespace-nowrap">Luna Lunera <em className="font-normal not-italic text-white/55">& Associates</em></span>
        </div>
        <nav className="hidden md:flex flex-1 items-center justify-center gap-8">
          <NavItem label="Home" to="/dashboard" />
          <NavItem label="Cases" to="/dashboard/cases" />
          <NavItem label="People" to="/dashboard/people" />
          <NavItem label="Evidences" to="/dashboard/evidences" />
          <NavItem label="Tasks" to="/dashboard/tasks" />
          <NavItem label="Notifications" to="/dashboard/notifications" active />
          {hasRole("ADMIN") && <NavItem label="Audit" to="/dashboard/audit" />}
        </nav>
        <div className="flex items-center gap-3 flex-shrink-0 ml-auto">
          <div className="w-8 h-8 rounded-full border border-white/20 bg-white/10 text-white font-playfair font-bold text-sm flex items-center justify-center">{initials}</div>
          <button onClick={logout} className="border border-white/30 text-white/55 font-elite text-[0.6rem] tracking-[0.14em] uppercase px-3 py-1.5 transition-all duration-200 hover:border-white hover:text-white">Sign out</button>
        </div>
      </header>

      <main className="relative z-10 flex-1 max-w-[1200px] w-full mx-auto px-10 py-10 flex flex-col gap-8 animate-fade-up">
        <section className="relative border border-white/15 bg-white/[0.03] px-12 py-8 overflow-hidden">
          <span className="absolute top-4 right-[-2.5rem] font-elite text-[0.6rem] tracking-[0.3em] text-red-600/15 rotate-12 pointer-events-none select-none whitespace-nowrap">CONFIDENTIAL</span>
          <div className="flex items-center gap-4 font-elite text-[0.58rem] tracking-[0.3em] text-white/45 uppercase mb-5">
            <span className="flex-1 max-w-[120px] h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.2),transparent)]" />
            <span>ALERT SYSTEM</span>
            <span className="flex-1 max-w-[120px] h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.2),transparent)]" />
          </div>
          <div className="flex items-center justify-between gap-6">
            <div>
              <h1 className="font-playfair font-black text-4xl md:text-5xl text-white leading-tight mb-2" style={{ textShadow: "0 0 40px rgba(255,255,255,0.15)" }}>
                Notifications
                {unreadCount > 0 && <span className="ml-3 text-lg font-elite text-amber-400/70">{unreadCount} unread</span>}
              </h1>
              <p className="font-crimson text-base text-white/60">System alerts and event notifications</p>
            </div>
            <div className="flex gap-2">
              {(["all", "unread"] as const).map((f) => (
                <button
                  key={f}
                  onClick={() => setFilter(f)}
                  className={`font-elite text-[0.58rem] tracking-[0.18em] uppercase px-4 py-2 border transition-all ${filter === f ? "border-white text-white" : "border-white/20 text-white/40 hover:border-white/50 hover:text-white/70"}`}
                >
                  {f}
                </button>
              ))}
            </div>
          </div>
        </section>

        <section className="border border-white/15 bg-white/[0.03] overflow-x-auto">
          {loadingData ? (
            <div className="p-10 text-center font-elite text-[0.65rem] tracking-[0.2em] uppercase text-white/40">Loading notifications...</div>
          ) : displayed.length === 0 ? (
            <div className="p-10 text-center font-crimson text-lg italic text-white/35">No notifications.</div>
          ) : (
            <table className="w-full text-sm">
              <thead className="border-b border-white/10">
                <tr>
                  {["ID", "Type", "Case", "Message", "Received", "Status", ""].map((h, i) => (
                    <th key={i} className={`p-4 font-elite text-[0.58rem] tracking-[0.2em] uppercase text-white/40 ${i === 6 ? "text-right" : "text-left"}`}>{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {displayed.map((n) => (
                  <tr key={n.id} className={`border-b border-white/5 transition-colors duration-150 ${!n.read ? "bg-white/[0.02]" : ""} hover:bg-white/[0.04]`}>
                    <td className="p-4 font-elite text-[0.65rem] tracking-widest text-white/40">#{n.id}</td>
                    <td className="p-4"><TypeBadge value={n.type} /></td>
                    <td className="p-4 font-elite text-[0.65rem] tracking-wider text-white/60">{n.caseId ? `#${n.caseId}` : "—"}</td>
                    <td className="p-4 font-crimson text-base text-white max-w-xs">{n.message}</td>
                    <td className="p-4 font-elite text-[0.62rem] tracking-wider text-white/40 whitespace-nowrap">{new Date(n.createdAt).toLocaleString()}</td>
                    <td className="p-4">
                      <span className={`font-elite text-[0.58rem] tracking-[0.15em] uppercase px-2 py-1 border ${n.read ? "border-white/15 text-white/25" : "border-amber-500/40 text-amber-400/80"}`}>
                        {n.read ? "Read" : "Unread"}
                      </span>
                    </td>
                    <td className="p-4 text-right">
                      {!n.read && (
                        <button onClick={() => handleMarkRead(n.id)} className="font-elite text-[0.58rem] tracking-[0.15em] uppercase text-white/40 hover:text-white transition-colors whitespace-nowrap">
                          Mark read
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </section>
      </main>

      <footer className="relative z-10 border-t border-white/10 py-4 font-elite text-[0.58rem] tracking-[0.12em] text-white/30 uppercase flex gap-2 justify-center">
        <span>© {new Date().getFullYear()} Luna Lunera & Associates</span>
        <span className="text-white/20">·</span>
        <span>Confidential information — internal use only</span>
      </footer>
    </div>
  );
}
