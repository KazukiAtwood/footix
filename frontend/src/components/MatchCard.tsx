import { Link } from 'react-router-dom';
import type { Match } from '../types';
import { formatScoreDisplay, getWinner, getWinnerName } from '../utils/matchResult';

interface MatchCardProps {
  match: Match;
  showPhase?: boolean;
}

function getStatusLabel(match: Match): { text: string; className: string } {
  if (match.finished) return { text: 'Terminé', className: 'status-finished' };
  if (match.status !== 'notstarted') return { text: 'En cours', className: 'status-live' };
  return { text: 'À venir', className: 'status-upcoming' };
}

export function MatchCard({ match, showPhase }: MatchCardProps) {
  const status = getStatusLabel(match);
  const hasScore = match.finished || match.status !== 'notstarted';
  const winner = getWinner(match);
  const winnerName = getWinnerName(match);

  return (
    <Link to={`/match/${match.id}`} className="match-card">
      {showPhase && <span className="match-phase">{match.phaseLabel}</span>}
      <div className="match-teams">
        <span className={`team-name ${winner === 'home' ? 'team-winner' : ''}`}>
          {winner === 'home' && <span className="winner-crown">🏆</span>}
          {match.homeTeamName}
        </span>
        <div className="match-score-block">
          {hasScore ? (
            <span className="match-score">{formatScoreDisplay(match)}</span>
          ) : (
            <span className="match-vs">VS</span>
          )}
          <span className={`match-status ${status.className}`}>{status.text}</span>
          {match.finished && winner === 'draw' && (
            <span className="result-badge draw">Match nul</span>
          )}
          {match.finished && winnerName && (
            <span className="result-badge win">Victoire {winnerName}</span>
          )}
        </div>
        <span className={`team-name ${winner === 'away' ? 'team-winner' : ''}`}>
          {winner === 'away' && <span className="winner-crown">🏆</span>}
          {match.awayTeamName}
        </span>
      </div>
      <div className="match-meta">
        <span>{match.date}</span>
        {match.stadiumName && <span>{match.stadiumName}, {match.stadiumCity}</span>}
        {match.group && match.type === 'group' && <span>Groupe {match.group}</span>}
        {match.status !== 'notstarted' && !match.finished && (
          <span className="live-indicator"><span className="live-dot" /> Temps réel</span>
        )}
      </div>
    </Link>
  );
}
