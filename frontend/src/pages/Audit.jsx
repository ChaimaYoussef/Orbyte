import { useState, useEffect } from 'react';
import { apiFetch } from '../api';
import { AlertTriangle, ClipboardList } from 'lucide-react';

export default function Audit() {
  const [logs, setLogs]       = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState('');

  useEffect(() => {
    apiFetch('/audit')
      .then(setLogs)
      .catch(() => setError('Failed to load audit logs'))
      .finally(() => setLoading(false));
  }, []);

  const levelBadge = (level) => {
    const map = { INFO: 'badge-accent', WARNING: 'badge-warning', ERROR: 'badge-danger', SUCCESS: 'badge-success' };
    return <span className={`badge ${map[level] || 'badge-muted'}`}>{level || 'INFO'}</span>;
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Audit Logs</h1>
          <p>Track all platform activity and security events</p>
        </div>
      </div>

      {error && <div className="error-msg"><AlertTriangle size={18} /> {error}</div>}

      <div className="card glass">
        {loading ? (
          <div className="loading">
            <div className="loading-spinner"></div>
            Loading audit logs…
          </div>
        ) : logs.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state-icon"><ClipboardList size={48} /></div>
            <h3>No audit logs found</h3>
            <p style={{ color: 'var(--text-muted)' }}>Activity will appear here once users interact with the platform.</p>
          </div>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Timestamp</th>
                  <th>User / System</th>
                  <th>Action</th>
                  <th>Resource</th>
                  <th style={{ textAlign: 'right' }}>Level</th>
                </tr>
              </thead>
              <tbody>
                {logs.map(l => (
                  <tr key={l.id}>
                    <td style={{ color: 'var(--text-muted)', fontSize: '13px', whiteSpace: 'nowrap' }}>
                      <span style={{ fontFamily: 'monospace' }}>
                        {l.createdAt ? new Date(l.createdAt).toLocaleString() : '—'}
                      </span>
                    </td>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <span style={{ color: 'var(--text-secondary)' }}>👤</span>
                        {l.userEmail || l.userId || 'System'}
                      </div>
                    </td>
                    <td><strong>{l.action}</strong></td>
                    <td>
                      <code style={{ background: 'rgba(255,255,255,0.05)', padding: '0.2rem 0.5rem', borderRadius: '4px', fontSize: '13px', color: 'var(--text-secondary)' }}>
                        {l.resource || '—'}
                      </code>
                    </td>
                    <td style={{ textAlign: 'right' }}>{levelBadge(l.level)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
