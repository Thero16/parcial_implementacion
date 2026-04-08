import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { getAuditLogs } from "../services/auditService";
import { hasRole } from "../auth/roles.utils";

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

function parseDescription(description: string) {
  const prefix = "Event received: ";
  if (!description?.startsWith(prefix)) return null;
  try {
    return JSON.parse(description.slice(prefix.length));
  } catch {
    return null;
  }
}

function DescriptionCell({ description }: { description: string }) {
  const parsed = parseDescription(description);
  if (!parsed) return <span className="font-crimson text-base text-white/70">{description}</span>;
  return (
    <div className="flex flex-wrap gap-x-4 gap-y-1">
      {Object.entries(parsed).map(([k, v]) => (
        <span key={k} className="whitespace-nowrap">
          <span className="font-elite text-[0.55rem] tracking-[0.15em] uppercase text-white/35">{k}: </span>
          <span className="font-crimson text-sm text-white/80">{String(v)}</span>
        </span>
      ))}
    </div>
  );
}

export default function AuditLogsPage() {
  const { user, logout, loading, authenticated } = useAuth();
  const [logs, setLogs] = useState<any[]>([]);
  const [loadingData, setLoadingData] = useState(true);

  useEffect(() => {
    if (authenticated) {
      getAuditLogs()
        .then(setLogs)
        .catch(console.error)
        .finally(() => setLoadingData(false));
    }
  }, [authenticated]);

  if (loading) return <div className="min-h-screen bg-black flex items-center justify-center text-white/50 font-elite tracking-widest text-xs uppercase">Verifying credentials...</div>;
  if (!authenticated) return null;

  const displayName = user?.name || [user?.given_name, user?.family_name].filter(Boolean).join(" ") || user?.preferred_username || "Unknown Agent";
  const initials = displayName.split(" ").slice(0, 2).map((w: string) => w[0]?.toUpperCase() ?? "").join("");

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
          <NavItem label="Notifications" to="/dashboard/notifications" />
          {hasRole("ADMIN") && <NavItem label="Audit" to="/dashboard/audit" active />}
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
            <span>SYSTEM AUDIT</span>
            <span className="flex-1 max-w-[120px] h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.2),transparent)]" />
          </div>
          <div>
            <h1 className="font-playfair font-black text-4xl md:text-5xl text-white leading-tight mb-2" style={{ textShadow: "0 0 40px rgba(255,255,255,0.15)" }}>Audit Logs</h1>
            <p className="font-crimson text-base text-white/60">Full event trail — read only</p>
          </div>
        </section>

        <section className="border border-white/15 bg-white/[0.03] overflow-x-auto">
          {loadingData ? (
            <div className="p-10 text-center font-elite text-[0.65rem] tracking-[0.2em] uppercase text-white/40">Loading audit logs...</div>
          ) : logs.length === 0 ? (
            <div className="p-10 text-center font-crimson text-lg italic text-white/35">No audit logs on record.</div>
          ) : (
            <table className="w-full text-sm">
              <thead className="border-b border-white/10">
                <tr>
                  {["ID", "Event Type", "Entity", "Description", "Performed By", "Timestamp"].map((h) => (
                    <th key={h} className="p-4 font-elite text-[0.58rem] tracking-[0.2em] uppercase text-white/40 text-left">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {logs.map((log) => (
                  <tr key={log.id} className="border-b border-white/5 hover:bg-white/[0.04] transition-colors duration-150">
                    <td className="p-4 font-elite text-[0.65rem] tracking-widest text-white/40">#{log.id}</td>
                    <td className="p-4">
                      <span className="font-elite text-[0.58rem] tracking-[0.15em] uppercase px-2 py-1 border border-white/20 text-white/60">
                        {log.eventType}
                      </span>
                    </td>
                    <td className="p-4 font-elite text-[0.65rem] tracking-wider text-white/50">{log.entityId ?? "—"}</td>
                    <td className="p-4 max-w-sm"><DescriptionCell description={log.description} /></td>
                    <td className="p-4 font-elite text-[0.65rem] tracking-wider text-white/50">{log.performedBy ?? "—"}</td>
                    <td className="p-4 font-elite text-[0.62rem] tracking-wider text-white/40 whitespace-nowrap">{new Date(log.timestamp).toLocaleString()}</td>
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
