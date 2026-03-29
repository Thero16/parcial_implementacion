import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { handleCallback } from '../services/auth.service';
import { useAuth } from '../context/AuthContext';

export default function CallbackPage() {
  const navigate = useNavigate();
  const { refreshUser } = useAuth();
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const code  = params.get('code');
    const state = params.get('state');
    const err   = params.get('error');

    if (err) {
      setError(`Keycloak: ${params.get('error_description') || err}`);
      return;
    }
    if (!code || !state) {
      setError('Invalid authentication parameters.');
      return;
    }

    handleCallback(code, state)
      .then(async () => {
        await refreshUser(); // ← sync context with new tokens before navigating
        navigate('/dashboard', { replace: true });
      })
      .catch((e: Error) => setError(e.message));
  }, [navigate, refreshUser]);

  // ── Error state ──────────────────────────────────────────────
  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-black p-8">

        {/* Background layers */}
        <div className="fixed inset-0 pointer-events-none z-0">
          <div className="absolute inset-0 opacity-[0.03] bg-[url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIzMDAiIGhlaWdodD0iMzAwIj48ZmlsdGVyIGlkPSJub2lzZSI+PGZlVHVyYnVsZW5jZSB0eXBlPSJmcmFjdGFsTm9pc2UiIGJhc2VGcmVxdWVuY3k9IjAuNjUiIG51bU9jdGF2ZXM9IjMiIHN0aXRjaFRpbGVzPSJzdGl0Y2giLz48L2ZpbHRlcj48cmVjdCB3aWR0aD0iMzAwIiBoZWlnaHQ9IjMwMCIgZmlsdGVyPSJ1cmwoI25vaXNlKSIgb3BhY2l0eT0iMSIvPjwvc3ZnPg==')]" />
          <div className="absolute inset-0 bg-[radial-gradient(ellipse_60%_50%_at_50%_50%,rgba(255,255,255,0.05)_0%,transparent_70%)]" />
          <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.025)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.025)_1px,transparent_1px)] bg-[size:48px_48px]" />
        </div>

        <div className="relative z-10 w-full max-w-sm bg-[#0d0d0d] border border-white/20 p-12 text-center shadow-[0_0_80px_rgba(255,255,255,0.04)]">
          <div className="text-5xl text-red-400 mb-4">✗</div>
          <h2 className="font-playfair text-2xl text-red-300 mb-3">Access denied</h2>
          <p className="font-crimson text-sm italic text-white/55 mb-6">{error}</p>
          <button
            onClick={() => navigate('/login', { replace: true })}
            className="
              border border-white/60 text-white bg-transparent
              font-elite text-[0.72rem] tracking-[0.18em] uppercase
              px-6 py-2.5 transition-all duration-200
              hover:bg-white hover:text-black
            "
          >
            Back to login
          </button>
        </div>
      </div>
    );
  }

  // ── Loading state ────────────────────────────────────────────
  return (
    <div className="min-h-screen flex items-center justify-center bg-black">

      {/* Background layers */}
      <div className="fixed inset-0 pointer-events-none z-0">
        <div className="absolute inset-0 opacity-[0.03] bg-[url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIzMDAiIGhlaWdodD0iMzAwIj48ZmlsdGVyIGlkPSJub2lzZSI+PGZlVHVyYnVsZW5jZSB0eXBlPSJmcmFjdGFsTm9pc2UiIGJhc2VGcmVxdWVuY3k9IjAuNjUiIG51bU9jdGF2ZXM9IjMiIHN0aXRjaFRpbGVzPSJzdGl0Y2giLz48L2ZpbHRlcj48cmVjdCB3aWR0aD0iMzAwIiBoZWlnaHQ9IjMwMCIgZmlsdGVyPSJ1cmwoI25vaXNlKSIgb3BhY2l0eT0iMSIvPjwvc3ZnPg==')]" />
        <div className="absolute inset-0 bg-[radial-gradient(ellipse_60%_50%_at_50%_50%,rgba(255,255,255,0.05)_0%,transparent_70%)]" />
        <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.025)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.025)_1px,transparent_1px)] bg-[size:48px_48px]" />
      </div>

      <div className="relative z-10 w-full max-w-sm bg-[#0d0d0d] border border-white/20 p-12 text-center shadow-[0_0_80px_rgba(255,255,255,0.04)]">
        <div className="flex justify-center mb-6">
          <div className="w-14 h-14 text-white animate-moon-spin">
            <svg viewBox="0 0 60 60" fill="none" xmlns="http://www.w3.org/2000/svg" className="w-full h-full">
              <circle cx="30" cy="30" r="22" stroke="currentColor" strokeWidth="1" strokeDasharray="5 3" />
              <path d="M30 12 C21 12 15 20 15 30 C15 40 21 48 30 48 C25 43 23 37 23 30 C23 23 25 17 30 12Z"
                fill="currentColor" opacity="0.7" />
            </svg>
          </div>
        </div>
        <h2 className="font-playfair text-xl font-bold text-white mb-2">Verifying credentials</h2>
        <p className="font-crimson text-sm italic text-white/55">One moment, agent...</p>
      </div>
    </div>
  );
}