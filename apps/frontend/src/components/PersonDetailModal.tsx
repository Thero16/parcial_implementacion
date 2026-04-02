interface PersonDetailModalProps {
  person: any;
  caseTitle: string;
  onClose: () => void;
}

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

export default function PersonDetailModal({ person, caseTitle, onClose }: PersonDetailModalProps) {
  return (
    <div
      className="fixed inset-0 bg-black/85 backdrop-blur-sm flex items-center justify-center z-50"
      onClick={onClose}
    >
      <div
        className="bg-[#0a0a0a] border border-white/15 w-full max-w-lg relative overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >

        {/* Top accent line */}
        <div className="h-px w-full bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.2),transparent)]" />

        {/* Watermark */}
        <span className="absolute top-4 right-[-2rem] font-elite text-[0.55rem] tracking-[0.3em] text-red-600/10 rotate-12 pointer-events-none select-none whitespace-nowrap">
          CONFIDENTIAL
        </span>

        {/* Header */}
        <div className="px-10 pt-8 pb-6 border-b border-white/10">
          <div className="flex items-center gap-4 font-elite text-[0.55rem] tracking-[0.3em] text-white/35 uppercase mb-4">
            <span className="flex-1 h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.15),transparent)]" />
            <span>PERSON OF INTEREST</span>
            <span className="flex-1 h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.15),transparent)]" />
          </div>

          <div className="flex items-start justify-between gap-4">
            <div>
              <p className="font-elite text-[0.6rem] tracking-[0.2em] text-white/35 uppercase mb-1">
                #{person.id}
              </p>
              <h2
                className="font-playfair font-black text-3xl text-white leading-tight"
                style={{ textShadow: "0 0 30px rgba(255,255,255,0.12)" }}
              >
                {person.fullName}
              </h2>
            </div>
            <RoleBadge value={person.role} />
          </div>
        </div>

        {/* Body */}
        <div className="px-10 py-7 space-y-6">

          {/* Case & Age row */}
          <div className="grid grid-cols-2 gap-6">
            <div>
              <p className="font-elite text-[0.55rem] tracking-[0.22em] uppercase text-white/35 mb-1.5">
                Linked Case
              </p>
              <p className="font-crimson text-base text-white/75">
                {caseTitle}
              </p>
            </div>

            <div>
              <p className="font-elite text-[0.55rem] tracking-[0.22em] uppercase text-white/35 mb-1.5">
                Age
              </p>
              <p className="font-crimson text-base text-white/75">
                {person.age ?? "—"}
              </p>
            </div>
          </div>

          {/* Divider */}
          <div className="h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.08),transparent)]" />

          {/* Description */}
          <div>
            <p className="font-elite text-[0.55rem] tracking-[0.22em] uppercase text-white/35 mb-2.5">
              Description
            </p>
            {person.description ? (
              <p className="font-crimson text-base text-white/70 leading-relaxed">
                {person.description}
              </p>
            ) : (
              <p className="font-crimson text-base italic text-white/30">
                No description on record.
              </p>
            )}
          </div>

        </div>

        {/* Footer */}
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