import { fetchWithAuth } from './fetchWithAuth';

const API_BASE = import.meta.env.VITE_API_URL;
const API = `${API_BASE}/notifications`;

export async function getNotifications() {
  const res = await fetchWithAuth(API);
  if (!res.ok) throw new Error('Failed to fetch notifications');
  return res.json();
}

export async function getUnreadNotifications() {
  const res = await fetchWithAuth(`${API}/unread`);
  if (!res.ok) throw new Error('Failed to fetch unread notifications');
  return res.json();
}

export async function markAsRead(id: number) {
  const res = await fetchWithAuth(`${API}/${id}/read`, { method: 'PUT' });
  if (!res.ok) throw new Error('Failed to mark notification as read');
  return res.json();
}