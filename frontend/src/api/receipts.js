import { apiJson, apiRequest } from './client';

export function getReceipts(isDeleted = false) {
  return apiJson(`/receipts?isDeleted=${isDeleted}`);
}

export function getReceipt(id, isDeleted = false) {
  return apiJson(`/receipts/${id}?isDeleted=${isDeleted}`);
}

export function createReceipt({ file, name, description }) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('name', name);
  formData.append('description', description);

  return apiJson('/receipts', {
    method: 'POST',
    body: formData,
  });
}

export function updateReceipt(id, { name, description }) {
  return apiJson(`/receipts/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, description }),
  });
}

export function deleteReceipt(id) {
  return apiRequest(`/receipts/${id}`, { method: 'DELETE' });
}
