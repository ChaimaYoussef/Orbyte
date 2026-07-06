import { useState, useEffect } from 'react';
import { apiFetch, API_BASE } from '../api';
import { AlertTriangle, FileText } from 'lucide-react';

export default function Documents() {
  const [docs, setDocs]       = useState([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [error, setError]     = useState('');

  const load = () => {
    setLoading(true);
    apiFetch('/documents')
      .then(setDocs)
      .catch(() => setError('Failed to load documents'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setUploading(true);
    setError('');
    const form = new FormData();
    form.append('file', file);
    try {
      const token = localStorage.getItem('token');
      const res = await fetch(`${API_BASE}/documents/upload`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` },
        body: form,
      });
      if (!res.ok) throw new Error('Upload failed');
      load();
    } catch (err) {
      setError(err.message);
    } finally {
      setUploading(false);
      e.target.value = '';
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this document?')) return;
    await apiFetch(`/documents/${id}`, { method: 'DELETE' });
    load();
  };

  const statusBadge = (status) => {
    const map = {
      PROCESSED: 'badge-success',
      PROCESSING: 'badge-warning',
      FAILED: 'badge-danger',
    };
    return <span className={`badge ${map[status] || 'badge-muted'}`}>{status}</span>;
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Knowledge Base</h1>
          <p>Upload and manage documents for agent retrieval</p>
        </div>
        <label className="btn btn-primary" style={{ cursor: 'pointer' }}>
          {uploading ? 'Uploading…' : '↑ Upload Document'}
          <input type="file" style={{ display: 'none' }} onChange={handleUpload} disabled={uploading} />
        </label>
      </div>

      {error && <div className="error-msg"><AlertTriangle size={18} /> {error}</div>}

      <div className="card glass">
        {loading ? (
          <div className="loading">
            <div className="loading-spinner"></div>
            Loading documents…
          </div>
        ) : docs.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state-icon"><FileText size={48} /></div>
            <h3>No documents found</h3>
            <p style={{ color: 'var(--text-muted)' }}>Upload PDFs, TXTs, or other documents to train your agents.</p>
          </div>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Type</th>
                  <th>Status</th>
                  <th>Created</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {docs.map(d => (
                  <tr key={d.id}>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.8rem' }}>
                        <span style={{ color: 'var(--accent)' }}><FileText size={24} /></span>
                        <strong>{d.title || d.source || '—'}</strong>
                      </div>
                    </td>
                    <td><span className="badge badge-muted">{d.fileType || '—'}</span></td>
                    <td>{statusBadge(d.status)}</td>
                    <td style={{ color: 'var(--text-muted)', fontSize: '13px' }}>
                      {d.createdAt ? new Date(d.createdAt).toLocaleDateString() : '—'}
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <button className="btn btn-ghost btn-sm btn-danger" onClick={() => handleDelete(d.id)}>Delete</button>
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
