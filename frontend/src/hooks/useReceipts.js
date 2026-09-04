import { useCallback, useEffect, useState } from 'react';
import { createReceipt, deleteReceipt, getReceipt, getReceipts, updateReceipt } from '../api/receipts';

export function useReceipts(isDeleted = false) {
  const [receipts, setReceipts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadReceipts = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      setReceipts(await getReceipts(isDeleted));
    } catch (err) {
      setError(err.message || 'Fişler yüklenemedi.');
    } finally {
      setLoading(false);
    }
  }, [isDeleted]);

  useEffect(() => {
    loadReceipts();
  }, [loadReceipts]);

  async function uploadReceipt(data) {
    const receipt = await createReceipt(data);
    await loadReceipts();
    return receipt;
  }

  async function removeReceipt(id) {
    await deleteReceipt(id);
    await loadReceipts();
  }

  async function editReceipt(id, data) {
    const receipt = await updateReceipt(id, data);
    await loadReceipts();
    return receipt;
  }

  return { receipts, loading, error, reload: loadReceipts, uploadReceipt, removeReceipt, editReceipt };
}

export function useReceipt(id, isDeleted = false) {
  const [receipt, setReceipt] = useState(null);
  const [loading, setLoading] = useState(Boolean(id));
  const [error, setError] = useState('');

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getReceipt(id, isDeleted)
      .then(setReceipt)
      .catch((err) => setError(err.message || 'Fiş yüklenemedi.'))
      .finally(() => setLoading(false));
  }, [id, isDeleted]);

  return { receipt, loading, error };
}
