import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client';
import { MatchCard } from '../components/MatchCard';
import { useLiveRefresh } from '../hooks/useLiveRefresh';
import type { Match } from '../types';
import { isLiveMatch } from '../utils/matchResult';

export function HomePage() {
  const [liveMatches, setLiveMatches] = useState<Match[]>([]);
  const [recentMatches, setRecentMatches] = useState<Match[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadMatches = useCallback(() => {
    return api.getMatches().then((matches) => {
      const live = matches.filter((m) => isLiveMatch(m));
      const recent = matches.filter((m) => m.finished).slice(-6).reverse();
      setLiveMatches(live);
      setRecentMatches(recent);
    });
  }, []);

  useEffect(() => {
    loadMatches()
      .catch(() => setError('Impossible de charger les matchs. Vérifiez que le backend est démarré.'))
      .finally(() => setLoading(false));
  }, [loadMatches]);

  useLiveRefresh(() => { loadMatches().catch(() => {}); }, liveMatches.length > 0);

  if (loading) return <div className="loading">Chargement...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="page">
      <section className="hero">
        <h1>Coupe du Monde FIFA 2026</h1>
        <p>USA · Mexique · Canada — 48 équipes, 104 matchs</p>
        <div className="hero-links">
          <Link to="/predictions" className="btn btn-primary">Prédictions IA</Link>
          <Link to="/groupes" className="btn btn-secondary">Voir les groupes</Link>
        </div>
      </section>

      {liveMatches.length > 0 && (
        <section className="section">
          <h2>
            <span className="live-dot" /> Matchs en direct
            <small className="live-hint">Scores mis à jour toutes les 15 secondes</small>
          </h2>
          <div className="match-grid">
            {liveMatches.map((m) => (
              <MatchCard key={m.id} match={m} showPhase />
            ))}
          </div>
        </section>
      )}

      <section className="section">
        <h2>Derniers résultats</h2>
        <div className="match-grid">
          {recentMatches.map((m) => (
            <MatchCard key={m.id} match={m} showPhase />
          ))}
        </div>
      </section>
    </div>
  );
}
