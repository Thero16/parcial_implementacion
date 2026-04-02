import { useEffect, useState } from "react";
import { getPeople } from "../services/peopleService";
import { getEvidences } from "../services/evidenceService";

interface CaseDetailModalProps {
  caseData: any;
  onClose: () => void;
}

// ── Priority badge ────────────────────────────────────────────
function PriorityBadge({ value }: { value: string }) {
  const colors: Record<string, string> = {
    LOW:    "border-green-500/40 text-green-400/80",
    MEDIUM: "border-yellow-500/40 text-yellow-400/80",
    HIGH:   "border-red-500/40 text-red-400/80",
  };
  return (
    <span className={`font-elite text-[0.58rem] tracking-[0.15em] uppercase px-2 py-1 border ${colors[value] ?? "border-white/20 text-white/40"}`}>
      {value}
    </span>
  );
}

// ── Status badge ──────────────────────────────────────────────
function StatusBadge({ value }: { value: string }) {
  const colors: Record<string, string> = {
    OPEN:        "border-blue-500/40 text-blue-400/80",
    IN_PROGRESS: "border-yellow-500/40 text-yellow-400/80",
    CLOSED:      "border-white/15 text-white/35",
  };
  return (
    <span className={`font-elite text-[0.58rem] tracking-[0.15em] uppercase px-2 py-1 border ${colors[value] ?? "border-white/20 text-white/40"}`}>
      {value?.replace("_", " ")}
    </span>
  );
}

// ── Role badge ────────────────────────────────────────────────
function RoleBadge({ value }: { value: string }) {
  const colors: Record<string, string> = {
    SUSPECT:   "border-red-500/40 text-red-400/80",
    WITNESS:   "border-blue-500/40 text-blue-400/80",
    VICTIM:    "border-purple-500/40 text-purple-400/80",
    INFORMANT: "border-yellow-500/40 text-yellow-400/80",
  };
  return (
    <span className={`font-elite text-[0.58rem] tracking-[0.15em] uppercase px-2 py-1 border ${colors[value?.toUpperCase()] ?? "border-white/20 text-white/40"}`}>
      {value}
    </span>
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
    <span className={`font-elite text-[0.58rem] tracking-[0.15em] uppercase px-2 py-1 border ${colors[value?.toUpperCase()] ?? "border-white/20 text-white/40"}`}>
      {value}
    </span>
  );
}

// ── Section divider ───────────────────────────────────────────
function SectionDivider({ label }: { label: string }) {
  return (
    <div className="flex items-center gap-4 font-elite text-[0.55rem] tracking-[0.3em] text-white/35 uppercase">
      <span className="flex-1 h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.08),transparent)]" />
      <span>{label}</span>
      <span className="flex-1 h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.08),transparent)]" />
    </div>
  );
}

