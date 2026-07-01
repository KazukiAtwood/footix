import { Link } from 'react-router-dom';
import type { Player } from '../types';
import { POSITION_LABELS } from '../types';

interface PlayerCardProps {
  player: Player;
  compact?: boolean;
}

export function PlayerCard({ player, compact }: PlayerCardProps) {
  return (
    <Link to={`/joueur/${player.id}`} className={`player-card ${compact ? 'compact' : ''}`}>
      <img src={player.photoUrl} alt={player.name} className="player-photo" loading="lazy" />
      <span className="player-number">{player.number}</span>
      <div className="player-info">
        <strong>{player.name}</strong>
        <span className="player-position">{POSITION_LABELS[player.position] || player.position}</span>
      </div>
      {!compact && (
        <div className="player-mini-stats">
          {player.goals > 0 && <span>{player.goals} but(s)</span>}
          {player.yellowCards > 0 && <span>🟨 {player.yellowCards}</span>}
        </div>
      )}
    </Link>
  );
}

interface LineupSectionProps {
  title: string;
  formation: string;
  starters: Player[];
  bench: Player[];
  teamName: string;
  teamId?: string;
}

export function LineupSection({ title, formation, starters, bench, teamName, teamId }: LineupSectionProps) {
  if (starters.length === 0 && bench.length === 0) {
    return (
      <div className="lineup-section">
        <h3>{title}</h3>
        <p className="empty-lineup">Composition non disponible ({teamName})</p>
      </div>
    );
  }

  return (
    <div className="lineup-section">
      <div className="lineup-header">
        <h3>
          {teamId ? <Link to={`/equipe/${teamId}`}>{teamName}</Link> : teamName}
        </h3>
        <span className="formation-badge">Formation {formation}</span>
      </div>

      <h4>Titulaires</h4>
      <div className="lineup-grid">
        {starters.map((p) => (
          <PlayerCard key={p.id} player={p} compact />
        ))}
      </div>

      {bench.length > 0 && (
        <>
          <h4>Banc ({bench.length} joueurs)</h4>
          <div className="lineup-grid bench">
            {bench.map((p) => (
              <PlayerCard key={p.id} player={p} compact />
            ))}
          </div>
        </>
      )}
    </div>
  );
}
