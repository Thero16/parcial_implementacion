const API_BASE = import.meta.env.VITE_API_URL;
const API = `${API_BASE}/tasks`;

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

export async function getTasks() {
  const res = await fetch(API, { headers: authHeaders() });
  if (!res.ok) throw new Error("Failed to fetch tasks");
  return res.json();
}

export async function getTasksByCase(caseId: number) {
  const res = await fetch(`${API}/case/${caseId}`, { headers: authHeaders() });
  if (!res.ok) throw new Error("Failed to fetch tasks for case");
  return res.json();
}

export async function createTask(data: any) {
  const res = await fetch(API, {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error("Failed to create task");
  return res.json();
}

export async function updateTask(id: number, data: any) {
  const res = await fetch(`${API}/${id}`, {
    method: "PUT",
    headers: authHeaders(),
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error("Failed to update task");
  return res.json();
}

export async function deleteTask(id: number) {
  const res = await fetch(`${API}/${id}`, {
    method: "DELETE",
    headers: authHeaders(),
  });
  if (!res.ok) throw new Error("Failed to delete task");
}
