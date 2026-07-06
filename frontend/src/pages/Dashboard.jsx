import { useState, useEffect } from 'react';
import { apiFetch } from '../api';
import { Bot, MessageSquare, FileText, Plug, Users, Zap, Activity } from 'lucide-react';

export default function Dashboard() {
  const [stats, setStats]     = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      apiFetch('/agents'),
      apiFetch('/chats'),
      apiFetch('/documents'),
      apiFetch('/connectors'),
      apiFetch('/users'),
    ]).then(([agents, chats, docs, connectors, users]) => {
      setStats({
        agents:     agents.length,
        chats:      chats.length,
        documents:  docs.length,
        connectors: connectors.length,
        users:      users.length,
      });
    }).catch(() => setStats(null))
      .finally(() => setLoading(false));
  }, []);

  const cards = [
    { label: 'Total Agents',      value: stats?.agents,     icon: <Bot size={24} />, color: 'var(--accent)' },
    { label: 'Sessions Chat',     value: stats?.chats,      icon: <MessageSquare size={24} />, color: '#22d3ee' },
    { label: 'Documents indexés', value: stats?.documents,  icon: <FileText size={24} />, color: '#60A5FA' },
    { label: 'Connecteurs',       value: stats?.connectors, icon: <Plug size={24} />, color: '#34d399' },
    { label: 'Utilisateurs',      value: stats?.users,      icon: <Users size={24} />, color: '#f87171' },
  ];

  return (
    <div className="dashboard-page">
      <div className="page-header">
        <div>
          <h1>Vue d'ensemble</h1>
          <p>Métriques en temps réel de votre plateforme Orbyte AI</p>
        </div>
        <span className="badge badge-success">● Système opérationnel</span>
      </div>

      {loading ? (
        <div className="loading">
          <div className="loading-spinner" />
          Synchronisation des données…
        </div>
      ) : (
        <>
          <div className="grid-4" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(210px, 1fr))', marginBottom: '2rem' }}>
            {cards.map((c) => (
              <div className="stat-card card" key={c.label}>
                <div className="stat-header">
                  <div className="stat-label">{c.label}</div>
                  <div className="stat-icon" style={{ color: c.color, background: `color-mix(in srgb, ${c.color} 15%, transparent)` }}>{c.icon}</div>
                </div>
                <div className="stat-value">{c.value ?? '—'}</div>
              </div>
            ))}
          </div>

          <div className="grid-2">
            {/* Quick Actions */}
            <div className="card">
              <h3 style={{ marginBottom: '1.25rem', fontSize: '1rem', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '0.5rem' }}><Zap size={18} /> Actions rapides</h3>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {[
                  { icon: <Bot size={20} />, text: 'Créer un nouvel agent IA', color: 'var(--accent)' },
                  { icon: <FileText size={20} />, text: 'Importer des documents', color: '#60A5FA' },
                  { icon: <Plug size={20} />, text: 'Connecter une source de données', color: '#34d399' },
                  { icon: <Users size={20} />, text: 'Inviter un membre', color: '#f87171' },
                ].map(a => (
                  <div key={a.text} style={{ display: 'flex', alignItems: 'center', gap: '0.9rem', padding: '0.75rem', borderRadius: '10px', background: 'rgba(255,255,255,0.03)', border: '1px solid rgba(255,255,255,0.05)', cursor: 'pointer', transition: 'all 0.2s' }}
                    onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.06)'}
                    onMouseLeave={e => e.currentTarget.style.background = 'rgba(255,255,255,0.03)'}>
                    <span style={{ fontSize: '1.3rem' }}>{a.icon}</span>
                    <span style={{ color: 'var(--text-secondary)', fontSize: '13.5px' }}>{a.text}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Platform health */}
            <div className="card">
              <h3 style={{ marginBottom: '1.25rem', fontSize: '1rem', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '0.5rem' }}><Activity size={18} /> État de la plateforme</h3>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {[
                  { label: 'API Spring Boot', status: 'En ligne', ok: true },
                  { label: 'Base de données H2', status: 'En ligne', ok: true },
                  { label: 'Service IA (FastAPI)', status: 'Mode fallback', ok: false },
                  { label: 'Qdrant Vector DB', status: 'Non connecté', ok: false },
                ].map(s => (
                  <div key={s.label} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.65rem 0.9rem', borderRadius: '10px', background: 'rgba(255,255,255,0.03)', border: '1px solid rgba(255,255,255,0.05)' }}>
                    <span style={{ fontSize: '13.5px', color: 'var(--text-secondary)' }}>{s.label}</span>
                    <span className={`badge ${s.ok ? 'badge-success' : 'badge-warning'}`} style={{ fontSize: '11px' }}>
                      {s.ok ? '●' : '◌'} {s.status}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
