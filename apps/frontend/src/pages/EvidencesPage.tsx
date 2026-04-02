import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { getEvidences, deleteEvidence, getEvidenceCustodyHistory } from "../services/evidenceService";
import { getCases } from "../services/caseService";
import { hasRole } from "../auth/roles.utils";
import EvidenceModal from "../components/EvidenceModal";
import EvidenceDetailModal from "../components/EvidenceDetailModal";

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

// ── Evidence type badge ───────────────────────────────────────
function TypeBadge({ value }: { value: string }) {
  const colors: Record<string, string> = {
    PHOTO:       "border-sky-500/40 text-sky-400/80",
    VIDEO:       "border-violet-500/40 text-violet-400/80",
    AUDIO:       "border-indigo-500/40 text-indigo-400/80",
    DOCUMENT:    "border-amber-500/40 text-amber-400/80",
    WEAPON:      "border-red-500/40 text-red-400/80",
    DNA:         "border-green-500/40 text-green-400/80",
    FINGERPRINT: "border-teal-500/40 text-teal-400/80",
    CLOTHING:    "border-pink-500/40 text-pink-400/80",
    OTHER:       "border-white/20 text-white/40",
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

// ── Custody status badge ──────────────────────────────────────
function CustodyBadge({ value }: { value: string }) {
  const colors: Record<string, string> = {
    COLLECTED:   "border-blue-500/40 text-blue-400/80",
    STORED:      "border-white/30 text-white/60",
    IN_ANALYSIS: "border-yellow-500/40 text-yellow-400/80",
    TRANSFERRED: "border-orange-500/40 text-orange-400/80",
    ARCHIVED:    "border-white/20 text-white/35",
  };

  return (
    <span
      className={`font-elite text-[0.56rem] tracking-[0.15em] uppercase px-2 py-1 border ${
        colors[value?.toUpperCase()] ?? "border-white/20 text-white/40"
      }`}
    >
      {value?.replace("_", " ")}
    </span>
  );
}

// ── Custody history row ───────────────────────────────────────
function CustodyHistoryRow({ entry }: { entry: any }) {
  const date = entry.transferredAt
    ? new Date(entry.transferredAt).toLocaleString("en-GB", {
        day: "2-digit", month: "short", year: "numeric",
        hour: "2-digit", minute: "2-digit",
      })
    : "—";

  return (
    <tr className="border-b border-white/5 last:border-0">
      <td className="px-4 py-2 font-elite text-[0.58rem] tracking-wider text-white/30">
        #{entry.historyId}
      </td>
      <td className="px-4 py-2 font-crimson text-sm text-white/50">
        {entry.previousCustodian ?? "—"}
      </td>
      <td className="px-4 py-2">
        <span className="font-crimson text-sm text-white/75">{entry.newCustodian}</span>
      </td>
      <td className="px-4 py-2 font-crimson text-sm text-white/45 italic max-w-[200px] truncate">
        {entry.reason ?? "—"}
      </td>
      <td className="px-4 py-2 font-elite text-[0.58rem] tracking-wider text-white/35 whitespace-nowrap">
        {date}
      </td>
    </tr>
  );
}

// ── Expanded custody panel ────────────────────────────────────
function CustodyPanel({
  evidenceId,
  open,
}: {
  evidenceId: number;
  open: boolean;
}) {
  const [history, setHistory] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [fetched, setFetched] = useState(false);

  useEffect(() => {
    if (open && !fetched) {
      setLoading(true);
      getEvidenceCustodyHistory(evidenceId)
        .then((data) => { setHistory(data); setFetched(true); })
        .catch(console.error)
        .finally(() => setLoading(false));
    }
  }, [open, evidenceId, fetched]);

  if (!open) return null;

  return (
    <tr>
      <td colSpan={9} className="px-0 py-0">
        <div className="mx-6 mb-4 border border-white/10 bg-white/[0.02]">
          <div className="flex items-center gap-4 px-4 py-2.5 border-b border-white/10">
            <span className="font-elite text-[0.55rem] tracking-[0.25em] uppercase text-white/40">
              Chain of Custody — Evidence #{evidenceId}
            </span>
          </div>

          {loading ? (
            <div className="px-4 py-4 font-elite text-[0.6rem] tracking-widest uppercase text-white/30">
              Loading history...
            </div>
          ) : history.length === 0 ? (
            <div className="px-4 py-4 font-crimson text-sm italic text-white/30">
              No custody transfers recorded for this evidence.
            </div>
          ) : (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-white/10">
                  {["#", "From", "To", "Reason", "Date"].map((h) => (
                    <th
                      key={h}
                      className="px-4 py-2 font-elite text-[0.55rem] tracking-[0.18em] uppercase text-white/30 text-left"
                    >
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {history.map((entry) => (
                  <CustodyHistoryRow key={entry.historyId} entry={entry} />
                ))}
              </tbody>
            </table>
          )}
        </div>
      </td>
    </tr>
  );
}

// ── Main EvidencesPage ────────────────────────────────────────
export default function EvidencesPage() {
  const { user, logout, loading, authenticated } = useAuth();

  const [evidences, setEvidences] = useState<any[]>([]);
  const [cases, setCases] = useState<any[]>([]);
  const [loadingEvidences, setLoadingEvidences] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingEvidence, setEditingEvidence] = useState<any>(null);
  const [expandedId, setExpandedId] = useState<number | null>(null);
  const [detailEvidence, setDetailEvidence] = useState<any>(null);

  async function loadData() {
    try {
      setLoadingEvidences(true);
      const [evidencesData, casesData] = await Promise.all([
        getEvidences(),
        getCases(),
      ]);
      setEvidences(evidencesData);
      setCases(casesData);
    } catch (err) {
      console.error("Failed to load evidences", err);
    } finally {
      setLoadingEvidences(false);
    }
  }

  useEffect(() => {
    if (authenticated) {
      loadData();
    }
  }, [authenticated]);

  async function handleDelete(id: number) {
    if (!confirm(`Delete evidence #${id}? This action cannot be undone.`)) return;
    try {
      await deleteEvidence(id);
      if (expandedId === id) setExpandedId(null);
      loadData();
    } catch (err) {
      console.error("Failed to delete evidence", err);
    }
  }

  function toggleExpand(id: number) {
    setExpandedId((prev) => (prev === id ? null : id));
  }

  const caseMap = useMemo(() => {
    const map: Record<number, string> = {};
    cases.forEach((c) => { map[c.id] = c.title; });
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
          <NavItem label="People" to="/dashboard/people" />
          <NavItem label="Evidences" to="/dashboard/evidences" active />
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
            <span>PHYSICAL &amp; DIGITAL EVIDENCE</span>
            <span className="flex-1 max-w-[120px] h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.2),transparent)]" />
          </div>

          <div className="flex items-center justify-between gap-6">
            <div>
              <h1
                className="font-playfair font-black text-4xl md:text-5xl text-white leading-tight mb-2"
                style={{ textShadow: "0 0 40px rgba(255,255,255,0.15)" }}
              >
                Evidences
              </h1>
              <p className="font-crimson text-base text-white/60 leading-relaxed">
                Collected items and their complete chain of custody
              </p>
            </div>

            {(hasRole("ADMIN") || hasRole("DETECTIVE")) && (
              <button
                onClick={() => {
                  setEditingEvidence(null);
                  setModalOpen(true);
                }}
                className="flex-shrink-0 border border-white/30 text-white/70 font-elite text-[0.6rem] tracking-[0.18em] uppercase px-5 py-2.5 transition-all duration-200 hover:border-white hover:text-white"
              >
                + New Evidence
              </button>
            )}
          </div>
        </section>

        {/* Table section */}
        <section className="border border-white/15 bg-white/[0.03] overflow-x-auto">
          {loadingEvidences ? (
            <div className="p-10 text-center font-elite text-[0.65rem] tracking-[0.2em] uppercase text-white/40">
              Loading evidences...
            </div>
          ) : evidences.length === 0 ? (
            <div className="p-10 text-center font-crimson text-lg italic text-white/35">
              No evidence on record.
            </div>
          ) : (
            <table className="w-full text-sm">
              <thead className="border-b border-white/10">
                <tr>
                  {[
                    "ID", "Case", "Type", "Description",
                    "Location Found", "Collected By", "Custodian",
                    "Status", "Actions",
                  ].map((h, i) => (
                    <th
                      key={h}
                      className={`p-4 font-elite text-[0.58rem] tracking-[0.2em] uppercase text-white/40 ${
                        i === 8 ? "text-right" : "text-left"
                      }`}
                    >
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>

              <tbody>
                {evidences.map((ev) => {
                  const isExpanded = expandedId === ev.evidenceId;

                  return (
                    <>
                      <tr
                        key={ev.evidenceId}
                        onClick={() => setDetailEvidence(ev)}
                        className={`border-b border-white/5 transition-colors duration-150 cursor-pointer ${
                          isExpanded
                            ? "bg-white/[0.06] border-white/10"
                            : "hover:bg-white/[0.04]"
                        }`}
                      >
                        {/* ID */}
                        <td className="p-4 font-elite text-[0.65rem] tracking-widest text-white/40">
                          #{ev.evidenceId}
                        </td>

                        {/* Case */}
                        <td className="p-4 font-elite text-[0.65rem] tracking-wider text-white/60">
                          {caseMap[ev.caseId] ?? `Case #${ev.caseId}`}
                        </td>

                        {/* Type */}
                        <td className="p-4">
                          <TypeBadge value={ev.evidenceType} />
                        </td>

                        {/* Description */}
                        <td className="p-4 font-crimson text-sm text-white/70 max-w-[180px] truncate">
                          {ev.description}
                        </td>

                        {/* Location Found */}
                        <td className="p-4 font-crimson text-sm text-white/50 max-w-[140px] truncate">
                          {ev.locationFound}
                        </td>

                        {/* Collected By */}
                        <td className="p-4 font-crimson text-sm text-white/55">
                          {ev.collectedBy}
                        </td>

                        {/* Current Custodian */}
                        <td className="p-4 font-crimson text-sm text-white/55">
                          {ev.currentCustodian}
                        </td>

                        {/* Status */}
                        <td className="p-4">
                          <CustodyBadge value={ev.custodyStatus} />
                        </td>

                        {/* Actions */}
                        <td className="p-4">
                          <div
                            className="flex justify-end gap-2"
                            onClick={(e) => e.stopPropagation()}
                          >
                            {/* Custody history toggle */}
                            <button
                              onClick={() => toggleExpand(ev.evidenceId)}
                              className={`border font-elite text-[0.58rem] tracking-[0.12em] uppercase px-3 py-1.5 transition-all duration-200 ${
                                isExpanded
                                  ? "border-white/40 text-white"
                                  : "border-white/15 text-white/35 hover:border-white/40 hover:text-white/70"
                              }`}
                            >
                              {isExpanded ? "Hide" : "History"}
                            </button>

                            {(hasRole("ADMIN") || hasRole("DETECTIVE")) && (
                              <button
                                onClick={() => {
                                  setEditingEvidence(ev);
                                  setModalOpen(true);
                                }}
                                className="border border-white/20 text-white/50 font-elite text-[0.58rem] tracking-[0.12em] uppercase px-3 py-1.5 hover:border-white hover:text-white transition-all duration-200"
                              >
                                Edit
                              </button>
                            )}

                            {hasRole("ADMIN") && (
                              <button
                                onClick={() => handleDelete(ev.evidenceId)}
                                className="border border-red-600/30 text-red-400/70 font-elite text-[0.58rem] tracking-[0.12em] uppercase px-3 py-1.5 hover:border-red-500 hover:text-red-400 transition-all duration-200"
                              >
                                Delete
                              </button>
                            )}
                          </div>
                        </td>
                      </tr>

                      {/* Custody history expandable panel */}
                      <CustodyPanel
                        key={`custody-${ev.evidenceId}`}
                        evidenceId={ev.evidenceId}
                        open={isExpanded}
                      />
                    </>
                  );
                })}
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

      {/* ── Evidence Detail Modal ── */}
      {detailEvidence && (
        <EvidenceDetailModal
          evidence={detailEvidence}
          caseTitle={caseMap[detailEvidence.caseId] ?? `Case #${detailEvidence.caseId}`}
          onClose={() => setDetailEvidence(null)}
        />
      )}

      {/* ── Edit/Create Modal ── */}
      {modalOpen && (
        <EvidenceModal
          evidenceData={editingEvidence}
          cases={cases}
          onClose={() => setModalOpen(false)}
          onSaved={loadData}
        />
      )}

    </div>
  );
}