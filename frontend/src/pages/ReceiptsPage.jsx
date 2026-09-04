import { useState } from 'react';
import { useReceipts } from '../hooks/useReceipts';
import ReceiptCard from '../components/ReceiptCard';

export default function ReceiptsPage({ isDeleted = false }) {
  const { receipts, loading, error, uploadReceipt } = useReceipts(isDeleted);
  const [uploading, setUploading] = useState(false);
  const [uploadError, setUploadError] = useState('');

  async function handleUpload(event) {
    const file = event.target.files?.[0];
    event.target.value = '';
    if (!file) return;

    setUploading(true);
    setUploadError('');
    try {
      await uploadReceipt({ file, name: file.name, description: '' });
    } catch (err) {
      setUploadError(err.message || 'Fiş yüklenemedi.');
    } finally {
      setUploading(false);
    }
  }

  return (
    <>
      <header>
        <div>
          <p className="eyebrow">{isDeleted ? 'TRASH' : 'MY RECEIPTS'}</p>
          <h1>{isDeleted ? 'Çöp Kutusu' : 'Fişlerim'}</h1>
        </div>
        {!isDeleted && (
          <label className="upload">
            {uploading ? 'Yükleniyor...' : '+ Fiş yükle'}
            <input type="file" accept="image/*" disabled={uploading} onChange={handleUpload} />
          </label>
        )}
      </header>

      {(error || uploadError) && <p className="error">{error || uploadError}</p>}

      {loading ? (
        <div className="empty"><p>Fişler yükleniyor...</p></div>
      ) : receipts.length ? (
        <section className="grid">
          {receipts.map((receipt) => <ReceiptCard key={receipt.id} receipt={receipt} isDeleted={isDeleted} />)}
        </section>
      ) : (
        <div className="empty"><span>🧾</span><h2>Henüz fiş yok</h2><p>{isDeleted ? 'Çöp kutusunda fiş bulunmuyor.' : 'İlk fişini yükle, gerisini Receipt Manager halletsin.'}</p></div>
      )}
    </>
  );
}
