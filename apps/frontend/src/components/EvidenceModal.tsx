import { useState } from "react";
import { createEvidence, updateEvidence } from "../services/evidenceService";

const EVIDENCE_TYPES = [
  "PHOTO", "VIDEO", "AUDIO", "DOCUMENT",
  "WEAPON", "DNA", "FINGERPRINT", "CLOTHING", "OTHER",
];

const CUSTODY_STATUSES = [
  "COLLECTED", "STORED", "IN_ANALYSIS", "TRANSFERRED", "ARCHIVED",
];

function toDatetimeLocal(isoString: string | undefined) {
  if (!isoString) return "";
  // Slice to "YYYY-MM-DDTHH:mm"
  return isoString.slice(0, 16);
}

export default function EvidenceModal({ evidenceData, cases, onClose, onSaved }: any) {
  const isEditing = !!evidenceData;

  const [caseId, setCaseId] = useState<number | "">(evidenceData?.caseId || "");
  const [evidenceType, setEvidenceType] = useState(evidenceData?.evidenceType || "PHOTO");
  const [description, setDescription] = useState(evidenceData?.description || "");
  const [locationFound, setLocationFound] = useState(evidenceData?.locationFound || "");
  const [dateCollected, setDateCollected] = useState(
    toDatetimeLocal(evidenceData?.dateCollected)
  );
  const [collectedBy, setCollectedBy] = useState(evidenceData?.collectedBy || "");
  const [fileUrl, setFileUrl] = useState(evidenceData?.fileUrl || "");
  const [custodyStatus, setCustodyStatus] = useState(evidenceData?.custodyStatus || "COLLECTED");
  const [currentCustodian, setCurrentCustodian] = useState(evidenceData?.currentCustodian || "");
  const [transferReason, setTransferReason] = useState("");

  async function handleSubmit(e: any) {
    e.preventDefault();

    if (isEditing) {
      const payload: any = {
        evidenceType,
        description,
        locationFound,
        dateCollected,
        collectedBy,
        fileUrl: fileUrl || null,
        custodyStatus,
        currentCustodian,
        transferReason: transferReason || null,
      };
      await updateEvidence(evidenceData.evidenceId, payload);
    } else {
      const payload: any = {
        caseId: Number(caseId),
        evidenceType,
        description,
        locationFound,
        dateCollected,
        collectedBy,
        fileUrl: fileUrl || null,
        custodyStatus,
        currentCustodian,
      };
      await createEvidence(payload);
    }

    onSaved();
    onClose();
  }

  const inputClass =
    "w-full bg-white/5 border border-white/20 p-2.5 text-white text-sm font-crimson placeholder:text-white/30 focus:outline-none focus:border-white/50 transition-colors duration-200";

  const selectClass =
    "w-full bg-white/5 border border-white/20 p-2.5 text-white text-sm font-elite tracking-wider focus:outline-none focus:border-white/50 transition-colors duration-200 [&>option]:bg-neutral-900 [&>option]:text-white";

  const labelClass =
    "block font-elite text-[0.58rem] tracking-[0.2em] uppercase text-white/45 mb-1.5";

  return (
    <div className="fixed inset-0 bg-black/85 backdrop-blur-sm flex items-center justify-center z-50 overflow-y-auto py-8">
      <form
        onSubmit={handleSubmit}
        className="bg-[#0a0a0a] border border-white/15 p-10 w-full max-w-lg space-y-5 relative overflow-hidden my-auto"
      >
        {/* Watermark */}
        <span className="absolute top-4 right-[-2rem] font-elite text-[0.55rem] tracking-[0.3em] text-red-600/10 rotate-12 pointer-events-none select-none whitespace-nowrap">
          CONFIDENTIAL
        </span>

        {/* Divider title */}
        <div className="flex items-center gap-4 font-elite text-[0.55rem] tracking-[0.3em] text-white/35 uppercase mb-2">
          <span className="flex-1 h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.15),transparent)]" />
          <span>{isEditing ? "EDIT RECORD" : "NEW RECORD"}</span>
          <span className="flex-1 h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.15),transparent)]" />
        </div>

        <h2
          className="font-playfair font-black text-2xl text-white"
          style={{ textShadow: "0 0 30px rgba(255,255,255,0.1)" }}
        >
          {isEditing ? `Evidence #${evidenceData.evidenceId}` : "New Evidence"}
        </h2>

        {/* Linked Case — only on create */}
        {!isEditing && (
          <div>
            <label className={labelClass}>Linked Case</label>
            {cases && cases.length > 0 ? (
              <select
                value={caseId}
                onChange={(e) => setCaseId(Number(e.target.value))}
                className={selectClass}
                required
              >
                <option value="" disabled>Select a case...</option>
                {cases.map((c: any) => (
                  <option key={c.id} value={c.id}>
                    #{c.id} — {c.title}
                  </option>
                ))}
              </select>
            ) : (
              <p className="font-elite text-[0.62rem] tracking-wider text-white/30 italic py-2">
                No cases available.
              </p>
            )}
          </div>
        )}

        {/* Type & Custody Status side by side */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className={labelClass}>Evidence Type</label>
            <select
              value={evidenceType}
              onChange={(e) => setEvidenceType(e.target.value)}
              className={selectClass}
            >
              {EVIDENCE_TYPES.map((t) => (
                <option key={t} value={t}>
                  {t.charAt(0) + t.slice(1).toLowerCase()}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className={labelClass}>Custody Status</label>
            <select
              value={custodyStatus}
              onChange={(e) => setCustodyStatus(e.target.value)}
              className={selectClass}
            >
              {CUSTODY_STATUSES.map((s) => (
                <option key={s} value={s}>
                  {s.replace("_", " ")}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Description */}
        <div>
          <label className={labelClass}>Description</label>
          <textarea
            placeholder="Describe the evidence..."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={3}
            className={`${inputClass} resize-none`}
            required
          />
        </div>

        {/* Location Found */}
        <div>
          <label className={labelClass}>Location Found</label>
          <input
            placeholder="e.g. 221B Baker Street, Room 3"
            value={locationFound}
            onChange={(e) => setLocationFound(e.target.value)}
            className={inputClass}
            required
          />
        </div>

        {/* Date Collected & Collected By side by side */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className={labelClass}>Date Collected</label>
            <input
              type="datetime-local"
              value={dateCollected}
              onChange={(e) => setDateCollected(e.target.value)}
              className={`${inputClass} [color-scheme:dark]`}
              required
            />
          </div>

          <div>
            <label className={labelClass}>Collected By</label>
            <input
              placeholder="Agent name..."
              value={collectedBy}
              onChange={(e) => setCollectedBy(e.target.value)}
              className={inputClass}
              required
            />
          </div>
        </div>

        {/* Current Custodian */}
        <div>
          <label className={labelClass}>Current Custodian</label>
          <input
            placeholder="Who currently holds this evidence"
            value={currentCustodian}
            onChange={(e) => setCurrentCustodian(e.target.value)}
            className={inputClass}
            required
          />
        </div>

        {/* File URL (optional) */}
        <div>
          <label className={labelClass}>File URL <span className="text-white/25 normal-case tracking-normal">(optional)</span></label>
          <input
            placeholder="https://..."
            value={fileUrl}
            onChange={(e) => setFileUrl(e.target.value)}
            className={inputClass}
          />
        </div>

        {/* Transfer Reason — only on edit */}
        {isEditing && (
          <div>
            <label className={labelClass}>
              Transfer Reason <span className="text-white/25 normal-case tracking-normal">(optional)</span>
            </label>
            <input
              placeholder="Reason for custody change, if applicable..."
              value={transferReason}
              onChange={(e) => setTransferReason(e.target.value)}
              className={inputClass}
            />
          </div>
        )}

        {/* Actions */}
        <div className="flex justify-end gap-3 pt-3 border-t border-white/10">
          <button
            type="button"
            onClick={onClose}
            className="border border-white/20 text-white/50 font-elite text-[0.6rem] tracking-[0.14em] uppercase px-5 py-2 hover:border-white/40 hover:text-white/70 transition-all duration-200"
          >
            Cancel
          </button>

          <button
            type="submit"
            className="border border-white text-white font-elite text-[0.6rem] tracking-[0.14em] uppercase px-5 py-2 hover:bg-white hover:text-black transition-all duration-200"
          >
            {isEditing ? "Save Changes" : "Create Evidence"}
          </button>
        </div>
      </form>
    </div>
  );
}