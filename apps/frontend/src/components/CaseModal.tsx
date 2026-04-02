import { useState } from "react";
import { createCase, updateCase } from "../services/caseService";

export default function CaseModal({ caseData, onClose, onSaved }: any) {

  const [title, setTitle] = useState(caseData?.title || "");
  const [description, setDescription] = useState(caseData?.description || "");
  const [priority, setPriority] = useState(caseData?.priority || "LOW");
  const [status, setStatus] = useState(caseData?.status || "OPEN");
  const [detective, setDetective] = useState(caseData?.assignedDetective || "");

  async function handleSubmit(e: any) {
    e.preventDefault();

    const payload = {
      title,
      description,
      priority,
      status,
      assignedDetective: detective,
      createdAt: new Date().toISOString()
    };

    if (caseData) {
      await updateCase(caseData.id, payload);
    } else {
      await createCase(payload);
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
    <div className="fixed inset-0 bg-black/85 backdrop-blur-sm flex items-center justify-center z-50">

      <form
        onSubmit={handleSubmit}
        className="bg-[#0a0a0a] border border-white/15 p-10 w-full max-w-lg space-y-5 relative overflow-hidden"
      >

        {/* Watermark */}
        <span className="absolute top-4 right-[-2rem] font-elite text-[0.55rem] tracking-[0.3em] text-red-600/10 rotate-12 pointer-events-none select-none whitespace-nowrap">
          CONFIDENTIAL
        </span>

        {/* Divider title */}
        <div className="flex items-center gap-4 font-elite text-[0.55rem] tracking-[0.3em] text-white/35 uppercase mb-2">
          <span className="flex-1 h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.15),transparent)]" />
          <span>{caseData ? "EDIT RECORD" : "NEW RECORD"}</span>
          <span className="flex-1 h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.15),transparent)]" />
        </div>

        <h2 className="font-playfair font-black text-2xl text-white" style={{ textShadow: "0 0 30px rgba(255,255,255,0.1)" }}>
          {caseData ? `Case #${caseData.id}` : "New Case"}
        </h2>

        {/* Title */}
        <div>
          <label className={labelClass}>Case Title</label>
          <input
            placeholder="e.g. The Midnight Cipher"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className={inputClass}
            required
          />
        </div>

        {/* Description */}
        <div>
          <label className={labelClass}>Description</label>
          <textarea
            placeholder="Brief summary of the investigation..."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={3}
            className={`${inputClass} resize-none`}
          />
        </div>

        {/* Detective */}
        <div>
          <label className={labelClass}>Assigned Detective</label>
          <input
            placeholder="Full name of the detective in charge"
            value={detective}
            onChange={(e) => setDetective(e.target.value)}
            className={inputClass}
          />
        </div>

        {/* Priority & Status side by side */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className={labelClass}>Priority</label>
            <select
              value={priority}
              onChange={(e) => setPriority(e.target.value)}
              className={selectClass}
            >
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
          </div>

          <div>
            <label className={labelClass}>Status</label>
            <select
              value={status}
              onChange={(e) => setStatus(e.target.value)}
              className={selectClass}
            >
              <option value="OPEN">Open</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="CLOSED">Closed</option>
            </select>
          </div>
        </div>

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
            {caseData ? "Save Changes" : "Create Case"}
          </button>
        </div>

      </form>
    </div>
  );
}