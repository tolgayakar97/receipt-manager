import { useState } from 'react';
import { useAuth } from '../hooks/useAuth';

export default function AuthForm({ mode, onSuccess, onToggle }) {
  const { loading, error, signIn, signUp } = useAuth();
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  async function handleSubmit(event) {
    event.preventDefault();
    if (mode === 'login') {
      const token = await signIn(email, password);
      if (token) onSuccess();
      return;
    }

    const result = await signUp(firstName, lastName, email, password);
    if (result) onSuccess();
  }

  return (
    <section className="authCard">
      <div className="brand">RM</div>
      <p className="eyebrow">RECEIPT MANAGER</p>
      <h1>{mode === 'login' ? 'Tekrar hoş geldin.' : 'Hesabını oluştur.'}</h1>
      <p className="muted">Fişlerini tek yerde sakla ve OCR ile dijitalleştir.</p>

      <form onSubmit={handleSubmit}>
        {mode === 'register' && (
          <>
            <input placeholder="Ad" value={firstName} onChange={(e) => setFirstName(e.target.value)} required />
            <input placeholder="Soyad" value={lastName} onChange={(e) => setLastName(e.target.value)} required />
          </>
        )}
        <input type="email" placeholder="E-posta" value={email} onChange={(e) => setEmail(e.target.value)} required />
        <input type="password" placeholder="Şifre" value={password} onChange={(e) => setPassword(e.target.value)} required />
        <button disabled={loading}>{loading ? 'Bekle...' : mode === 'login' ? 'Giriş yap' : 'Kayıt ol'}</button>
      </form>

      {error && <p className="error">{error}</p>}
      <button className="link" onClick={onToggle}>
        {mode === 'login' ? 'Hesabın yok mu? Kayıt ol' : 'Zaten hesabın var mı? Giriş yap'}
      </button>
    </section>
  );
}
