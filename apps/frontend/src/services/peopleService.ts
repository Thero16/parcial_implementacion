import { fetchWithAuth } from './fetchWithAuth';

const API_BASE = import.meta.env.VITE_API_URL;
const API = `${API_BASE}/people`;

export async function getPeople() {
  const res = await fetchWithAuth(API);
  if (!res.ok) throw new Error('Failed to fetch people');
  return res.json();
}

export async function createPerson(data: any) {
  const res = await fetchWithAuth(API, {
    method: 'POST',
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error('Failed to create person');
  return res.json();
}

export async function updatePerson(id: number, data: any) {
  const res = await fetchWithAuth(`${API}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error('Failed to update person');
  return res.json();
}

export async function deletePerson(id: number) {
  const res = await fetchWithAuth(`${API}/${id}`, { method: 'DELETE' });
  if (!res.ok) throw new Error('Failed to delete person');
}