import { useState } from 'react';
import { initiateLogin } from '../services/auth.service';

// ── Dust particle — floats in background ─────────────────────
function DustParticle({ index }: { index: number }) {
  const left = `${index * 5.8}%`;
  const top  = `${index * 6.3}%`;
  const delay = `${index * 0.8}s`;
  return (
    <span
      className="absolute w-0.5 h-0.5 rounded-full bg-gold-dim opacity-30 animate-float pointer-events-none"
      style={{ left, top, animationDelay: delay }}
    />
  );
}

// ── Moon SVG emblem ───────────────────────────────────────────
function MoonEmblem() {
  return (
    <div className="flex justify-center mb-8">
      <div className="relative w-22 h-22 flex items-center justify-center">
        <div className="w-[88px] h-[88px] rounded-full border border-white/30 flex items-center justify-center bg-[radial-gradient(circle,rgba(255,255,255,0.06)_0%,transparent_70%)] animate-pulse-glow">
          <div className="absolute -inset-1.5 rounded-full border border-white/15 pointer-events-none" />
          <div className="w-[52px] h-[52px] text-white">
            <svg viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg" className="w-full h-full">
              <circle cx="40" cy="40" r="28" stroke="currentColor" strokeWidth="1.5" strokeDasharray="4 2" />
              <path d="M40 16 C28 16 20 27 20 40 C20 53 28 64 40 64 C34 58 31 50 31 40 C31 30 34 22 40 16Z" fill="currentColor" opacity="0.8" />
              <circle cx="40" cy="40" r="3" fill="currentColor" />
              <line x1="40" y1="8"  x2="40" y2="14" stroke="currentColor" strokeWidth="1.5" />
              <line x1="40" y1="66" x2="40" y2="72" stroke="currentColor" strokeWidth="1.5" />
              <line x1="8"  y1="40" x2="14" y2="40" stroke="currentColor" strokeWidth="1.5" />
              <line x1="66" y1="40" x2="72" y2="40" stroke="currentColor" strokeWidth="1.5" />
            </svg>
          </div>
        </div>
      </div>
    </div>
  );
}

// ── Divider with star ─────────────────────────────────────────
function Divider() {
  return (
    <div className="flex items-center gap-3 mx-auto w-4/5 mb-5">
      <span className="flex-1 h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.35),transparent)]" />
      <span className="text-white/60 text-[0.6rem]">✦</span>
      <span className="flex-1 h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.35),transparent)]" />
    </div>
  );
}

