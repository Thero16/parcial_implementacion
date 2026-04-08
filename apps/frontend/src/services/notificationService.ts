const API_BASE = import.meta.env.VITE_API_URL;
const API = `${API_BASE}/notifications`;

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

export async function getNotifications() {
  const res = await fetch(API, { headers: authHeaders() });
  if (!res.ok) throw new Error("Failed to fetch notifications");
  return res.json();
}

export async function getUnreadNotifications() {
  const res = await fetch(`${API}/unread`, { headers: authHeaders() });
  if (!res.ok) throw new Error("Failed to fetch unread notifications");
  return res.json();
}

export async function markAsRead(id: number) {
  const res = await fetch(`${API}/${id}/read`, {
    method: "PUT",
    headers: authHeaders(),
  });
  if (!res.ok) throw new Error("Failed to mark notification as read");
  return res.json();
}
