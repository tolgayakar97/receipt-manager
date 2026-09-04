import { Link } from 'react-router-dom';

export default function ReceiptCard({ receipt, isDeleted }) {
  return (
    <Link className="receipt" to={`/receipts/${receipt.id}${isDeleted ? '?deleted=true' : ''}`}>
      <div className="thumb">🧾</div>
      <div>
        <h3>{receipt.name || `Fiş #${receipt.id}`}</h3>
        <p>{receipt.description || 'Açıklama yok'}</p>
        <small>{receipt.createdAt ? new Date(receipt.createdAt).toLocaleDateString('tr-TR') : ''}</small>
      </div>
    </Link>
  );
}
