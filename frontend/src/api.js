export const API_BASE = 'http://localhost:8081/api/v1';

export async function apiFetch(path, options = {}) {
  const token = localStorage.getItem('token');
  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  });

  if (res.status === 401) {
    localStorage.removeItem('token');
    window.location.reload();
    throw new Error('Unauthorized');
  }

  if (res.status === 204) return null;

  const data = await res.json();
  if (!res.ok) throw new Error(data.message || 'API error');
  return data;
}
