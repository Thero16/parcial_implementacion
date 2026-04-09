import { fetchWithAuth } from './fetchWithAuth';

const API_BASE = import.meta.env.VITE_API_URL;
const API = `${API_BASE}/audit-logs`;

export async function getAuditLogs() {
  const res = await fetchWithAuth(API);
  if (!res.ok) throw new Error('Failed to fetch audit logs');
  return res.json();
}