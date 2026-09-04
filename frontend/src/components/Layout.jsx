import { NavLink, Outlet, useNavigate } from 'react-router-dom';

export default function Layout() {
  const navigate = useNavigate();

  function logout() {
    localStorage.removeItem('token');
    navigate('/login', { replace: true });
  }

  return (
    <div className="app">
      <aside>
        <div className="logo">RM</div>
        <nav>
          <NavLink to="/receipts">▣ <span>Fişler</span></NavLink>
          <NavLink to="/trash">⌫ <span>Çöp Kutusu</span></NavLink>
        </nav>
        <button className="logout" onClick={logout}>Çıkış</button>
      </aside>
      <main className="content"><Outlet /></main>
    </div>
  );
}
