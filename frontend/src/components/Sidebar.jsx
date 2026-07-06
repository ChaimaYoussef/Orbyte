import './Sidebar.css';
import { Sun, Moon } from 'lucide-react';
import logo from '../assets/orbyte.svg';

const ROLE_LABEL = {
  ADMIN:   { label: 'Administrateur', color: 'var(--accent)',  bg: 'var(--accent-light)' },
  CURATOR: { label: 'Curateur',       color: '#34d399',       bg: 'rgba(52,211,153,0.12)' },
  USER:    { label: 'Utilisateur',    color: '#22d3ee',       bg: 'rgba(34,211,238,0.12)' },
  VIEWER:  { label: 'Observateur',    color: '#94a3b8',       bg: 'rgba(148,163,184,0.1)' },
};

export default function Sidebar({ currentPage, setPage, user, role, navItems, theme, setTheme }) {
  const rl = ROLE_LABEL[role] || ROLE_LABEL.USER;

  return (
    <aside className="sidebar">
      {/* Logo */}
      <div className="sidebar-logo">
        <img src={logo} alt="Orbyte" className="sidebar-logo-icon" style={{ height: '32px', width: 'auto' }} />
      </div>

      {/* Role badge */}
      <div className="sidebar-role-badge" style={{ background: rl.bg, borderColor: `color-mix(in srgb, ${rl.color} 30%, transparent)` }}>
        <span style={{ color: rl.color, fontSize: '11px', fontWeight: 600, letterSpacing: '0.04em', textTransform: 'uppercase' }}>
          ● {rl.label}
        </span>
      </div>

      {/* Navigation — dynamic per role */}
      <nav className="sidebar-nav">
        {(navItems || []).map((item) => (
          <button
            key={item.page}
            id={`nav-${item.page}`}
            className={`sidebar-item ${currentPage === item.page ? 'active' : ''}`}
            onClick={() => setPage(item.page)}
          >
            <span className="sidebar-icon">{item.icon}</span>
            <span className="sidebar-label">{item.label}</span>
          </button>
        ))}
      </nav>

      {/* User info + logout */}
      <div className="sidebar-footer">
        <div className="sidebar-user">
          <div className="sidebar-avatar">
            {user?.fullName ? user.fullName.charAt(0).toUpperCase() : 'U'}
          </div>
          <div>
            <div className="sidebar-user-name">{user?.fullName || 'Utilisateur'}</div>
            <div className="sidebar-user-role" style={{ color: rl.color }}>{rl.label}</div>
          </div>
        </div>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button
            className="sidebar-logout"
            onClick={() => setTheme(theme === 'dark' ? 'light' : 'dark')}
            title={theme === 'dark' ? 'Passer en mode clair' : 'Passer en mode sombre'}
          >
            {theme === 'dark' ? <Sun size={18} /> : <Moon size={18} />}
          </button>
          <button
            className="sidebar-logout"
            onClick={() => {
              localStorage.removeItem('token');
              localStorage.removeItem('role');
              window.location.reload();
            }}
            title="Se déconnecter"
          >
            ⏻
          </button>
        </div>
      </div>
    </aside>
  );
}
