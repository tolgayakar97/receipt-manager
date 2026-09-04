import { useNavigate } from 'react-router-dom';
import AuthForm from '../components/AuthForm';

export default function LoginPage() {
  const navigate = useNavigate();
  return (
    <main className="auth">
      <AuthForm mode="login" onSuccess={() => navigate('/receipts', { replace: true })} onToggle={() => navigate('/register')} />
    </main>
  );
}
