import { fetchWithAuth } from './fetchWithAuth';

const API_BASE = import.meta.env.VITE_API_URL;
const API = `${API_BASE}/tasks`;

export async function getTasks() {
  const res = await fetchWithAuth(API);
  if (!res.ok) throw new Error('Failed to fetch tasks');
  return res.json();
}

export async function getTasksByCase(caseId: number) {
  const res = await fetchWithAuth(`${API}/case/${caseId}`);
  if (!res.ok) throw new Error('Failed to fetch tasks for case');
  return res.json();
}

export async function createTask(data: any) {
  const res = await fetchWithAuth(API, {
    method: 'POST',
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error('Failed to create task');
  return res.json();
}

export async function updateTask(id: number, data: any) {
  const res = await fetchWithAuth(`${API}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error('Failed to update task');
  return res.json();
}

export async function deleteTask(id: number) {
  const res = await fetchWithAuth(`${API}/${id}`, { method: 'DELETE' });
  if (!res.ok) throw new Error('Failed to delete task');
}