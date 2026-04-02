const API_BASE= import.meta.env.VITE_API_URL;

const API = `${API_BASE}/evidences`;

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

export async function getEvidences() {
  const res = await fetch(API, { headers: authHeaders() });
  if (!res.ok) throw new Error("Failed to fetch evidences");
  return res.json();
}

export async function getEvidenceById(id: number) {
  const res = await fetch(`${API}/${id}`, { headers: authHeaders() });
  if (!res.ok) throw new Error("Failed to fetch evidence");
  return res.json();
}

export async function getEvidenceCustodyHistory(id: number) {
  const res = await fetch(`${API}/${id}/custody-history`, { headers: authHeaders() });
  if (!res.ok) throw new Error("Failed to fetch custody history");
  return res.json();
}

export async function createEvidence(data: any) {
  const res = await fetch(API, {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error("Failed to create evidence");
  return res.json();
}

export async function updateEvidence(id: number, data: any) {
  const res = await fetch(`${API}/${id}`, {
    method: "PUT",
    headers: authHeaders(),
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error("Failed to update evidence");
  return res.json();
}

export async function deleteEvidence(id: number) {
  const res = await fetch(`${API}/${id}`, {
    method: "DELETE",
    headers: authHeaders(),
  });
  if (!res.ok) throw new Error("Failed to delete evidence");
}