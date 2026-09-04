import { useState } from 'react';
import { login, register } from '../api/auth';

export function useAuth() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function signIn(email, password) {
    setLoading(true);
    setError('');
    try {
      const token = await login({ email, password });
      localStorage.setItem('token', token);
      return token;
    } catch (err) {
      setError(err.message || 'Giriş yapılamadı.');
      return null;
    } finally {
      setLoading(false);
    }
  }

  async function signUp(firstName, lastName, email, password) {
    setLoading(true);
    setError('');
    try {
      return await register({ firstName, lastName, email, password });
    } catch (err) {
      setError(err.message || 'Kayıt oluşturulamadı.');
      return null;
    } finally {
      setLoading(false);
    }
  }

  function signOut() {
    localStorage.removeItem('token');
  }

  return { loading, error, signIn, signUp, signOut };
}
