import { fetchWithAuth } from './fetchWithAuth';

const API_BASE = import.meta.env.VITE_API_URL;
const API = `${API_BASE}/cases`;

export async function getCases() {
  const res = await fetchWithAuth(API);
  if (!res.ok) throw new Error('Failed to fetch cases');
  return res.json();
}

export async function createCase(data: any) {
  const res = await fetchWithAuth(API, {
    method: 'POST',
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error('Failed to create case');
  return res.json();
}

export async function updateCase(id: number, data: any) {
  const res = await fetchWithAuth(`${API}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error('Failed to update case');
  return res.json();
}

export async function deleteCase(id: number) {
  const res = await fetchWithAuth(`${API}/${id}`, { method: 'DELETE' });
  if (!res.ok) throw new Error('Failed to delete case');
}