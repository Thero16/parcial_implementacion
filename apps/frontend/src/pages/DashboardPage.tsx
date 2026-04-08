import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { hasRole } from "../auth/roles.utils";

// ── Mini moon logo ────────────────────────────────────────────
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

// ── Nav item ──────────────────────────────────────────────────
function NavItem({
  label,
  to,
  active = false,
}: {
  label: string;
  to: string;
  active?: boolean;
}) {
  return (
    <Link
      to={to}
      className={`
        relative font-elite text-[0.65rem] tracking-[0.18em] uppercase cursor-pointer
        pb-1 transition-colors duration-200
        after:absolute after:bottom-[-4px] after:left-0 after:right-0 after:h-px after:bg-white after:transition-transform after:duration-200
        ${
          active
            ? "text-white after:scale-x-100"
            : "text-white/45 hover:text-white after:scale-x-0 hover:after:scale-x-100"
        }
      `}
    >
      {label}
    </Link>
  );
}

// ── Rotating emblem ───────────────────────────────────────────
function WelcomeEmblem() {
  return (
    <div className="relative w-28 h-28 flex-shrink-0 hidden md:flex items-center justify-center">
      <div className="absolute inset-0 rounded-full border border-white/20 animate-spin-slow" />
      <div className="absolute inset-2.5 rounded-full border border-white/15 animate-spin-rev" />
      <div className="absolute inset-[22px] rounded-full border border-white/20 animate-spin-fast" />

      <div className="w-16 h-16 text-white/50 relative z-10">
        <svg viewBox="0 0 100 100" fill="none" xmlns="http://www.w3.org/2000/svg" className="w-full h-full">
          <circle cx="50" cy="50" r="35" stroke="currentColor" strokeWidth="1.5" strokeDasharray="6 3" />
          <path d="M50 20 C36 20 26 33 26 50 C26 67 36 80 50 80 C42 73 38 62 38 50 C38 38 42 27 50 20Z" fill="currentColor" opacity="0.8" />
          <circle cx="50" cy="50" r="4" fill="currentColor" />
          <line x1="50" y1="8" x2="50" y2="18" stroke="currentColor" strokeWidth="1.5" />
          <line x1="50" y1="82" x2="50" y2="92" stroke="currentColor" strokeWidth="1.5" />
          <line x1="8" y1="50" x2="18" y2="50" stroke="currentColor" strokeWidth="1.5" />
          <line x1="82" y1="50" x2="92" y2="50" stroke="currentColor" strokeWidth="1.5" />
        </svg>
      </div>
    </div>
  );
}

