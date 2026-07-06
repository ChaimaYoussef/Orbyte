import { useState, useEffect } from 'react';
import Login from './pages/Login';
import Sidebar from './components/Sidebar';
import Dashboard from './pages/Dashboard';
import UserDashboard from './pages/UserDashboard';
import Agents from './pages/Agents';
import Chat from './pages/Chat';
import Documents from './pages/Documents';
import Connectors from './pages/Connectors';
import Users from './pages/Users';

import Audit from './pages/Audit';
import './index.css';
import { LayoutDashboard, Bot, MessageSquare, FileText, Plug, Users as UsersIcon, ClipboardList } from 'lucide-react';

const API_BASE = 'http://localhost:8081/api/v1';

/* ================================================================
   Role-based navigation configuration
   ADMIN  → all pages
   CURATOR → Dashboard, Agents, Chat, Documents, Connectors, Models
   USER   → Dashboard, Chat, Documents
   VIEWER → Dashboard, Chat (read-only)
   ================================================================ */
const NAV_BY_ROLE = {
  ADMIN: [
    { page: 'dashboard',  icon: <LayoutDashboard size={20} />,  label: 'Dashboard' },
    { page: 'agents',     icon: <Bot size={20} />, label: 'Agents IA' },
    { page: 'chat',       icon: <MessageSquare size={20} />, label: 'Chat' },
    { page: 'documents',  icon: <FileText size={20} />, label: 'Documents' },
    { page: 'connectors', icon: <Plug size={20} />, label: 'Connecteurs' },
    { page: 'users',      icon: <UsersIcon size={20} />, label: 'Utilisateurs' },
    { page: 'audit',      icon: <ClipboardList size={20} />, label: 'Audit' },
  ],
  CURATOR: [
    { page: 'dashboard',  icon: <LayoutDashboard size={20} />,  label: 'Dashboard' },
    { page: 'agents',     icon: <Bot size={20} />, label: 'Agents IA' },
    { page: 'chat',       icon: <MessageSquare size={20} />, label: 'Chat' },
    { page: 'documents',  icon: <FileText size={20} />, label: 'Documents' },
    { page: 'connectors', icon: <Plug size={20} />, label: 'Connecteurs' },
  ],
  USER: [
    { page: 'dashboard', icon: <LayoutDashboard size={20} />,  label: 'Accueil' },
    { page: 'chat',      icon: <MessageSquare size={20} />, label: 'Chat' },
  ],
  VIEWER: [
    { page: 'dashboard', icon: <LayoutDashboard size={20} />,  label: 'Accueil' },
    { page: 'chat',      icon: <MessageSquare size={20} />, label: 'Chat' },
  ],
};

function App() {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [role, setRole]   = useState(localStorage.getItem('role') || 'USER');
  const [user, setUser]   = useState(null);
  const [page, setPage]   = useState('dashboard');
  const [theme, setTheme] = useState(localStorage.getItem('theme') || 'dark');

  /* Apply theme to body */
  useEffect(() => {
    if (theme === 'light') {
      document.body.classList.add('light-theme');
    } else {
      document.body.classList.remove('light-theme');
    }
    localStorage.setItem('theme', theme);
  }, [theme]);

  /* Fetch current user info on mount / token change */
  useEffect(() => {
    if (!token) return;
    fetch(`${API_BASE}/auth/me`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => r.ok ? r.json() : Promise.reject())
      .then(data => {
        setUser(data);
        const r = data.role || 'USER';
        setRole(r);
        localStorage.setItem('role', r);
      })
      .catch(() => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        setToken(null);
      });
  }, [token]);

  /* After role is known, reset to first allowed page if current isn't allowed */
  useEffect(() => {
    const allowed = (NAV_BY_ROLE[role] || NAV_BY_ROLE.USER).map(n => n.page);
    if (!allowed.includes(page)) {
      setPage('dashboard');
    }
  }, [role]);

  /* Handle login callback */
  const handleLogin = (newToken, newRole) => {
    setToken(newToken);
    setRole(newRole || 'USER');
    setPage('dashboard');
  };

  /* ---- Not logged in ---- */
  if (!token) {
    return <Login onLogin={handleLogin} />;
  }

  /* ---- Role-based page renderer ---- */
  const isAdmin   = role === 'ADMIN';
  const isCurator = role === 'CURATOR';
  const isUser    = role === 'USER';
  const isViewer  = role === 'VIEWER';

  const renderPage = () => {
    switch (page) {
      case 'dashboard':
        return isAdmin ? <Dashboard /> : <UserDashboard />;

      case 'agents':
        return (isAdmin || isCurator) ? <Agents /> : <NotAllowed />;

      case 'chat':
        return <Chat readOnly={isViewer} />;

      case 'documents':
        return (isAdmin || isCurator) ? <Documents readOnly={isViewer} /> : <NotAllowed />;

      case 'connectors':
        return (isAdmin || isCurator) ? <Connectors /> : <NotAllowed />;

      case 'users':
        return isAdmin ? <Users /> : <NotAllowed />;

      case 'audit':
        return isAdmin ? <Audit /> : <NotAllowed />;

      default:
        return isAdmin ? <Dashboard /> : <UserDashboard />;
    }
  };

  const isSimpleChatBot = isUser || isViewer;

  if (isSimpleChatBot) {
    return (
      <div className="app-container" style={{ display: 'block' }}>
        <main className="page-content" style={{ marginLeft: 0, padding: '1.5rem', height: '100vh', display: 'flex', flexDirection: 'column' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.8rem', marginBottom: '1.5rem', paddingLeft: '0.5rem' }}>
            <span style={{ fontSize: '1.8rem', color: '#fff', background: 'var(--accent-gradient)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', filter: 'drop-shadow(0 0 8px rgba(139, 92, 246, 0.4))' }}>⬡</span>
            <span style={{ fontFamily: 'Outfit', fontSize: '1.4rem', fontWeight: 700, color: 'var(--text-primary)', letterSpacing: '-0.02em' }}>Orbyte Assistant</span>
          </div>
          <Chat readOnly={isViewer} isStandalone={true} user={user} role={role} />
        </main>
      </div>
    );
  }

  const navItems = NAV_BY_ROLE[role] || NAV_BY_ROLE.USER;

  return (
    <div className="app-container">
      <Sidebar
        currentPage={page}
        setPage={setPage}
        user={user}
        role={role}
        navItems={navItems}
        theme={theme}
        setTheme={setTheme}
      />
      <main className="page-content">
        {renderPage()}
      </main>
    </div>
  );
}

/* ---- Fallback for unauthorized access ---- */
function NotAllowed() {
  return (
    <div className="empty-state" style={{ height: '60vh', justifyContent: 'center' }}>
      <div style={{ fontSize: '4rem' }}>🔒</div>
      <h2 style={{ color: 'var(--text-primary)' }}>Accès non autorisé</h2>
      <p style={{ color: 'var(--text-muted)', maxWidth: '360px', textAlign: 'center' }}>
        Vous n'avez pas les permissions nécessaires pour accéder à cette section.
      </p>
    </div>
  );
}

export default App;