// ── Main Login Page ───────────────────────────────────────────
export default function LoginPage() {
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState<string | null>(null);

  const handleLogin = async () => {
    try {
      setLoading(true);
      setError(null);
      await initiateLogin();
    } catch {
      setError('Unable to connect to the authentication server.');
      setLoading(false);
    }
  };

  return (
    <div className="relative min-h-screen flex items-center justify-center overflow-hidden bg-black p-8">

      {/* ── Fixed background layers ── */}
      <div className="fixed inset-0 pointer-events-none z-0">
        <div className="absolute inset-0 opacity-[0.03] bg-[url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIzMDAiIGhlaWdodD0iMzAwIj48ZmlsdGVyIGlkPSJub2lzZSI+PGZlVHVyYnVsZW5jZSB0eXBlPSJmcmFjdGFsTm9pc2UiIGJhc2VGcmVxdWVuY3k9IjAuNjUiIG51bU9jdGF2ZXM9IjMiIHN0aXRjaFRpbGVzPSJzdGl0Y2giLz48L2ZpbHRlcj48cmVjdCB3aWR0aD0iMzAwIiBoZWlnaHQ9IjMwMCIgZmlsdGVyPSJ1cmwoI25vaXNlKSIgb3BhY2l0eT0iMSIvPjwvc3ZnPg==')]" />
        <div className="absolute inset-0 bg-[radial-gradient(ellipse_60%_50%_at_50%_50%,rgba(255,255,255,0.05)_0%,transparent_70%)]" />
        <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.025)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.025)_1px,transparent_1px)] bg-[size:48px_48px]" />
      </div>

      {/* ── Dust particles ── */}
      <div className="fixed inset-0 pointer-events-none z-[1]">
        {Array.from({ length: 18 }).map((_, i) => <DustParticle key={i} index={i} />)}
      </div>

      {/* ── Side decorative text ── */}
      <div className="fixed left-6 top-1/2 pointer-events-none z-[2] font-elite text-[0.55rem] tracking-[0.35em] text-white/20 uppercase whitespace-nowrap"
           style={{ transform: 'translateY(-50%) rotate(-90deg) translateX(-50%)', transformOrigin: 'left center' }}
           aria-hidden="true">
        CONFIDENTIAL · DISCREET · EFFECTIVE
      </div>
      <div className="fixed right-6 top-1/2 pointer-events-none z-[2] font-elite text-[0.55rem] tracking-[0.35em] text-white/20 uppercase whitespace-nowrap"
           style={{ transform: 'translateY(-50%) rotate(90deg) translateX(50%)', transformOrigin: 'right center' }}
           aria-hidden="true">
        EST. MMI · INVESTIGATION · SURVEILLANCE
      </div>

      {/* ── Card ── */}
      <div className="relative z-10 w-full max-w-[480px] bg-[#0d0d0d] border border-white/20 shadow-[0_0_80px_rgba(255,255,255,0.06)] px-12 pt-12 pb-10 text-center animate-card-appear">

        <MoonEmblem />

        {/* Agency name — era /35, ahora /65 */}
        <p className="font-elite text-[0.62rem] tracking-[0.22em] text-white/65 uppercase mb-2">
          — Private Investigation Agency —
        </p>
        <h1 className="font-playfair text-5xl font-black italic text-white leading-none mb-1"
            style={{ textShadow: '0 0 40px rgba(255,255,255,0.18)' }}>
          Luna Lunera
        </h1>
        {/* "& Associates" — era /40, ahora /70 */}
        <p className="font-crimson text-base italic tracking-[0.12em] text-white/70 mb-6">
          & Associates
        </p>

        <Divider />

        {/* Quote — era /35, ahora con borde lateral y fondo sutil */}
        <div className="mx-auto mb-8 px-4 py-3 border-l-2 border-white/30 bg-white/[0.03] text-left">
          <p className="font-crimson text-sm italic text-white/80 leading-relaxed">
            "Truth doesn't hide. It merely waits to be found."
          </p>
        </div>

        {/* Login block */}
        <div className="flex flex-col items-center gap-4">
          {/* era /30, ahora /65 */}
          <p className="font-elite text-[0.7rem] tracking-[0.18em] text-white/65 uppercase">
            Restricted access. Authorized personnel only.
          </p>

          {error && (
            <div role="alert" className="w-full bg-red-950/40 border border-red-500/40 text-red-300 px-4 py-3 font-crimson text-sm flex items-center gap-2">
              <span>⚠</span> {error}
            </div>
          )}

          <button
            onClick={handleLogin}
            disabled={loading}
            aria-busy={loading}
            className="
              group relative w-full py-4 px-8
              bg-transparent border border-white/70 text-white
              font-elite text-[0.8rem] tracking-[0.22em] uppercase
              flex items-center justify-center gap-3
              overflow-hidden transition-colors duration-300
              disabled:opacity-40 disabled:cursor-not-allowed
              hover:text-black
            "
          >
            <span className="absolute inset-0 bg-white translate-x-[-101%] group-hover:translate-x-0 transition-transform duration-300 ease-[cubic-bezier(0.22,1,0.36,1)] -z-[0] group-disabled:translate-x-[-101%]" />
            <span className="relative z-10 flex items-center gap-3">
              {loading ? (
                <>
                  <span className="inline-block w-3.5 h-3.5 border border-current border-t-transparent rounded-full animate-spin" />
                  Connecting...
                </>
              ) : (
                <>
                  <span>🔑</span>
                  Sign In
                </>
              )}
            </span>
          </button>

          {/* era /20, ahora /55 */}
          <p className="font-crimson text-[0.78rem] italic text-white/55">
            Redirects to Keycloak for secure authentication.
          </p>
        </div>

        {/* Footer — era /15, ahora /45 */}
        <div className="mt-10 pt-6 border-t border-white/15 font-elite text-[0.6rem] tracking-[0.1em] text-white/45 uppercase flex flex-wrap justify-center gap-2">
          <span>© {new Date().getFullYear()} Luna Lunera & Associates</span>
          <span className="text-white/30">·</span>
          <span>All rights reserved</span>
        </div>
      </div>
    </div>
  );
}