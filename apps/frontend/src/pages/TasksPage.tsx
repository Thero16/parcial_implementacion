import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { getTasks, createTask, updateTask, deleteTask } from "../services/taskService";
import { getCases } from "../services/caseService";
import { hasRole } from "../auth/roles.utils";

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

function NavItem({ label, to, active = false }: { label: string; to: string; active?: boolean }) {
  return (
    <Link
      to={to}
      className={`relative font-elite text-[0.65rem] tracking-[0.18em] uppercase cursor-pointer pb-1 transition-colors duration-200 after:absolute after:bottom-[-4px] after:left-0 after:right-0 after:h-px after:bg-white after:transition-transform after:duration-200 ${
        active ? "text-white after:scale-x-100" : "text-white/45 hover:text-white after:scale-x-0 hover:after:scale-x-100"
      }`}
    >
      {label}
    </Link>
  );
}

function StatusBadge({ value }: { value: string }) {
  const colors: Record<string, string> = {
    PENDING:     "border-yellow-500/40 text-yellow-400/80",
    IN_PROGRESS: "border-blue-500/40 text-blue-400/80",
    COMPLETED:   "border-green-500/40 text-green-400/80",
    OVERDUE:     "border-red-500/40 text-red-400/80",
  };
  return (
    <span className={`font-elite text-[0.58rem] tracking-[0.15em] uppercase px-2 py-1 border ${colors[value] ?? "border-white/20 text-white/40"}`}>
      {value?.replace("_", " ")}
    </span>
  );
}

function PriorityBadge({ value }: { value: string }) {
  const colors: Record<string, string> = {
    LOW:    "border-white/20 text-white/40",
    MEDIUM: "border-amber-500/40 text-amber-400/80",
    HIGH:   "border-red-500/40 text-red-400/80",
  };
  return (
    <span className={`font-elite text-[0.58rem] tracking-[0.15em] uppercase px-2 py-1 border ${colors[value] ?? "border-white/20 text-white/40"}`}>
      {value}
    </span>
  );
}

const EMPTY_FORM = { title: "", description: "", caseId: "", priority: "MEDIUM", assignedTo: "", dueDate: "" };

