import { useState, useEffect } from 'react';
import { apiFetch } from '../api';
import { AlertTriangle, Plug, FileText, Folder, Building2, Clipboard, MessageSquare, GitBranch, Cloud, Database } from 'lucide-react';

const TYPES = ['NOTION', 'GOOGLE_DRIVE', 'CONFLUENCE', 'JIRA', 'SLACK', 'GITHUB', 'S3', 'DATABASE'];
const emptyForm = { name: '', type: 'NOTION', config: '' };

export default function Connectors() {
  const [connectors, setConnectors] = useState([]);
  const [loading, setLoading]       = useState(true);
  const [showForm, setShowForm]     = useState(false);
  const [form, setForm]             = useState(emptyForm);
  const [saving, setSaving]         = useState(false);
  const [error, setError]           = useState('');

  const load = () => {
    setLoading(true);
    apiFetch('/connectors')
      .then(setConnectors)
      .catch(() => setError('Failed to load connectors'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await apiFetch('/connectors', { method: 'POST', body: JSON.stringify(form) });
      setShowForm(false);
      setForm(emptyForm);
      load();
    } catch {
      setError('Failed to create connector');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this connector?')) return;
    await apiFetch(`/connectors/${id}`, { method: 'DELETE' });
    load();
  };

  const statusBadge = (status) => {
    const map = { CONNECTED: 'badge-success', DISCONNECTED: 'badge-muted', ERROR: 'badge-danger' };
    return <span className={`badge ${map[status] || 'badge-muted'}`}>{status}</span>;
  };

  const typeIcon = (type) => {
    const map = {
      NOTION: <FileText size={16} />, GOOGLE_DRIVE: <Folder size={16} />, CONFLUENCE: <Building2 size={16} />, JIRA: <Clipboard size={16} />,
      SLACK: <MessageSquare size={16} />, GITHUB: <GitBranch size={16} />, S3: <Cloud size={16} />, DATABASE: <Database size={16} />,
    };
    return map[type] || <Plug size={16} />;
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Data Connectors</h1>
          <p>Sync external data sources with your workspace</p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowForm(!showForm)}>
          {showForm ? '✕ Cancel' : '+ New Connector'}
        </button>
      </div>

      {error && <div className="error-msg"><AlertTriangle size={18} /> {error}</div>}

      {showForm && (
        <div className="card glass" style={{ marginBottom: '2rem', animation: 'fadeIn 0.3s' }}>
          <h2 style={{ marginBottom: '1.5rem', fontSize: '1.2rem' }}>Configure New Connector</h2>
          <form onSubmit={handleSubmit}>
            <div className="grid-2">
              <div className="form-group">
                <label>Name *</label>
                <input value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} required placeholder="e.g. Engineering Wiki" />
              </div>
              <div className="form-group">
                <label>Connector Type</label>
                <select value={form.type} onChange={e => setForm(f => ({ ...f, type: e.target.value }))}>
                  {TYPES.map(t => <option key={t} value={t}>{t}</option>)}
                </select>
              </div>
            </div>
            <div className="form-group">
              <label>Configuration (JSON format)</label>
              <textarea placeholder='{"apiKey": "...", "workspace": "..."}' value={form.config} onChange={e => setForm(f => ({ ...f, config: e.target.value }))} style={{ fontFamily: 'monospace' }} />
            </div>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Connecting…' : 'Create Connector'}
            </button>
          </form>
        </div>
      )}

      <div className="card glass">
        {loading ? (
          <div className="loading">
            <div className="loading-spinner"></div>
            Loading connectors…
          </div>
        ) : connectors.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state-icon"><Plug size={48} /></div>
            <h3>No connectors configured</h3>
            <p style={{ color: 'var(--text-muted)' }}>Link your external tools to allow agents to search them.</p>
          </div>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Connector</th>
                  <th>Type</th>
                  <th>Status</th>
                  <th>Indexed</th>
                  <th>Last Sync</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {connectors.map(c => (
                  <tr key={c.id}>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.8rem' }}>
                        <span style={{ fontSize: '1.2rem' }}>{typeIcon(c.type)}</span>
                        <strong>{c.name}</strong>
                      </div>
                    </td>
                    <td><span className="badge badge-accent">{c.type}</span></td>
                    <td>{statusBadge(c.status)}</td>
                    <td>{c.docsIndexed ?? 0} docs</td>
                    <td style={{ color: 'var(--text-muted)', fontSize: '13px' }}>
                      {c.lastSync ? new Date(c.lastSync).toLocaleDateString() : 'Never'}
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <button className="btn btn-ghost btn-sm btn-danger" onClick={() => handleDelete(c.id)}>Remove</button>
                    </td>
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
