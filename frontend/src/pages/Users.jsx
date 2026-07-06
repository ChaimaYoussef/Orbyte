import { useState, useEffect } from 'react';
import { apiFetch } from '../api';
import { AlertTriangle, Users as UsersIcon } from 'lucide-react';

const emptyCreateForm = { name: '', email: '', password: '', role: 'USER', status: 'ACTIVE', department: '' };
const emptyInviteForm = { email: '', role: 'USER' };

export default function Users() {
  const [users, setUsers]             = useState([]);
  const [loading, setLoading]         = useState(true);
  const [showForm, setShowForm]       = useState(false);
  const [activeTab, setActiveTab]     = useState('create'); // 'create' | 'invite'
  
  const [createForm, setCreateForm]   = useState(emptyCreateForm);
  const [inviteForm, setInviteForm]   = useState(emptyInviteForm);
  const [editId, setEditId]           = useState(null); // when editing, activeTab becomes 'create' and editId is the user ID

  const [saving, setSaving]           = useState(false);
  const [inviteLink, setInviteLink]   = useState('');
  const [error, setError]             = useState('');

  const load = () => {
    setLoading(true);
    apiFetch('/users')
      .then(setUsers)
      .catch(() => setError('Failed to load users'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleCreateOrUpdate = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      if (editId) {
        await apiFetch(`/users/${editId}`, {
          method: 'PUT',
          body: JSON.stringify(createForm)
        });
      } else {
        await apiFetch('/users', {
          method: 'POST',
          body: JSON.stringify(createForm)
        });
      }
      setShowForm(false);
      setCreateForm(emptyCreateForm);
      setEditId(null);
      load();
    } catch (err) {
      setError(err.message || 'Failed to save user');
    } finally {
      setSaving(false);
    }
  };

  const handleInvite = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    setInviteLink('');
    try {
      const res = await apiFetch('/users/invite', {
        method: 'POST',
        body: JSON.stringify(inviteForm),
      });
      setInviteLink(res.token || JSON.stringify(res));
      setInviteForm(emptyInviteForm);
    } catch (err) {
      setError(err.message || 'Failed to send invitation');
    } finally {
      setSaving(false);
    }
  };

  const handleEditClick = (u) => {
    setCreateForm({
      name: u.name || u.fullName || '',
      email: u.email || '',
      password: '', // blank password means no change during update
      role: u.role || 'USER',
      status: u.status || 'ACTIVE',
      department: u.department || ''
    });
    setEditId(u.id);
    setActiveTab('create');
    setShowForm(true);
    setInviteLink('');
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this user?')) return;
    try {
      await apiFetch(`/users/${id}`, { method: 'DELETE' });
      load();
    } catch {
      setError('Failed to delete user');
    }
  };

  const roleBadge = (role) => {
    const map = { ADMIN: 'badge-accent', USER: 'badge-muted', CURATOR: 'badge-success', VIEWER: 'badge-info' };
    return <span className={`badge ${map[role] || 'badge-muted'}`}>{role}</span>;
  };

  const tabStyle = (isActive) => ({
    background: 'none',
    border: 'none',
    color: isActive ? 'var(--accent)' : 'var(--text-muted)',
    borderBottom: isActive ? '2px solid var(--accent)' : '2px solid transparent',
    padding: '0.6rem 1.2rem',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '600',
    transition: 'all 0.2s',
    outline: 'none',
    marginBottom: '-1px'
  });

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Workspace Members</h1>
          <p>Manage users, roles, and platform invitations</p>
        </div>
        <button className="btn btn-primary" onClick={() => { setShowForm(!showForm); setEditId(null); setCreateForm(emptyCreateForm); setInviteLink(''); }}>
          {showForm ? '✕ Cancel' : '+ Add / Invite Member'}
        </button>
      </div>

      {error && <div className="error-msg"><AlertTriangle size={18} /> {error}</div>}

      {showForm && (
        <div className="card glass" style={{ marginBottom: '2rem', animation: 'fadeIn 0.3s' }}>
          {/* Tabs */}
          {!editId && (
            <div style={{ display: 'flex', borderBottom: '1px solid rgba(255,255,255,0.05)', marginBottom: '1.5rem' }}>
              <button style={tabStyle(activeTab === 'create')} onClick={() => setActiveTab('create')}>
                Direct Create
              </button>
              <button style={tabStyle(activeTab === 'invite')} onClick={() => setActiveTab('invite')}>
                Send Invitation
              </button>
            </div>
          )}

          {activeTab === 'create' ? (
            <div>
              <h2 style={{ marginBottom: '1.5rem', fontSize: '1.2rem' }}>
                {editId ? 'Edit Member details' : 'Create New Member Directly'}
              </h2>
              <form onSubmit={handleCreateOrUpdate}>
                <div className="grid-2">
                  <div className="form-group">
                    <label>Full Name *</label>
                    <input value={createForm.name} onChange={e => setCreateForm(f => ({ ...f, name: e.target.value }))} required placeholder="e.g. John Doe" />
                  </div>
                  <div className="form-group">
                    <label>Email Address *</label>
                    <input type="email" value={createForm.email} onChange={e => setCreateForm(f => ({ ...f, email: e.target.value }))} required placeholder="john@company.com" />
                  </div>
                  <div className="form-group">
                    <label>{editId ? 'Password (Leave blank to keep current)' : 'Password'}</label>
                    <input type="password" value={createForm.password} onChange={e => setCreateForm(f => ({ ...f, password: e.target.value }))} placeholder={editId ? '••••••••' : 'Password or default'} />
                  </div>
                  <div className="form-group">
                    <label>Department</label>
                    <input value={createForm.department} onChange={e => setCreateForm(f => ({ ...f, department: e.target.value }))} placeholder="e.g. Engineering, Sales" />
                  </div>
                  <div className="form-group">
                    <label>Workspace Role</label>
                    <select value={createForm.role} onChange={e => setCreateForm(f => ({ ...f, role: e.target.value }))}>
                      <option value="USER">User (Standard Access)</option>
                      <option value="VIEWER">Viewer (Read-only)</option>
                      <option value="CURATOR">Curator (Manage documents/agents)</option>
                      <option value="ADMIN">Admin (Full Control)</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label>Account Status</label>
                    <select value={createForm.status} onChange={e => setCreateForm(f => ({ ...f, status: e.target.value }))}>
                      <option value="ACTIVE">Active</option>
                      <option value="PENDING">Pending</option>
                      <option value="INACTIVE">Inactive</option>
                    </select>
                  </div>
                </div>
                <button type="submit" className="btn btn-primary" disabled={saving} style={{ marginTop: '1rem' }}>
                  {saving ? 'Saving…' : editId ? 'Save Changes' : 'Create Member'}
                </button>
              </form>
            </div>
          ) : (
            <div>
              <h2 style={{ marginBottom: '1.5rem', fontSize: '1.2rem' }}>Invite a New Member</h2>
              <form onSubmit={handleInvite}>
                <div className="grid-2">
                  <div className="form-group">
                    <label>Email Address *</label>
                    <input type="email" value={inviteForm.email} onChange={e => setInviteForm(f => ({ ...f, email: e.target.value }))} required placeholder="colleague@company.com" />
                  </div>
                  <div className="form-group">
                    <label>Assign Role</label>
                    <select value={inviteForm.role} onChange={e => setInviteForm(f => ({ ...f, role: e.target.value }))}>
                      <option value="USER">User (Standard Access)</option>
                      <option value="VIEWER">Viewer (Read-only)</option>
                      <option value="CURATOR">Curator (Manage documents/agents)</option>
                      <option value="ADMIN">Admin (Full Control)</option>
                    </select>
                  </div>
                </div>
                <button type="submit" className="btn btn-primary" disabled={saving} style={{ marginTop: '1rem' }}>
                  {saving ? 'Sending…' : 'Generate Invitation Token'}
                </button>
              </form>
              {inviteLink && (
                <div style={{ marginTop: '1.5rem', padding: '1rem', background: 'rgba(16, 185, 129, 0.1)', border: '1px solid rgba(16, 185, 129, 0.2)', borderRadius: 'var(--radius-sm)', color: 'var(--success)', fontSize: '14px', wordBreak: 'break-all', display: 'flex', gap: '0.8rem', alignItems: 'center' }}>
                  <span style={{ fontSize: '1.2rem' }}>✓</span>
                  <div>
                    <strong>Invitation token created:</strong><br/>
                    <code style={{ background: 'rgba(0,0,0,0.2)', padding: '0.2rem 0.4rem', borderRadius: '4px', display: 'inline-block', marginTop: '0.4rem' }}>{inviteLink}</code>
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      )}

      <div className="card glass">
        {loading ? (
          <div className="loading">
            <div className="loading-spinner"></div>
            Loading members…
          </div>
        ) : users.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state-icon"><UsersIcon size={48} /></div>
            <h3>No members found</h3>
            <p style={{ color: 'var(--text-muted)' }}>Directly create or invite team members to join the workspace.</p>
          </div>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Member</th>
                  <th>Department</th>
                  <th>Role</th>
                  <th>Status</th>
                  <th>Last Active</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.map(u => (
                  <tr key={u.id}>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                        <div style={{ width: '36px', height: '36px', borderRadius: '50%', background: 'var(--accent-light)', color: 'var(--accent)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold' }}>
                          {u.name ? u.name.charAt(0).toUpperCase() : u.fullName ? u.fullName.charAt(0).toUpperCase() : 'U'}
                        </div>
                        <div>
                          <strong>{u.name || u.fullName || 'Unknown'}</strong><br />
                          <span style={{ color: 'var(--text-muted)', fontSize: '12px' }}>{u.email}</span>
                        </div>
                      </div>
                    </td>
                    <td>{u.department || '—'}</td>
                    <td>{roleBadge(u.role)}</td>
                    <td>
                      <span className={`badge ${u.status === 'ACTIVE' ? 'badge-success' : u.status === 'PENDING' ? 'badge-warning' : 'badge-muted'}`}>{u.status}</span>
                    </td>
                    <td style={{ color: 'var(--text-muted)', fontSize: '13px' }}>
                      {u.lastActive ? new Date(u.lastActive).toLocaleDateString() : '—'}
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <div style={{ display: 'inline-flex', gap: '0.4rem' }}>
                        <button className="btn btn-ghost btn-sm" onClick={() => handleEditClick(u)}>Edit</button>
                        <button className="btn btn-ghost btn-sm btn-danger" onClick={() => handleDelete(u.id)}>Remove</button>
                      </div>
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
