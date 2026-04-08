const API_BASE = import.meta.env.VITE_API_URL;
const API = `${API_BASE}/audit-logs`;

function getToken() {
  const match = document.cookie.match(/access_token=([^;]+)/);
  return match ? match[1] : null;
}

function authHeaders() {
  const token = getToken();
  return {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  };
}

export async function getAuditLogs() {
  const res = await fetch(API, { headers: authHeaders() });
  if (!res.ok) throw new Error("Failed to fetch audit logs");
  return res.json();
}
