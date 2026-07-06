import { useState, useEffect } from 'react';
import { apiFetch } from '../api';
import { AlertTriangle, Bot } from 'lucide-react';

const emptyForm = { name: '', description: '', category: '', systemPrompt: '', icon: '🤖' };

export default function Agents() {
  const [agents, setAgents]   = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState('');
  const [showForm, setShowForm] = useState(false);
  const [form, setForm]       = useState(emptyForm);
  const [saving, setSaving]   = useState(false);

  const load = () => {
    setLoading(true);
    apiFetch('/agents')
      .then(setAgents)
      .catch(() => setError('Failed to load agents'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await apiFetch('/agents', { method: 'POST', body: JSON.stringify(form) });
      setShowForm(false);
      setForm(emptyForm);
      load();
    } catch {
      setError('Failed to create agent');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this agent?')) return;
    try {
      await apiFetch(`/agents/${id}`, { method: 'DELETE' });
      load();
    } catch {
      setError('Failed to delete agent');
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>AI Agents</h1>
          <p>Configure and manage your intelligent assistants</p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowForm(!showForm)}>
          {showForm ? '✕ Cancel' : '+ New Agent'}
        </button>
      </div>

      {error && <div className="error-msg"><AlertTriangle size={18} /> {error}</div>}

      {showForm && (
        <div className="card glass" style={{ marginBottom: '2rem', animation: 'fadeIn 0.3s' }}>
          <h2 style={{ marginBottom: '1.5rem', fontSize: '1.2rem' }}>Create New Agent</h2>
          <form onSubmit={handleSubmit}>
            <div className="grid-2">
              <div className="form-group">
                <label>Name *</label>
                <input value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} required placeholder="e.g. Sales Copilot" />
              </div>
              <div className="form-group">
                <label>Icon (Emoji)</label>
                <input value={form.icon} onChange={e => setForm(f => ({ ...f, icon: e.target.value }))} />
              </div>
              <div className="form-group">
                <label>Category</label>
                <input value={form.category} onChange={e => setForm(f => ({ ...f, category: e.target.value }))} placeholder="e.g. Sales" />
              </div>
            </div>
            <div className="form-group">
              <label>Description</label>
              <input value={form.description} onChange={e => setForm(f => ({ ...f, description: e.target.value }))} placeholder="Briefly describe the agent's purpose..." />
            </div>
            <div className="form-group">
              <label>System Prompt</label>
              <textarea value={form.systemPrompt} onChange={e => setForm(f => ({ ...f, systemPrompt: e.target.value }))} placeholder="You are a helpful assistant..." />
            </div>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Creating…' : 'Create Agent'}
            </button>
          </form>
        </div>
      )}

      {loading ? (
        <div className="loading">
          <div className="loading-spinner"></div>
          Loading agents…
        </div>
      ) : agents.length === 0 ? (
        <div className="empty-state card glass">
          <div className="empty-state-icon"><Bot size={48} /></div>
          <h3>No agents found</h3>
          <p style={{ color: 'var(--text-muted)' }}>Create your first AI agent to start building conversations.</p>
          <button className="btn btn-primary" style={{ marginTop: '1rem' }} onClick={() => setShowForm(true)}>+ New Agent</button>
        </div>
      ) : (
        <div className="grid-3" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))' }}>
          {agents.map(a => (
            <div className="card glass stat-card" key={a.id} style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
                  <div style={{ fontSize: '2rem', background: 'var(--accent-light)', width: '48px', height: '48px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '12px' }}>
                    {a.icon}
                  </div>
                  <div>
                    <h3 style={{ fontSize: '1.1rem', margin: 0, color: 'var(--text-primary)' }}>{a.name}</h3>
                    <span className="badge badge-muted" style={{ marginTop: '0.3rem' }}>{a.category || 'General'}</span>
                  </div>
                </div>
                <button className="btn btn-ghost btn-sm btn-danger" style={{ padding: '0.3rem', border: 'none' }} onClick={() => handleDelete(a.id)} title="Delete agent">
                  ✕
                </button>
              </div>
              <p style={{ color: 'var(--text-secondary)', fontSize: '13px', flex: 1, margin: 0 }}>
                {a.description || 'No description provided.'}
              </p>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingTop: '1rem', borderTop: '1px solid rgba(255,255,255,0.05)' }}>
                <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                  <span className={`badge ${a.active ? 'badge-success' : 'badge-danger'}`} style={{ padding: '0.2rem 0.5rem' }}>
                    {a.active ? 'Active' : 'Inactive'}
                  </span>
                </div>
                <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>{a.usageCount ?? 0} chats</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
