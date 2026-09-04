import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { useReceipt, useReceipts } from '../hooks/useReceipts';

export default function ReceiptDetailPage() {
  const { id } = useParams();
  const [searchParams] = useSearchParams();
  const isDeleted = searchParams.get('deleted') === 'true';
  const navigate = useNavigate();
  const { receipt, loading, error } = useReceipt(id, isDeleted);
  const { removeReceipt } = useReceipts(isDeleted);

  async function handleDelete() {
    await removeReceipt(id);
    navigate(isDeleted ? '/trash' : '/receipts', { replace: true });
  }

  if (loading) return <div className="empty"><p>Fiş yükleniyor...</p></div>;
  if (error) return <p className="error">{error}</p>;
  if (!receipt) return <div className="empty"><h2>Fiş bulunamadı</h2></div>;

  return (
    <section className="detail">
      <button className="back" onClick={() => navigate(-1)}>← Geri</button>
      <div className="detailCard">
        <div className="thumb large">🧾</div>
        <div>
          <p className="eyebrow">RECEIPT #{receipt.id}</p>
          <h1>{receipt.name || `Fiş #${receipt.id}`}</h1>
          <p>{receipt.description || 'Açıklama yok'}</p>
          <small>Oluşturulma: {receipt.createdAt ? new Date(receipt.createdAt).toLocaleString('tr-TR') : '-'}</small>
          <p className="path">Dosya: {receipt.filePath || '-'}</p>
          {!isDeleted && <button className="danger" onClick={handleDelete}>Fişi sil</button>}
        </div>
      </div>
    </section>
  );
}