// ── Main Dashboard ────────────────────────────────────────────
export default function DashboardPage() {
  const { user, logout } = useAuth();

  const displayName =
    user?.name ||
    [user?.given_name, user?.family_name].filter(Boolean).join(" ") ||
    user?.preferred_username ||
    user?.email ||
    "Unknown Agent";

  const initials = displayName
    .split(" ")
    .slice(0, 2)
    .map((w) => w[0]?.toUpperCase() ?? "")
    .join("");

  const greeting = (() => {
    const h = new Date().getHours();
    if (h < 12) return "Good morning";
    if (h < 19) return "Good afternoon";
    return "Good evening";
  })();

  return (
    <div className="min-h-screen flex flex-col bg-black relative">

      {/* ── Fixed background layers ── */}
      <div className="fixed inset-0 pointer-events-none z-0">
        <div className="absolute inset-0 opacity-[0.03] bg-[url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIzMDAiIGhlaWdodD0iMzAwIj48ZmlsdGVyIGlkPSJub2lzZSI+PGZlVHVyYnVsZW5jZSB0eXBlPSJmcmFjdGFsTm9pc2UiIGJhc2VGcmVxdWVuY3k9IjAuNjUiIG51bU9jdGF2ZXM9IjMiIHN0aXRjaFRpbGVzPSJzdGl0Y2giLz48L2ZpbHRlcj48cmVjdCB3aWR0aD0iMzAwIiBoZWlnaHQ9IjMwMCIgZmlsdGVyPSJ1cmwoI25vaXNlKSIgb3BhY2l0eT0iMSIvPjwvc3ZnPg==')]" />
        <div className="absolute inset-0 bg-[radial-gradient(ellipse_60%_50%_at_50%_50%,rgba(255,255,255,0.05)_0%,transparent_70%)]" />
        <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.025)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.025)_1px,transparent_1px)] bg-[size:48px_48px]" />
      </div>

      {/* ── Header ── */}
      <header className="relative z-20 flex items-center gap-6 px-10 h-16 border-b border-white/10 bg-black/90 backdrop-blur-sm animate-slide-down">
        
        <div className="flex items-center gap-3 flex-shrink-0">
          <MoonLogo />
          <span className="font-playfair text-base font-bold text-white whitespace-nowrap">
            Luna Lunera <em className="font-normal not-italic text-white/55">& Associates</em>
          </span>
        </div>

        <nav className="hidden md:flex flex-1 items-center justify-center gap-8">
          <NavItem label="Home" to="/dashboard" active />
          <NavItem label="Cases" to="/dashboard/cases" />
          <NavItem label="People" to="/dashboard/people" />
          <NavItem label="Evidences" to="/dashboard/evidences" />
          <NavItem label="Tasks" to="/dashboard/tasks" />
          <NavItem label="Notifications" to="/dashboard/notifications" />
          {hasRole("ADMIN") && <NavItem label="Audit" to="/dashboard/audit" />}
        </nav>

        <div className="flex items-center gap-3 flex-shrink-0 ml-auto">
          <div className="w-8 h-8 rounded-full border border-white/20 bg-white/10 text-white font-playfair font-bold text-sm flex items-center justify-center">
            {initials}
          </div>

          <button
            onClick={logout}
            className="border border-white/30 text-white/55 font-elite text-[0.6rem] tracking-[0.14em] uppercase px-3 py-1.5 transition-all duration-200 hover:border-white hover:text-white"
          >
            Sign out
          </button>
        </div>

      </header>

      {/* ── Main content ── */}
      <main className="relative z-10 flex-1 max-w-[1200px] w-full mx-auto px-10 py-10 flex flex-col gap-8 animate-fade-up">

        <section className="relative border border-white/15 bg-white/[0.03] px-12 py-10 overflow-hidden">

          <span className="absolute top-4 right-[-2.5rem] font-elite text-[0.6rem] tracking-[0.3em] text-red-600/15 rotate-12 pointer-events-none select-none whitespace-nowrap">
            CONFIDENTIAL
          </span>

          <div className="flex items-center gap-4 font-elite text-[0.58rem] tracking-[0.3em] text-white/45 uppercase mb-6">
            <span className="flex-1 max-w-[120px] h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.2),transparent)]" />
            <span>OPERATIONS PANEL</span>
            <span className="flex-1 max-w-[120px] h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.2),transparent)]" />
          </div>

          <div className="flex items-center justify-between gap-8">
            <div className="flex-1">

              <p className="font-crimson text-lg italic text-white/55 mb-1">
                {greeting},
              </p>

              <h1
                className="font-playfair font-black text-4xl md:text-5xl text-white leading-tight mb-3"
                style={{ textShadow: "0 0 40px rgba(255,255,255,0.15)" }}
              >
                {displayName}
              </h1>

              <p className="font-crimson text-base text-white/60 leading-relaxed mb-2">
                Welcome back to the agency. Your credentials have been verified.
              </p>

              {user?.email && (
                <p className="font-elite text-[0.7rem] tracking-[0.1em] text-white/40">
                  ✉ {user.email}
                </p>
              )}
            </div>

            <WelcomeEmblem />
          </div>

        </section>

      </main>

      {/* ── Footer ── */}
      <footer className="relative z-10 border-t border-white/10 py-4 font-elite text-[0.58rem] tracking-[0.12em] text-white/30 uppercase flex gap-2 justify-center">
        <span>© {new Date().getFullYear()} Luna Lunera & Associates</span>
        <span className="text-white/20">·</span>
        <span>Confidential information — internal use only</span>
      </footer>

    </div>
  );
}