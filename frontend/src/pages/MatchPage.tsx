import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api } from '../api/client';
import { LineupSection } from '../components/PlayerCard';
import { MatchStatsPanel } from '../components/MatchStatsPanel';
import { useMatchStream } from '../hooks/useMatchStream';
import type { MatchDetail } from '../types';
import { formatScoreDisplay, getWinner, getWinnerName } from '../utils/matchResult';

function parseScorers(raw: string): string[] {
  if (!raw || raw === 'null') return [];
  const matches = raw.match(/[^,{}\[\]]+/g);
  if (!matches) return [];
  return matches
    .map((s) => s.replace(/["\\]/g, '').trim())
    .filter((s) => s.length > 1 && s !== 'null');
}

export function MatchPage() {
  const { id } = useParams<{ id: string }>();
  const [detail, setDetail] = useState<MatchDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [isFavorite, setIsFavorite] = useState(false);

  useEffect(() => {
    if (!id) return;
    Promise.all([api.getMatchDetail(id), api.getFavorites()])
      .then(([d, favs]) => {
        setDetail(d);
        setIsFavorite(favs.some((f) => f.matchId === id));
      })
      .catch(() => setError('Match introuvable.'))
      .finally(() => setLoading(false));
  }, [id]);

  const streamed = useMatchStream(id, detail?.match ?? null);
  const match = streamed ?? detail?.match;

  useEffect(() => {
    if (!id || !match || loading) return;
    api.getMatchDetail(id).then(setDetail).catch(() => {});
  }, [id, match?.homeScore, match?.awayScore, match?.finished, loading]);

  const toggleFavorite = async () => {
    if (!id) return;
    if (isFavorite) {
      await api.removeFavorite(id);
      setIsFavorite(false);
    } else {
      await api.addFavorite(id);
      setIsFavorite(true);
    }
  };

  if (loading) return <div className="loading">Chargement de la feuille de match...</div>;
  if (error || !match || !detail) return <div className="error">{error || 'Match introuvable'}</div>;

  const isLive = !match.finished && match.status !== 'notstarted';
  const homeScorers = parseScorers(match.homeScorers);
  const awayScorers = parseScorers(match.awayScorers);
  const winner = getWinner(match);
  const winnerName = getWinnerName(match);

  return (
    <div className="page match-detail">
      <div className="match-detail-header">
        <span className="phase-badge">{match.phaseLabel}</span>
        {isLive && (
          <span className="live-badge">
            <span className="live-dot" /> En direct — mise à jour toutes les 15 s
          </span>
        )}
        {match.finished && winnerName && (
          <span className="result-badge win">🏆 Victoire {winnerName}</span>
        )}
        {match.finished && winner === 'draw' && (
          <span className="result-badge draw">Match nul</span>
        )}
        <button className={`btn-fav ${isFavorite ? 'active' : ''}`} onClick={toggleFavorite}>
          {isFavorite ? '★ Favori' : '☆ Suivre'}
        </button>
      </div>

      <div className="match-detail-scoreboard">
        <div className="team-block">
          {match.homeTeamId !== '0' ? (
            <Link to={`/equipe/${match.homeTeamId}`} className={`team-link-lg ${winner === 'home' ? 'team-winner' : ''}`}>
              {winner === 'home' && '🏆 '}{match.homeTeamName}
            </Link>
          ) : (
            <span className={`team-link-lg ${winner === 'home' ? 'team-winner' : ''}`}>
              {match.homeTeamName || match.homeTeamLabel}
            </span>
          )}
          {homeScorers.length > 0 && (
            <ul className="scorers">
              {homeScorers.map((s, i) => <li key={i}>⚽ {s}</li>)}
            </ul>
          )}
        </div>

        <div className="score-display">
          <span className="score-big">{formatScoreDisplay(match)}</span>
          <span className="match-status-text">
            {match.finished ? 'Terminé' : isLive ? `${match.status}'` : 'À venir'}
          </span>
        </div>

        <div className="team-block">
          {match.awayTeamId !== '0' ? (
            <Link to={`/equipe/${match.awayTeamId}`} className={`team-link-lg ${winner === 'away' ? 'team-winner' : ''}`}>
              {winner === 'away' && '🏆 '}{match.awayTeamName}
            </Link>
          ) : (
            <span className={`team-link-lg ${winner === 'away' ? 'team-winner' : ''}`}>
              {match.awayTeamName || match.awayTeamLabel}
            </span>
          )}
          {awayScorers.length > 0 && (
            <ul className="scorers">
              {awayScorers.map((s, i) => <li key={i}>⚽ {s}</li>)}
            </ul>
          )}
        </div>
      </div>

      <div className="match-info-grid">
        <div className="info-card">
          <h3>Informations</h3>
          <dl>
            <dt>Date</dt><dd>{match.date}</dd>
            {match.stadiumName && (
              <>
                <dt>Stade</dt><dd>{match.stadiumName}, {match.stadiumCity}</dd>
              </>
            )}
            {match.group && match.type === 'group' && (
              <>
                <dt>Groupe</dt><dd>{match.group}</dd>
              </>
            )}
            <dt>Statut</dt>
            <dd>{match.finished ? 'Terminé' : isLive ? 'En cours' : 'À venir'}</dd>
          </dl>
        </div>
      </div>

      <MatchStatsPanel
        home={detail.homeStats}
        away={detail.awayStats}
        homePenaltyScore={match.homePenaltyScore}
        awayPenaltyScore={match.awayPenaltyScore}
        unavailableStatistics={detail.unavailableStatistics}
      />

      <section className="section lineups">
        <h2>Compositions</h2>
        <div className="lineups-grid">
          <LineupSection
            title="Domicile"
            teamName={match.homeTeamName}
            teamId={match.homeTeamId !== '0' ? match.homeTeamId : undefined}
            formation={detail.homeFormation}
            starters={detail.homeStarters}
            bench={detail.homeBench}
          />
          <LineupSection
            title="Extérieur"
            teamName={match.awayTeamName}
            teamId={match.awayTeamId !== '0' ? match.awayTeamId : undefined}
            formation={detail.awayFormation}
            starters={detail.awayStarters}
            bench={detail.awayBench}
          />
        </div>
      </section>
    </div>
  );
}
