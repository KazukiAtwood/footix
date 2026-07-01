import { useCallback, useEffect, useState } from 'react';
import { api } from '../api/client';
import { MatchCard } from '../components/MatchCard';
import { useLiveRefresh } from '../hooks/useLiveRefresh';
import type { Match, Phase } from '../types';
import { PHASES } from '../types';
import { isLiveMatch } from '../utils/matchResult';

const KNOCKOUT_PHASES: Phase[] = ['r32', 'r16', 'qf', 'sf', 'third', 'final'];

export function KnockoutPage() {
  const [activePhase, setActivePhase] = useState<Phase>('r32');
  const [matches, setMatches] = useState<Match[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    api.getMatches(activePhase)
      .then(setMatches)
      .finally(() => setLoading(false));
  }, [activePhase]);

  const hasLive = matches.some((m) => isLiveMatch(m));
  useLiveRefresh(() => {
    api.getMatches(activePhase).then(setMatches).catch(() => {});
  }, hasLive);

  return (
    <div className="page">
      <h1>Phase finale</h1>

      <div className="phase-tabs">
        {PHASES.filter((p) => KNOCKOUT_PHASES.includes(p.key)).map((p) => (
          <button
            key={p.key}
            className={activePhase === p.key ? 'tab active' : 'tab'}
            onClick={() => setActivePhase(p.key)}
          >
            {p.label}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="loading">Chargement...</div>
      ) : (
        <div className="match-grid">
          {matches.map((m) => (
            <MatchCard key={m.id} match={m} />
          ))}
        </div>
      )}
    </div>
  );
}
