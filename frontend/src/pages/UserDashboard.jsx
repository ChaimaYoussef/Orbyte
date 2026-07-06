import { useState, useEffect } from 'react';
import { apiFetch } from '../api';
import { User, Book, Eye, MessageSquare, BrainCircuit, Lightbulb, Tent, ClipboardList, Wifi, BarChart2, Palette } from 'lucide-react';

/* --------------------------------------------------------
   USER DASHBOARD — Standard user (Chat + Documents access)
   -------------------------------------------------------- */
export default function UserDashboard() {
  const [chats, setChats] = useState([]);
  const [docs, setDocs] = useState([]);
  const [loading, setLoading] = useState(true);
  const role = localStorage.getItem('role') || 'USER';

  useEffect(() => {
    Promise.all([
      apiFetch('/chats').catch(() => []),
      apiFetch('/documents').catch(() => []),
    ]).then(([c, d]) => {
      setChats(c || []);
      setDocs(d || []);
    }).finally(() => setLoading(false));
  }, []);

  const roleConfig = {
    USER: { label: 'Utilisateur', color: 'var(--accent)', icon: <User size={32} />, accent: 'rgba(139,92,246,0.12)' },
    CURATOR: { label: 'Curateur', color: '#34d399', icon: <Book size={32} />, accent: 'rgba(52,211,153,0.12)' },
    VIEWER: { label: 'Observateur', color: '#22d3ee', icon: <Eye size={32} />, accent: 'rgba(34,211,238,0.12)' },
  };
  const rc = roleConfig[role] || roleConfig.USER;

  return (
    <div>
      {/* Welcome Banner */}
      <div className="card" style={{ marginBottom: '2rem', background: rc.accent, borderColor: `color-mix(in srgb, ${rc.color} 20%, transparent)`, padding: '2rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
          <div style={{ fontSize: '3rem', background: `color-mix(in srgb, ${rc.color} 20%, transparent)`, width: '72px', height: '72px', borderRadius: '20px', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
            {rc.icon}
          </div>
          <div>
            <h1 style={{ fontSize: '1.6rem', marginBottom: '0.4rem', background: `linear-gradient(135deg, #fff, ${rc.color})`, WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
              Bienvenue sur Orbyte AI
            </h1>
            <p style={{ color: 'var(--text-secondary)', fontSize: '14px' }}>
              Vous êtes connecté en tant que <strong style={{ color: rc.color }}>{rc.label}</strong>. Posez vos questions à l'assistant IA.
            </p>
          </div>
        </div>
      </div>

      {loading ? (
        <div className="loading"><div className="loading-spinner" />Chargement…</div>
      ) : (
        <div className="grid-2">
          {/* Recent chats */}
          <div className="card">
            <h3 style={{ fontSize: '1rem', color: 'var(--text-secondary)', marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <MessageSquare size={18} /> Mes conversations récentes
            </h3>
            {chats.length === 0 ? (
              <div className="empty-state" style={{ padding: '2rem 1rem' }}>
                <div className="empty-state-icon"><MessageSquare size={32} /></div>
                <p style={{ fontSize: '13px' }}>Aucune conversation. Allez dans Chat pour commencer.</p>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                {chats.slice(0, 5).map(c => (
                  <div key={c.id} style={{ padding: '0.75rem 1rem', borderRadius: '10px', background: 'rgba(255,255,255,0.03)', border: '1px solid rgba(255,255,255,0.05)', fontSize: '13.5px' }}>
                    <div style={{ color: 'var(--text-primary)', fontWeight: 500 }}>{c.title || 'Conversation'}</div>
                    {c.lastMessage && <div style={{ color: 'var(--text-muted)', marginTop: '0.2rem', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{c.lastMessage}</div>}
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Knowledge Base Info */}
          <div className="card">
            <h3 style={{ fontSize: '1rem', color: 'var(--text-secondary)', marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <BrainCircuit size={18} /> Base de connaissances IA
            </h3>
            <div style={{ padding: '1rem', borderRadius: '10px', background: 'rgba(255,255,255,0.03)', border: '1px solid rgba(255,255,255,0.05)' }}>
              <p style={{ fontSize: '13.5px', color: 'var(--text-primary)', lineHeight: '1.6' }}>
                L'assistant IA est connecté à la <strong>Base de Connaissances</strong> de l'entreprise.
                Posez vos questions dans le Chat, et l'IA analysera automatiquement les documents internes pour vous fournir une réponse précise, avec les sources associées.
              </p>
            </div>
          </div>

          {/* Tips */}
          <div className="card" style={{ gridColumn: '1 / -1' }}>
            <h3 style={{ fontSize: '1rem', color: 'var(--text-secondary)', marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Lightbulb size={18} /> Suggestions de requêtes
            </h3>
            <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
              {[
                { icon: <Tent size={14} />, text: 'Comment demander des congés ?' },
                { icon: <ClipboardList size={14} />, text: 'Obtenir une attestation de travail' },
                { icon: <Wifi size={14} />, text: 'Accès WiFi et onboarding' },
                { icon: <BarChart2 size={14} />, text: 'Politique de télétravail' },
                { icon: <Palette size={14} />, text: 'Logo et charte graphique' },
              ].map(q => (
                <div key={q.text} style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', padding: '0.5rem 1rem', borderRadius: '999px', background: 'var(--accent-light)', border: '1px solid rgba(59,130,246,0.2)', color: 'var(--accent)', fontSize: '13px', cursor: 'pointer', transition: 'all 0.15s' }}
                  onMouseEnter={e => e.currentTarget.style.background = 'rgba(59,130,246,0.2)'}
                  onMouseLeave={e => e.currentTarget.style.background = 'var(--accent-light)'}>
                  {q.icon} {q.text}
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
