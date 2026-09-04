import { useNavigate } from 'react-router-dom';
import AuthForm from '../components/AuthForm';

export default function RegisterPage() {
  const navigate = useNavigate();
  return (
    <main className="auth">
      <AuthForm mode="register" onSuccess={() => navigate('/login', { replace: true })} onToggle={() => navigate('/login')} />
    </main>
  );
}
