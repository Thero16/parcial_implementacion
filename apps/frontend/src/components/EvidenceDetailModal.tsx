import { useEffect, useState } from "react";
import { getEvidenceCustodyHistory } from "../services/evidenceService";

interface EvidenceDetailModalProps {
  evidence: any;
  caseTitle: string;
  onClose: () => void;
}

// ── Type badge ────────────────────────────────────────────────
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
      className={`font-elite text-[0.58rem] tracking-[0.15em] uppercase px-2 py-1 border ${
        colors[value?.toUpperCase()] ?? "border-white/20 text-white/40"
      }`}
    >
      {value?.replace("_", " ")}
    </span>
  );
}

// ── Detail field ──────────────────────────────────────────────
function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div>
      <p className="font-elite text-[0.55rem] tracking-[0.22em] uppercase text-white/35 mb-1.5">
        {label}
      </p>
      <div className="font-crimson text-base text-white/75">{children}</div>
    </div>
  );
}

// ── Main modal ────────────────────────────────────────────────
export default function EvidenceDetailModal({
  evidence,
  caseTitle,
  onClose,
}: EvidenceDetailModalProps) {
  const [history, setHistory] = useState<any[]>([]);
  const [loadingHistory, setLoadingHistory] = useState(true);

  useEffect(() => {
    setLoadingHistory(true);
    getEvidenceCustodyHistory(evidence.evidenceId)
      .then(setHistory)
      .catch(console.error)
      .finally(() => setLoadingHistory(false));
  }, [evidence.evidenceId]);

  const dateCollected = evidence.dateCollected
    ? new Date(evidence.dateCollected).toLocaleString("en-GB", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
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
            <span>EVIDENCE RECORD</span>
            <span className="flex-1 h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.15),transparent)]" />
          </div>

          <div className="flex items-start justify-between gap-4">
            <div>
              <p className="font-elite text-[0.6rem] tracking-[0.2em] text-white/35 uppercase mb-1">
                #{evidence.evidenceId}
              </p>
              <h2
                className="font-playfair font-black text-3xl text-white leading-tight"
                style={{ textShadow: "0 0 30px rgba(255,255,255,0.12)" }}
              >
                {evidence.description}
              </h2>
            </div>
            <div className="flex flex-col items-end gap-2 flex-shrink-0">
              <TypeBadge value={evidence.evidenceType} />
              <CustodyBadge value={evidence.custodyStatus} />
            </div>
          </div>
        </div>

        {/* ── Body ── */}
        <div className="px-10 py-7 space-y-6">

          {/* Row 1: Case & Location */}
          <div className="grid grid-cols-2 gap-6">
            <Field label="Linked Case">{caseTitle}</Field>
            <Field label="Location Found">
              {evidence.locationFound ?? "—"}
            </Field>
          </div>

          <div className="h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.08),transparent)]" />

          {/* Row 2: Collected By & Date */}
          <div className="grid grid-cols-2 gap-6">
            <Field label="Collected By">
              {evidence.collectedBy ?? "—"}
            </Field>
            <Field label="Date Collected">{dateCollected}</Field>
          </div>

          <div className="h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.08),transparent)]" />

          {/* Row 3: Current Custodian & File URL */}
          <div className="grid grid-cols-2 gap-6">
            <Field label="Current Custodian">
              {evidence.currentCustodian ?? "—"}
            </Field>
            <Field label="File">
              {evidence.fileUrl ? (
                <a
                  href={evidence.fileUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-white/55 underline underline-offset-2 hover:text-white transition-colors duration-150 break-all"
                >
                  View file
                </a>
              ) : (
                <span className="italic text-white/30">No file attached</span>
              )}
            </Field>
          </div>

          <div className="h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.08),transparent)]" />

          {/* ── Chain of Custody ── */}
          <div>
            <div className="flex items-center gap-4 font-elite text-[0.55rem] tracking-[0.3em] text-white/35 uppercase mb-4">
              <span className="flex-1 h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.08),transparent)]" />
              <span>Chain of Custody</span>
              <span className="flex-1 h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.08),transparent)]" />
            </div>

            {loadingHistory ? (
              <p className="font-elite text-[0.6rem] tracking-widest uppercase text-white/30 text-center py-4">
                Loading history...
              </p>
            ) : history.length === 0 ? (
              <p className="font-crimson text-base italic text-white/30 text-center py-4">
                No custody transfers recorded for this evidence.
              </p>
            ) : (
              <div className="border border-white/10 overflow-hidden">
                <table className="w-full text-sm">
                  <thead className="border-b border-white/10">
                    <tr>
                      {["#", "From", "To", "Reason", "Date"].map((h) => (
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
                    {history.map((entry, i) => {
                      const date = entry.transferredAt
                        ? new Date(entry.transferredAt).toLocaleString("en-GB", {
                            day: "2-digit",
                            month: "short",
                            year: "numeric",
                            hour: "2-digit",
                            minute: "2-digit",
                          })
                        : "—";

                      return (
                        <tr
                          key={entry.historyId}
                          className={`${i !== history.length - 1 ? "border-b border-white/5" : ""}`}
                        >
                          <td className="px-4 py-2.5 font-elite text-[0.58rem] tracking-wider text-white/30">
                            #{entry.historyId}
                          </td>
                          <td className="px-4 py-2.5 font-crimson text-sm text-white/50">
                            {entry.previousCustodian ?? "—"}
                          </td>
                          <td className="px-4 py-2.5 font-crimson text-sm text-white/75">
                            {entry.newCustodian}
                          </td>
                          <td className="px-4 py-2.5 font-crimson text-sm text-white/45 italic max-w-[180px] truncate">
                            {entry.reason ?? "—"}
                          </td>
                          <td className="px-4 py-2.5 font-elite text-[0.58rem] tracking-wider text-white/35 whitespace-nowrap">
                            {date}
                          </td>
                        </tr>
                      );
                    })}
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