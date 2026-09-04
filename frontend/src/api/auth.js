import { apiJson, apiRequest } from './client';

export function login(request) {
  return apiRequest('/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  }).then((response) => response.text());
}

export function register(request) {
  return apiJson('/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });
}
