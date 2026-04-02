import { useState } from "react";
import { createPerson, updatePerson } from "../services/peopleService";

export default function PersonModal({ personData, cases, onClose, onSaved }: any) {

  const [fullName, setFullName] = useState(personData?.fullName || "");
  const [role, setRole] = useState(personData?.role || "SUSPECT");
  const [age, setAge] = useState(personData?.age ?? "");
  const [description, setDescription] = useState(personData?.description || "");
  const [caseId, setCaseId] = useState<number | "">(personData?.caseId || "");

  async function handleSubmit(e: any) {
    e.preventDefault();

    if (personData) {
      // PersonUpdateDTO — sin caseId
      const payload = {
        fullName,
        role,
        age: age !== "" ? Number(age) : null,
        description,
      };
      await updatePerson(personData.id, payload);
    } else {
      // PersonCreateDTO — con caseId obligatorio
      const payload = {
        caseId: Number(caseId),
        fullName,
        role,
        age: age !== "" ? Number(age) : null,
        description,
      };
      await createPerson(payload);
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

  const isEditing = !!personData;

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
          <span>{isEditing ? "EDIT RECORD" : "NEW RECORD"}</span>
          <span className="flex-1 h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.15),transparent)]" />
        </div>

        <h2 className="font-playfair font-black text-2xl text-white" style={{ textShadow: "0 0 30px rgba(255,255,255,0.1)" }}>
          {isEditing ? `Person #${personData.id}` : "New Person"}
        </h2>

        {/* Full Name */}
        <div>
          <label className={labelClass}>Full Name</label>
          <input
            placeholder="e.g. John H. Watson"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            className={inputClass}
            required
          />
        </div>

        {/* Role & Age side by side */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className={labelClass}>Role</label>
            <select
              value={role}
              onChange={(e) => setRole(e.target.value)}
              className={selectClass}
            >
              <option value="SUSPECT">Suspect</option>
              <option value="WITNESS">Witness</option>
              <option value="VICTIM">Victim</option>
              <option value="INFORMANT">Informant</option>
            </select>
          </div>

          <div>
            <label className={labelClass}>Age</label>
            <input
              type="number"
              min={0}
              max={120}
              placeholder="—"
              value={age}
              onChange={(e) => setAge(e.target.value)}
              className={inputClass}
            />
          </div>
        </div>

        {/* Linked Case — solo al crear, caseId no existe en PersonUpdateDTO */}
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

        {/* Description */}
        <div>
          <label className={labelClass}>Description</label>
          <textarea
            placeholder="Notes on this person's involvement..."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={3}
            className={`${inputClass} resize-none`}
          />
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
            {isEditing ? "Save Changes" : "Create Person"}
          </button>
        </div>

      </form>
    </div>
  );
}