import { useState } from 'react';
import { Eye, EyeOff } from 'lucide-react';
import './Login.css';
import logo from '../assets/orbyte.svg';

const API = 'http://localhost:8081/api/v1';

export default function Login({ onLogin }) {
  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [error, setError]       = useState('');
  const [loading, setLoading]   = useState(false);
  const [showPass, setShowPass] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await fetch(`${API}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });
      if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.message || 'Email ou mot de passe incorrect');
      }
      const data = await res.json();
      localStorage.setItem('token', data.accessToken);
      localStorage.setItem('role', data.role || 'USER');
      onLogin(data.accessToken, data.role || 'USER');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      {/* Animated background orbs */}
      <div className="login-orb login-orb-1" />
      <div className="login-orb login-orb-2" />
      <div className="login-orb login-orb-3" />

      <div className="login-card">
        {/* Logo */}
        <div className="login-logo" style={{ display: 'flex', justifyContent: 'center', marginBottom: '1rem' }}>
          <img src={logo} alt="Orbyte Logo" style={{ height: '48px', width: 'auto' }} />
        </div>

        <p className="login-tagline">AI Platform · Connectez-vous à votre espace de travail</p>

        {/* Error banner */}
        {error && (
          <div className="login-error">
            <span className="login-error-icon">⚠</span>
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="login-form">
          {/* Email */}
          <div className="login-field">
            <label htmlFor="login-email">Adresse e-mail</label>
            <div className="login-input-wrap">
              <span className="login-input-icon">✉</span>
              <input
                id="login-email"
                type="email"
                placeholder="nom@entreprise.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                autoComplete="email"
                autoFocus
              />
            </div>
          </div>

          {/* Password */}
          <div className="login-field">
            <label htmlFor="login-password">Mot de passe</label>
            <div className="login-input-wrap">
              <span className="login-input-icon">🔒</span>
              <input
                id="login-password"
                type={showPass ? 'text' : 'password'}
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                autoComplete="current-password"
              />
              <button
                type="button"
                className="login-pass-toggle"
                onClick={() => setShowPass(v => !v)}
                tabIndex={-1}
              >
                {showPass ? <EyeOff size={16} /> : <Eye size={16} />}
              </button>
            </div>
          </div>

          <button
            type="submit"
            className="login-btn"
            disabled={loading}
            id="login-submit"
          >
            {loading ? (
              <span className="login-spinner" />
            ) : (
              <>
                <span>Se connecter</span>
                <span className="login-btn-arrow">→</span>
              </>
            )}
          </button>
        </form>

        <p className="login-hint">
          <span className="login-hint-dot" /> Connexion sécurisée via JWT · Orbyte AI
        </p>
      </div>
    </div>
  );
}
