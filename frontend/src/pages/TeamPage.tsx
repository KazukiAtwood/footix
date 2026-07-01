import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api } from '../api/client';
import { PlayerCard } from '../components/PlayerCard';
import type { Team } from '../types';

export function TeamPage() {
  const { id } = useParams<{ id: string }>();
  const [team, setTeam] = useState<Team | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!id) return;
    api.getTeam(id)
      .then(setTeam)
      .catch(() => setError('Équipe introuvable.'))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div className="loading">Chargement...</div>;
  if (error || !team) return <div className="error">{error}</div>;

  return (
    <div className="page">
      <div className="team-header">
        {team.flag && <img src={team.flag} alt={team.name} className="flag-large" />}
        <div>
          <h1>{team.name}</h1>
          <p className="subtitle">
            {team.fifaCode} · Groupe {team.group} · Formation {team.formation}
          </p>
        </div>
      </div>

      <section className="section">
        <h2>Titulaires ({team.starters.length})</h2>
        <div className="player-grid">
          {team.starters.map((p) => (
            <PlayerCard key={p.id} player={p} />
          ))}
        </div>
      </section>

      <section className="section">
        <h2>Banc de touche ({team.bench.length} joueurs)</h2>
        <p className="subtitle">Effectif élargi — Coupe du Monde 2026 (26 joueurs)</p>
        <div className="player-grid">
          {team.bench.map((p) => (
            <PlayerCard key={p.id} player={p} />
          ))}
        </div>
      </section>
    </div>
  );
}
