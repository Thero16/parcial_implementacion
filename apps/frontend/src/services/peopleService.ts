const API_BASE= import.meta.env.VITE_API_URL;

const API = `${API_BASE}/people`;

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

export async function getPeople() {
  const res = await fetch(API, {
    headers: authHeaders(),
  });

  if (!res.ok) throw new Error("Failed to fetch people");

  return res.json();
}

export async function createPerson(data: any) {
  const res = await fetch(API, {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify(data),
  });

  if (!res.ok) throw new Error("Failed to create person");

  return res.json();
}

export async function updatePerson(id: number, data: any) {
  const res = await fetch(`${API}/${id}`, {
    method: "PUT",
    headers: authHeaders(),
    body: JSON.stringify(data),
  });

  if (!res.ok) throw new Error("Failed to update person");

  return res.json();
}

export async function deletePerson(id: number) {
  const res = await fetch(`${API}/${id}`, {
    method: "DELETE",
    headers: authHeaders(),
  });

  if (!res.ok) throw new Error("Failed to delete person");
}