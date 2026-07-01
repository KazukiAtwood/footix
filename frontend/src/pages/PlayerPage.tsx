import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api } from '../api/client';
import type { Player } from '../types';
import { POSITION_LABELS } from '../types';

export function PlayerPage() {
  const { id } = useParams<{ id: string }>();
  const [player, setPlayer] = useState<Player | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) return;
    api.getPlayer(Number(id))
      .then(setPlayer)
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div className="loading">Chargement...</div>;
  if (!player) return <div className="error">Joueur introuvable.</div>;

  return (
    <div className="page">
      <Link to={`/equipe/${player.teamId}`} className="back-link">
        ← {player.teamName}
      </Link>

      <div className="player-profile-full">
        <img src={player.photoUrl} alt={player.name} className="player-photo-large" />
        <div className="player-profile-info">
          <span className="player-number-big">{player.number}</span>
          <h1>{player.name}</h1>
          <p className="subtitle">
            {POSITION_LABELS[player.position] || player.position} · {player.teamName}
            {player.starter ? ' · Titulaire' : ' · Banc'}
          </p>
          <p className="player-age">{player.age} ans</p>
        </div>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <span className="stat-value">{player.goals}</span>
          <span className="stat-label">Buts</span>
        </div>
        <div className="stat-card">
          <span className="stat-value">{player.assists}</span>
          <span className="stat-label">Passes D.</span>
        </div>
        <div className="stat-card">
          <span className="stat-value">{player.yellowCards}</span>
          <span className="stat-label">Cartons J.</span>
        </div>
        <div className="stat-card">
          <span className="stat-value">{player.redCards}</span>
          <span className="stat-label">Cartons R.</span>
        </div>
      </div>
    </div>
  );
}