export default function TasksPage() {
  const { user, logout, loading, authenticated } = useAuth();
  const [tasks, setTasks] = useState<any[]>([]);
  const [cases, setCases] = useState<any[]>([]);
  const [loadingTasks, setLoadingTasks] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<any>(null);
  const [form, setForm] = useState({ ...EMPTY_FORM });

  async function load() {
    try {
      setLoadingTasks(true);
      const [tasksData, casesData] = await Promise.all([getTasks(), getCases()]);
      setTasks(tasksData);
      setCases(casesData);
    } catch (e) {
      console.error(e);
    } finally {
      setLoadingTasks(false);
    }
  }

  useEffect(() => { if (authenticated) load(); }, [authenticated]);

  function openCreate() { setEditing(null); setForm({ ...EMPTY_FORM }); setModalOpen(true); }
  function openEdit(t: any) {
    setEditing(t);
    setForm({ title: t.title, description: t.description ?? "", caseId: t.caseId, priority: t.priority, assignedTo: t.assignedTo ?? "", dueDate: t.dueDate ? t.dueDate.slice(0, 16) : "" });
    setModalOpen(true);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    const payload = { ...form, caseId: Number(form.caseId), dueDate: form.dueDate ? form.dueDate + ":00" : null };
    try {
      if (editing) await updateTask(editing.id, payload);
      else await createTask(payload);
      setModalOpen(false);
      load();
    } catch (err) { console.error(err); }
  }

  async function handleDelete(id: number) {
    if (!confirm(`Delete task #${id}?`)) return;
    try { await deleteTask(id); load(); } catch (e) { console.error(e); }
  }

  if (loading) return <div className="min-h-screen bg-black flex items-center justify-center text-white/50 font-elite tracking-widest text-xs uppercase">Verifying credentials...</div>;
  if (!authenticated) return null;

  const displayName = user?.name || [user?.given_name, user?.family_name].filter(Boolean).join(" ") || user?.preferred_username || "Unknown Agent";
  const initials = displayName.split(" ").slice(0, 2).map((w: string) => w[0]?.toUpperCase() ?? "").join("");

  const selectClass = "bg-black border border-white/20 text-white font-elite text-[0.65rem] tracking-wider uppercase px-3 py-2 focus:outline-none focus:border-white/50 [&>option]:bg-neutral-900 [&>option]:text-white";
  const inputClass  = "bg-transparent border border-white/20 text-white font-crimson text-base px-3 py-2 focus:outline-none focus:border-white/50";
  const labelClass  = "font-elite text-[0.6rem] tracking-[0.15em] uppercase text-white/50";

  return (
    <div className="min-h-screen flex flex-col bg-black relative">
      <div className="fixed inset-0 pointer-events-none z-0">
        <div className="absolute inset-0 opacity-[0.03] bg-[url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIzMDAiIGhlaWdodD0iMzAwIj48ZmlsdGVyIGlkPSJub2lzZSI+PGZlVHVyYnVsZW5jZSB0eXBlPSJmcmFjdGFsTm9pc2UiIGJhc2VGcmVxdWVuY3k9IjAuNjUiIG51bU9jdGF2ZXM9IjMiIHN0aXRjaFRpbGVzPSJzdGl0Y2giLz48L2ZpbHRlcj48cmVjdCB3aWR0aD0iMzAwIiBoZWlnaHQ9IjMwMCIgZmlsdGVyPSJ1cmwoI25vaXNlKSIgb3BhY2l0eT0iMSIvPjwvc3ZnPg==')]" />
        <div className="absolute inset-0 bg-[radial-gradient(ellipse_60%_50%_at_50%_50%,rgba(255,255,255,0.05)_0%,transparent_70%)]" />
        <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.025)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.025)_1px,transparent_1px)] bg-[size:48px_48px]" />
      </div>

      <header className="relative z-20 flex items-center gap-6 px-10 h-16 border-b border-white/10 bg-black/90 backdrop-blur-sm animate-slide-down">
        <div className="flex items-center gap-3 flex-shrink-0">
          <MoonLogo />
          <span className="font-playfair text-base font-bold text-white whitespace-nowrap">Luna Lunera <em className="font-normal not-italic text-white/55">& Associates</em></span>
        </div>
        <nav className="hidden md:flex flex-1 items-center justify-center gap-8">
          <NavItem label="Home" to="/dashboard" />
          <NavItem label="Cases" to="/dashboard/cases" />
          <NavItem label="People" to="/dashboard/people" />
          <NavItem label="Evidences" to="/dashboard/evidences" />
          <NavItem label="Tasks" to="/dashboard/tasks" active />
          <NavItem label="Notifications" to="/dashboard/notifications" />
          {hasRole("ADMIN") && <NavItem label="Audit" to="/dashboard/audit" />}
        </nav>
        <div className="flex items-center gap-3 flex-shrink-0 ml-auto">
          <div className="w-8 h-8 rounded-full border border-white/20 bg-white/10 text-white font-playfair font-bold text-sm flex items-center justify-center">{initials}</div>
          <button onClick={logout} className="border border-white/30 text-white/55 font-elite text-[0.6rem] tracking-[0.14em] uppercase px-3 py-1.5 transition-all duration-200 hover:border-white hover:text-white">Sign out</button>
        </div>
      </header>

      <main className="relative z-10 flex-1 max-w-[1200px] w-full mx-auto px-10 py-10 flex flex-col gap-8 animate-fade-up">
        <section className="relative border border-white/15 bg-white/[0.03] px-12 py-8 overflow-hidden">
          <span className="absolute top-4 right-[-2.5rem] font-elite text-[0.6rem] tracking-[0.3em] text-red-600/15 rotate-12 pointer-events-none select-none whitespace-nowrap">CONFIDENTIAL</span>
          <div className="flex items-center gap-4 font-elite text-[0.58rem] tracking-[0.3em] text-white/45 uppercase mb-5">
            <span className="flex-1 max-w-[120px] h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.2),transparent)]" />
            <span>WORKFLOW REGISTRY</span>
            <span className="flex-1 max-w-[120px] h-px bg-[linear-gradient(90deg,transparent,rgba(255,255,255,0.2),transparent)]" />
          </div>
          <div className="flex items-center justify-between gap-6">
            <div>
              <h1 className="font-playfair font-black text-4xl md:text-5xl text-white leading-tight mb-2" style={{ textShadow: "0 0 40px rgba(255,255,255,0.15)" }}>Tasks</h1>
              <p className="font-crimson text-base text-white/60">Manage investigation workflow tasks</p>
            </div>
            {(hasRole("ADMIN") || hasRole("DETECTIVE")) && (
              <button onClick={openCreate} className="flex-shrink-0 border border-white/30 text-white/70 font-elite text-[0.6rem] tracking-[0.18em] uppercase px-5 py-2.5 transition-all duration-200 hover:border-white hover:text-white">+ New Task</button>
            )}
          </div>
        </section>

        <section className="border border-white/15 bg-white/[0.03] overflow-x-auto">
          {loadingTasks ? (
            <div className="p-10 text-center font-elite text-[0.65rem] tracking-[0.2em] uppercase text-white/40">Loading tasks...</div>
          ) : tasks.length === 0 ? (
            <div className="p-10 text-center font-crimson text-lg italic text-white/35">No tasks on record.</div>
          ) : (
            <table className="w-full text-sm">
              <thead className="border-b border-white/10">
                <tr>
                  {["ID", "Title", "Case", "Assigned To", "Priority", "Status", "Due Date", "Actions"].map((h, i) => (
                    <th key={h} className={`p-4 font-elite text-[0.58rem] tracking-[0.2em] uppercase text-white/40 ${i === 7 ? "text-right" : "text-left"}`}>{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {tasks.map((t) => (
                  <tr key={t.id} className="border-b border-white/5 hover:bg-white/[0.04] transition-colors duration-150">
                    <td className="p-4 font-elite text-[0.65rem] tracking-widest text-white/40">#{t.id}</td>
                    <td className="p-4 font-crimson text-base text-white">{t.title}</td>
                    <td className="p-4 font-elite text-[0.65rem] tracking-wider text-white/60">
                      {cases.find(c => c.id === t.caseId)?.title ?? `#${t.caseId}`}
                    </td>
                    <td className="p-4 font-elite text-[0.65rem] tracking-wider text-white/60">{t.assignedTo ?? "—"}</td>
                    <td className="p-4"><PriorityBadge value={t.priority} /></td>
                    <td className="p-4"><StatusBadge value={t.status} /></td>
                    <td className="p-4 font-elite text-[0.62rem] tracking-wider text-white/40">{t.dueDate ? new Date(t.dueDate).toLocaleDateString() : "—"}</td>
                    <td className="p-4 text-right">
                      <div className="flex items-center justify-end gap-3" onClick={(e) => e.stopPropagation()}>
                        {(hasRole("ADMIN") || hasRole("DETECTIVE")) && (
                          <button onClick={() => openEdit(t)} className="font-elite text-[0.58rem] tracking-[0.15em] uppercase text-white/40 hover:text-white transition-colors">Edit</button>
                        )}
                        {hasRole("ADMIN") && (
                          <button onClick={() => handleDelete(t.id)} className="font-elite text-[0.58rem] tracking-[0.15em] uppercase text-red-500/50 hover:text-red-400 transition-colors">Delete</button>
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

      <footer className="relative z-10 border-t border-white/10 py-4 font-elite text-[0.58rem] tracking-[0.12em] text-white/30 uppercase flex gap-2 justify-center">
        <span>© {new Date().getFullYear()} Luna Lunera & Associates</span>
        <span className="text-white/20">·</span>
        <span>Confidential information — internal use only</span>
      </footer>

      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm">
          <div className="relative w-full max-w-lg border border-white/15 bg-black/95 p-8">
            <button onClick={() => setModalOpen(false)} className="absolute top-4 right-4 text-white/30 hover:text-white font-elite text-xs tracking-widest uppercase">✕ Close</button>
            <h2 className="font-playfair font-bold text-2xl text-white mb-6">{editing ? "Edit Task" : "New Task"}</h2>
            <form onSubmit={handleSubmit} className="flex flex-col gap-4">

              {/* Title */}
              <div className="flex flex-col gap-1">
                <label className={labelClass}>Title *</label>
                <input type="text" required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} className={inputClass} />
              </div>

              {/* Description */}
              <div className="flex flex-col gap-1">
                <label className={labelClass}>Description</label>
                <input type="text" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} className={inputClass} />
              </div>

              {/* Case select */}
              <div className="flex flex-col gap-1">
                <label className={labelClass}>Case *</label>
                <select
                  required
                  value={form.caseId}
                  onChange={(e) => setForm({ ...form, caseId: e.target.value })}
                  className={selectClass}
                >
                  <option value="" disabled>Select a case...</option>
                  {cases.map((c) => (
                    <option key={c.id} value={c.id}>#{c.id} — {c.title}</option>
                  ))}
                </select>
              </div>

              {/* Assigned To */}
              <div className="flex flex-col gap-1">
                <label className={labelClass}>Assigned To</label>
                <input type="text" value={form.assignedTo} onChange={(e) => setForm({ ...form, assignedTo: e.target.value })} className={inputClass} />
              </div>

              {/* Due Date */}
              <div className="flex flex-col gap-1">
                <label className={labelClass}>Due Date</label>
                <input type="datetime-local" value={form.dueDate} onChange={(e) => setForm({ ...form, dueDate: e.target.value })} className={`${inputClass} [color-scheme:dark]`} />
              </div>

              {/* Priority */}
              <div className="flex flex-col gap-1">
                <label className={labelClass}>Priority *</label>
                <select value={form.priority} onChange={(e) => setForm({ ...form, priority: e.target.value })} className={selectClass}>
                  {["LOW", "MEDIUM", "HIGH"].map((v) => <option key={v} value={v}>{v}</option>)}
                </select>
              </div>

              <button type="submit" className="mt-2 border border-white/30 text-white/70 font-elite text-[0.62rem] tracking-[0.18em] uppercase px-5 py-3 hover:border-white hover:text-white transition-all">
                {editing ? "Save Changes" : "Create Task"}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}