import { fetchWithAuth } from './fetchWithAuth';

const API_BASE = import.meta.env.VITE_API_URL;
const API = `${API_BASE}/evidences`;

export async function getEvidences() {
  const res = await fetchWithAuth(API);
  if (!res.ok) throw new Error('Failed to fetch evidences');
  return res.json();
}

export async function getEvidenceById(id: number) {
  const res = await fetchWithAuth(`${API}/${id}`);
  if (!res.ok) throw new Error('Failed to fetch evidence');
  return res.json();
}

export async function getEvidenceCustodyHistory(id: number) {
  const res = await fetchWithAuth(`${API}/${id}/custody-history`);
  if (!res.ok) throw new Error('Failed to fetch custody history');
  return res.json();
}

export async function createEvidence(data: any) {
  const res = await fetchWithAuth(API, {
    method: 'POST',
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error('Failed to create evidence');
  return res.json();
}

export async function updateEvidence(id: number, data: any) {
  const res = await fetchWithAuth(`${API}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error('Failed to update evidence');
  return res.json();
}

export async function deleteEvidence(id: number) {
  const res = await fetchWithAuth(`${API}/${id}`, { method: 'DELETE' });
  if (!res.ok) throw new Error('Failed to delete evidence');
}