// ── Main modal ────────────────────────────────────────────────
export default function CaseDetailModal({ caseData, onClose }: CaseDetailModalProps) {
  const [people, setPeople]           = useState<any[]>([]);
  const [evidences, setEvidences]     = useState<any[]>([]);
  const [loadingPeople, setLoadingPeople]       = useState(true);
  const [loadingEvidences, setLoadingEvidences] = useState(true);

  useEffect(() => {
    // Load all people, then filter by caseId
    getPeople()
      .then((data) => setPeople(data.filter((p: any) => p.caseId === caseData.id)))
      .catch(console.error)
      .finally(() => setLoadingPeople(false));

    // Load all evidences, then filter by caseId
    getEvidences()
      .then((data) => setEvidences(data.filter((e: any) => e.caseId === caseData.id)))
      .catch(console.error)
      .finally(() => setLoadingEvidences(false));
  }, [caseData.id]);

  const createdAt = caseData.createdAt
    ? new Date(caseData.createdAt).toLocaleDateString("en-GB", {
        day: "2-digit", month: "short", year: "numeric",
      })
    : "—";

  return (
    <div
      className="fixed inset-0 bg-black/85 backdrop-blur-sm flex items-center justify-center z-50 overflow-y-auto py-8"
      onClick={onClose}
    >
      <div
        className="bg-[#0a0a0a] border border-white/15 w-full max-w-2xl relative overflow-hidden my-auto"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Top accent line */}
        <div className="h-px w-full bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.2),transparent)]" />

        {/* Watermark */}
        <span className="absolute top-4 right-[-2rem] font-elite text-[0.55rem] tracking-[0.3em] text-red-600/10 rotate-12 pointer-events-none select-none whitespace-nowrap">
          CONFIDENTIAL
        </span>

        {/* ── Header ── */}
        <div className="px-10 pt-8 pb-6 border-b border-white/10">
          <div className="flex items-center gap-4 font-elite text-[0.55rem] tracking-[0.3em] text-white/35 uppercase mb-4">
            <span className="flex-1 h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.15),transparent)]" />
            <span>CASE FILE</span>
            <span className="flex-1 h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.15),transparent)]" />
          </div>

          <div className="flex items-start justify-between gap-4">
            <div>
              <p className="font-elite text-[0.6rem] tracking-[0.2em] text-white/35 uppercase mb-1">
                #{caseData.id}
              </p>
              <h2
                className="font-playfair font-black text-3xl text-white leading-tight"
                style={{ textShadow: "0 0 30px rgba(255,255,255,0.12)" }}
              >
                {caseData.title}
              </h2>
            </div>
            <div className="flex flex-col items-end gap-2 flex-shrink-0">
              <StatusBadge value={caseData.status} />
              <PriorityBadge value={caseData.priority} />
            </div>
          </div>
        </div>

        {/* ── Body ── */}
        <div className="px-10 py-7 space-y-6">

          {/* Row 1: Detective & Created */}
          <div className="grid grid-cols-2 gap-6">
            <div>
              <p className="font-elite text-[0.55rem] tracking-[0.22em] uppercase text-white/35 mb-1.5">
                Assigned Detective
              </p>
              <p className="font-crimson text-base text-white/75">
                {caseData.assignedDetective ?? "—"}
              </p>
            </div>
            <div>
              <p className="font-elite text-[0.55rem] tracking-[0.22em] uppercase text-white/35 mb-1.5">
                Opened
              </p>
              <p className="font-crimson text-base text-white/75">
                {createdAt}
              </p>
            </div>
          </div>

          {/* Description */}
          {caseData.description && (
            <>
              <div className="h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.08),transparent)]" />
              <div>
                <p className="font-elite text-[0.55rem] tracking-[0.22em] uppercase text-white/35 mb-2">
                  Description
                </p>
                <p className="font-crimson text-base text-white/70 leading-relaxed">
                  {caseData.description}
                </p>
              </div>
            </>
          )}

          <div className="h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.08),transparent)]" />

          {/* ── Persons of Interest ── */}
          <div className="space-y-3">
            <SectionDivider label="Persons of Interest" />

            {loadingPeople ? (
              <p className="font-elite text-[0.6rem] tracking-widest uppercase text-white/30 text-center py-3">
                Loading persons...
              </p>
            ) : people.length === 0 ? (
              <p className="font-crimson text-base italic text-white/30 text-center py-3">
                No persons linked to this case.
              </p>
            ) : (
              <div className="border border-white/10 overflow-hidden">
                <table className="w-full text-sm">
                  <thead className="border-b border-white/10">
                    <tr>
                      {["#", "Name", "Role", "Age"].map((h) => (
                        <th
                          key={h}
                          className="px-4 py-2.5 font-elite text-[0.55rem] tracking-[0.18em] uppercase text-white/30 text-left"
                        >
                          {h}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {people.map((p, i) => (
                      <tr
                        key={p.id}
                        className={i !== people.length - 1 ? "border-b border-white/5" : ""}
                      >
                        <td className="px-4 py-2.5 font-elite text-[0.58rem] tracking-wider text-white/30">
                          #{p.id}
                        </td>
                        <td className="px-4 py-2.5 font-crimson text-base text-white/75">
                          {p.fullName}
                        </td>
                        <td className="px-4 py-2.5">
                          <RoleBadge value={p.role} />
                        </td>
                        <td className="px-4 py-2.5 font-crimson text-sm text-white/45">
                          {p.age ?? "—"}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          <div className="h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.08),transparent)]" />

          {/* ── Evidence ── */}
          <div className="space-y-3">
            <SectionDivider label="Evidence" />

            {loadingEvidences ? (
              <p className="font-elite text-[0.6rem] tracking-widest uppercase text-white/30 text-center py-3">
                Loading evidence...
              </p>
            ) : evidences.length === 0 ? (
              <p className="font-crimson text-base italic text-white/30 text-center py-3">
                No evidence linked to this case.
              </p>
            ) : (
              <div className="border border-white/10 overflow-hidden">
                <table className="w-full text-sm">
                  <thead className="border-b border-white/10">
                    <tr>
                      {["#", "Type", "Description", "Custodian"].map((h) => (
                        <th
                          key={h}
                          className="px-4 py-2.5 font-elite text-[0.55rem] tracking-[0.18em] uppercase text-white/30 text-left"
                        >
                          {h}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {evidences.map((ev, i) => (
                      <tr
                        key={ev.evidenceId}
                        className={i !== evidences.length - 1 ? "border-b border-white/5" : ""}
                      >
                        <td className="px-4 py-2.5 font-elite text-[0.58rem] tracking-wider text-white/30">
                          #{ev.evidenceId}
                        </td>
                        <td className="px-4 py-2.5">
                          <TypeBadge value={ev.evidenceType} />
                        </td>
                        <td className="px-4 py-2.5 font-crimson text-sm text-white/70 max-w-[180px] truncate">
                          {ev.description}
                        </td>
                        <td className="px-4 py-2.5 font-crimson text-sm text-white/45">
                          {ev.currentCustodian ?? "—"}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

        </div>

        {/* ── Footer ── */}
        <div className="px-10 py-5 border-t border-white/10 flex justify-end">
          <button
            onClick={onClose}
            className="border border-white/25 text-white/55 font-elite text-[0.6rem] tracking-[0.14em] uppercase px-6 py-2 hover:border-white hover:text-white transition-all duration-200"
          >
            Close
          </button>
        </div>

        {/* Bottom accent line */}
        <div className="h-px w-full bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.1),transparent)]" />
      </div>
    </div>
  );
}