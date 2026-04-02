import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { getPeople, deletePerson } from "../services/peopleService";
import { getCases } from "../services/caseService";
import { hasRole } from "../auth/roles.utils";
import PersonModal from "../components/PersonModal";
import PersonDetailModal from "../components/PersonDetailModal";

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

// ── Role badge ────────────────────────────────────────────────
function RoleBadge({ value }: { value: string }) {
  const colors: Record<string, string> = {
    SUSPECT: "border-red-500/40 text-red-400/80",
    WITNESS: "border-blue-500/40 text-blue-400/80",
    VICTIM: "border-purple-500/40 text-purple-400/80",
    INFORMANT: "border-yellow-500/40 text-yellow-400/80",
  };

  return (
    <span
      className={`font-elite text-[0.58rem] tracking-[0.15em] uppercase px-2 py-1 border ${
        colors[value?.toUpperCase()] ?? "border-white/20 text-white/40"
      }`}
    >
      {value}
    </span>
  );
}

// ── Main PeoplePage ───────────────────────────────────────────
export default function PeoplePage() {
  const { user, logout, loading, authenticated } = useAuth();

  const [people, setPeople] = useState<any[]>([]);
  const [cases, setCases] = useState<any[]>([]);
  const [loadingPeople, setLoadingPeople] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingPerson, setEditingPerson] = useState<any>(null);
  const [detailPerson, setDetailPerson] = useState<any>(null);

  async function loadData() {
    try {
      setLoadingPeople(true);
      const [peopleData, casesData] = await Promise.all([
        getPeople(),
        getCases(),
      ]);
      setPeople(peopleData);
      setCases(casesData);
    } catch (err) {
      console.error("Failed to load people", err);
    } finally {
      setLoadingPeople(false);
    }
  }

  useEffect(() => {
    if (authenticated) {
      loadData();
    }
  }, [authenticated]);

  async function handleDelete(id: number) {
    if (!confirm(`Delete person #${id}? This action cannot be undone.`)) return;
    try {
      await deletePerson(id);
      loadData();
    } catch (err) {
      console.error("Failed to delete person", err);
    }
  }

  const caseMap = useMemo(() => {
    const map: Record<number, string> = {};
    cases.forEach((c) => {
      map[c.id] = c.title;
    });
    return map;
  }, [cases]);

  if (loading) {
    return (
      <div className="min-h-screen bg-black flex items-center justify-center text-white/50 font-elite tracking-widest text-xs uppercase">
        Verifying credentials...
      </div>
    );
  }

  if (!authenticated) return null;

  const displayName =
    user?.name ||
    [user?.given_name, user?.family_name].filter(Boolean).join(" ") ||
    user?.preferred_username ||
    user?.email ||
    "Unknown Agent";

  const initials = displayName
    .split(" ")
    .slice(0, 2)
    .map((w: string) => w[0]?.toUpperCase() ?? "")
    .join("");

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
          <NavItem label="Home" to="/dashboard" />
          <NavItem label="Cases" to="/dashboard/cases" />
          <NavItem label="People" to="/dashboard/people" active />
          <NavItem label="Evidences" to="/dashboard/evidences" />
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

        {/* Page header section */}
        <section className="relative border border-white/15 bg-white/[0.03] px-12 py-8 overflow-hidden">
          <span className="absolute top-4 right-[-2.5rem] font-elite text-[0.6rem] tracking-[0.3em] text-red-600/15 rotate-12 pointer-events-none select-none whitespace-nowrap">
            CONFIDENTIAL
          </span>

          <div className="flex items-center gap-4 font-elite text-[0.58rem] tracking-[0.3em] text-white/45 uppercase mb-5">
            <span className="flex-1 max-w-[120px] h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.2),transparent)]" />
            <span>PERSONS OF INTEREST</span>
            <span className="flex-1 max-w-[120px] h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.2),transparent)]" />
          </div>

          <div className="flex items-center justify-between gap-6">
            <div>
              <h1
                className="font-playfair font-black text-4xl md:text-5xl text-white leading-tight mb-2"
                style={{ textShadow: "0 0 40px rgba(255,255,255,0.15)" }}
              >
                People
              </h1>
              <p className="font-crimson text-base text-white/60 leading-relaxed">
                Suspects, witnesses and persons linked to investigations
              </p>
            </div>

            {(hasRole("ADMIN") || hasRole("DETECTIVE")) && (
              <button
                onClick={() => {
                  setEditingPerson(null);
                  setModalOpen(true);
                }}
                className="flex-shrink-0 border border-white/30 text-white/70 font-elite text-[0.6rem] tracking-[0.18em] uppercase px-5 py-2.5 transition-all duration-200 hover:border-white hover:text-white"
              >
                + New Person
              </button>
            )}
          </div>
        </section>

        {/* Table section */}
        <section className="border border-white/15 bg-white/[0.03] overflow-x-auto">
          {loadingPeople ? (
            <div className="p-10 text-center font-elite text-[0.65rem] tracking-[0.2em] uppercase text-white/40">
              Loading people...
            </div>
          ) : people.length === 0 ? (
            <div className="p-10 text-center font-crimson text-lg italic text-white/35">
              No persons on record.
            </div>
          ) : (
            <table className="w-full text-sm">
              <thead className="border-b border-white/10">
                <tr>
                  {["ID", "Case", "Name", "Role", "Age", "Description", "Actions"].map((h, i) => (
                    <th
                      key={h}
                      className={`p-4 font-elite text-[0.58rem] tracking-[0.2em] uppercase text-white/40 ${i === 6 ? "text-right" : "text-left"}`}
                    >
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>

              <tbody>
                {people.map((p) => (
                  <tr
                    key={p.id}
                    onClick={() => setDetailPerson(p)}
                    className="border-b border-white/5 hover:bg-white/[0.04] transition-colors duration-150 cursor-pointer"
                  >
                    <td className="p-4 font-elite text-[0.65rem] tracking-widest text-white/40">
                      #{p.id}
                    </td>

                    <td className="p-4 font-elite text-[0.65rem] tracking-wider text-white/60">
                      {caseMap[p.caseId] ?? `Case #${p.caseId}`}
                    </td>

                    <td className="p-4 font-crimson text-base text-white">
                      {p.fullName}
                    </td>

                    <td className="p-4">
                      <RoleBadge value={p.role} />
                    </td>

                    <td className="p-4 font-elite text-[0.65rem] tracking-wider text-white/60">
                      {p.age ?? "—"}
                    </td>

                    <td className="p-4 font-crimson text-sm text-white/55 max-w-[260px] truncate">
                      {p.description}
                    </td>

                    <td className="p-4">
                      {/* stopPropagation en el td de acciones para que los botones no abran el detalle */}
                      <div
                        className="flex justify-end gap-2"
                        onClick={(e) => e.stopPropagation()}
                      >
                        {(hasRole("ADMIN") || hasRole("DETECTIVE")) && (
                          <button
                            onClick={() => {
                              setEditingPerson(p);
                              setModalOpen(true);
                            }}
                            className="border border-white/20 text-white/50 font-elite text-[0.58rem] tracking-[0.12em] uppercase px-3 py-1.5 hover:border-white hover:text-white transition-all duration-200"
                          >
                            Edit
                          </button>
                        )}

                        {hasRole("ADMIN") && (
                          <button
                            onClick={() => handleDelete(p.id)}
                            className="border border-red-600/30 text-red-400/70 font-elite text-[0.58rem] tracking-[0.12em] uppercase px-3 py-1.5 hover:border-red-500 hover:text-red-400 transition-all duration-200"
                          >
                            Delete
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </section>

      </main>

      {/* ── Footer ── */}
      <footer className="relative z-10 border-t border-white/10 py-4 font-elite text-[0.58rem] tracking-[0.12em] text-white/30 uppercase flex gap-2 justify-center">
        <span>© {new Date().getFullYear()} Luna Lunera & Associates</span>
        <span className="text-white/20">·</span>
        <span>Confidential information — internal use only</span>
      </footer>

      {/* ── Person Detail Modal ── */}
      {detailPerson && (
        <PersonDetailModal
          person={detailPerson}
          caseTitle={caseMap[detailPerson.caseId] ?? `Case #${detailPerson.caseId}`}
          onClose={() => setDetailPerson(null)}
        />
      )}

      {/* ── Edit/Create Modal ── */}
      {modalOpen && (
        <PersonModal
          personData={editingPerson}
          cases={cases}
          onClose={() => setModalOpen(false)}
          onSaved={loadData}
        />
      )}

    </div>
  );
}