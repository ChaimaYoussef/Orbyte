import { useState, useEffect, useRef } from 'react';
import { apiFetch } from '../api';
import { Search, Plus, X, FileText, Hexagon, Send, Bot } from 'lucide-react';
import './Chat.css';

export default function Chat({ readOnly, isStandalone, user, role }) {
  const [sessions, setSessions]   = useState([]);
  const [activeSession, setActiveSession] = useState(null);
  const [messages, setMessages]   = useState([]);
  const [input, setInput]         = useState('');
  const [sending, setSending]     = useState(false);
  const [agents, setAgents]       = useState([]);
  const [selectedAgent, setSelectedAgent] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const messagesEndRef = useRef(null);

  useEffect(() => {
    apiFetch('/chats').then(setSessions).catch(() => {});
    apiFetch('/agents').then(data => {
      setAgents(data || []);
      if (data && data.length > 0) {
        setSelectedAgent(data[0].id);
      }
    }).catch(() => {});
  }, []);

  useEffect(() => {
    if (activeSession) {
      apiFetch(`/chats/${activeSession.id}/messages`).then(setMessages).catch(() => {});
    }
  }, [activeSession]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const startSession = async () => {
    const session = await apiFetch('/chats', {
      method: 'POST',
      body: JSON.stringify({ agentId: selectedAgent || null, title: 'New Conversation' }),
    });
    setSessions(prev => [session, ...prev]);
    setActiveSession(session);
    setMessages([]);
  };

  const sendMessage = async (e) => {
    e.preventDefault();
    if (!input.trim() || !activeSession) return;
    const userMsg = input.trim();
    setInput('');
    setMessages(prev => [...prev, { role: 'USER', content: userMsg }]);
    setSending(true);
    try {
      const res = await apiFetch('/chats/query', {
        method: 'POST',
        body: JSON.stringify({ sessionId: activeSession.id, message: userMsg }),
      });
      setMessages(prev => [...prev, { 
        role: 'ASSISTANT', 
        content: res.content,
        sources: res.sources || []
      }]);
    } catch {
      setMessages(prev => [...prev, { role: 'ASSISTANT', content: 'Error getting response from the agent.' }]);
    } finally {
      setSending(false);
    }
  };

  const deleteSession = async (id) => {
    await apiFetch(`/chats/${id}`, { method: 'DELETE' });
    setSessions(prev => prev.filter(s => s.id !== id));
    if (activeSession?.id === id) { setActiveSession(null); setMessages([]); }
  };

  return (
    <div className="chat-layout">
      {/* Sessions sidebar */}
      <div className="chat-sidebar">
        <div className="chat-sidebar-header">
          Conversations
        </div>

        <div className="chat-new-section">
          {agents.length > 0 && (
            <select
              value={selectedAgent}
              onChange={e => setSelectedAgent(e.target.value)}
              className="chat-agent-select"
            >
              {agents.map(a => (
                <option key={a.id} value={a.id}>{a.icon} {a.name}</option>
              ))}
            </select>
          )}
          <button className="btn btn-primary" style={{ width: '100%', justifyContent: 'center' }} onClick={startSession}>
            <Plus size={16} /> New Chat
          </button>
        </div>

        <div className="chat-search-wrap" style={{ padding: '0 1rem', marginBottom: '1rem', position: 'relative' }}>
          <Search size={14} style={{ position: 'absolute', left: '1.8rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
          <input 
            type="text" 
            placeholder="Rechercher une conversation..." 
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
            style={{ width: '100%', padding: '0.6rem 1rem 0.6rem 2.2rem', background: 'var(--bg-hover)', border: '1px solid var(--border)', borderRadius: '8px', color: 'var(--text-primary)', fontSize: '13px', outline: 'none' }}
          />
        </div>

        <div className="chat-sessions-list">
          {sessions.filter(s => (s.title || '').toLowerCase().includes(searchQuery.toLowerCase())).map(s => (
            <div
              key={s.id}
              className={`chat-session-item ${activeSession?.id === s.id ? 'active' : ''}`}
              onClick={() => setActiveSession(s)}
            >
              <span className="chat-session-title">{s.title || 'Conversation'}</span>
              <button
                className="chat-session-delete"
                onClick={ev => { ev.stopPropagation(); deleteSession(s.id); }}
                title="Delete chat"
              ><X size={14} /></button>
            </div>
          ))}
          {sessions.length === 0 && <div className="empty-state" style={{ padding: '2rem 1rem', fontSize: '13px' }}>No conversations yet.<br/>Start a new chat above.</div>}
        </div>

        {isStandalone && (
          <div className="chat-sidebar-footer" style={{ marginTop: 'auto', padding: '1.2rem', borderTop: '1px solid rgba(255,255,255,0.05)', display: 'flex', alignItems: 'center', gap: '0.8rem', background: 'rgba(0,0,0,0.1)' }}>
            <div style={{ width: '38px', height: '38px', borderRadius: '8px', background: 'var(--accent-gradient)', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold', fontSize: '1.1rem', flexShrink: 0 }}>
              {user?.fullName ? user.fullName.charAt(0).toUpperCase() : 'U'}
            </div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-primary)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{user?.fullName || 'Utilisateur'}</div>
              <div style={{ fontSize: '11px', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.04em' }}>{role === 'VIEWER' ? 'Observateur' : 'Utilisateur'}</div>
            </div>
            <button
              onClick={() => {
                localStorage.removeItem('token');
                localStorage.removeItem('role');
                window.location.reload();
              }}
              style={{ background: 'transparent', border: '1px solid rgba(255,255,255,0.1)', borderRadius: '6px', color: 'var(--text-secondary)', cursor: 'pointer', fontSize: '1.2rem', width: '32px', height: '32px', display: 'flex', alignItems: 'center', justifyContent: 'center', transition: 'all 0.2s', padding: 0 }}
              title="Se déconnecter"
              onMouseEnter={e => { e.currentTarget.style.color = '#fff'; e.currentTarget.style.background = 'var(--danger)'; e.currentTarget.style.borderColor = 'var(--danger)'; }}
              onMouseLeave={e => { e.currentTarget.style.color = 'var(--text-secondary)'; e.currentTarget.style.background = 'transparent'; e.currentTarget.style.borderColor = 'rgba(255,255,255,0.1)'; }}
            >
              ⏻
            </button>
          </div>
        )}
      </div>

      {/* Messages area */}
      <div className="chat-main">
        {!activeSession ? (
          <div className="chat-empty">
            <div className="chat-empty-icon"><Hexagon size={48} strokeWidth={1.5} /></div>
            <p>Select a conversation or start a new one to begin</p>
          </div>
        ) : (
          <>
            <div className="chat-messages">
              {messages.map((m, i) => (
                <div key={i} className={`chat-msg ${m.role === 'USER' ? 'chat-msg-user' : 'chat-msg-ai'}`}>
                  <div className="chat-avatar">{m.role === 'USER' ? 'U' : <Bot size={18} />}</div>
                  <div className="chat-msg-content-wrapper" style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', flex: 1, minWidth: 0 }}>
                    <div className="chat-msg-bubble">{m.content}</div>
                    {m.sources && m.sources.length > 0 && (
                      <div className="chat-sources" style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem', marginTop: '0.2rem' }}>
                        {m.sources.map((s, idx) => (
                          <span 
                            key={idx} 
                            title={s.snippet || 'Source context'}
                            className="chat-source-badge"
                            style={{ 
                              fontSize: '11px', 
                              background: 'rgba(139, 92, 246, 0.1)', 
                              color: 'var(--accent)', 
                              padding: '0.2rem 0.5rem', 
                              borderRadius: '4px',
                              border: '1px solid rgba(139, 92, 246, 0.2)',
                              display: 'flex',
                              alignItems: 'center',
                              gap: '0.3rem',
                              cursor: 'help'
                            }}
                          >
                            <FileText size={12} /> {s.label || 'Document source'}
                          </span>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              ))}
              {sending && (
                <div className="chat-msg chat-msg-ai">
                  <div className="chat-avatar"><Bot size={18} /></div>
                  <div className="chat-msg-bubble chat-typing">
                    <span></span><span></span><span></span>
                  </div>
                </div>
              )}
              <div ref={messagesEndRef} />
            </div>
            <form className="chat-input-form" onSubmit={sendMessage}>
              <input
                type="text"
                className="chat-input"
                placeholder="Message Orbyte Assistant..."
                value={input}
                onChange={e => setInput(e.target.value)}
                disabled={sending}
                autoFocus
              />
              <button type="submit" className="btn btn-primary" disabled={sending || !input.trim()}>
                <Send size={16} /> Send
              </button>
            </form>
          </>
        )}
      </div>
    </div>
  );
}
