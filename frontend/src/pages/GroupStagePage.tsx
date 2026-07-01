import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client';
import { MatchCard } from '../components/MatchCard';
import { useLiveRefresh } from '../hooks/useLiveRefresh';
import type { Group, Match } from '../types';
import { isLiveMatch } from '../utils/matchResult';

export function GroupStagePage() {
  const [matches, setMatches] = useState<Match[]>([]);
  const [groups, setGroups] = useState<Group[]>([]);
  const [selectedGroup, setSelectedGroup] = useState<string>('all');
  const [loading, setLoading] = useState(true);

  const loadData = useCallback(() => {
    return Promise.all([api.getMatches('group'), api.getGroups()]).then(([m, g]) => {
      setMatches(m);
      setGroups(g);
    });
  }, []);

  useEffect(() => {
    loadData().finally(() => setLoading(false));
  }, [loadData]);

  const hasLive = matches.some((m) => isLiveMatch(m));
  useLiveRefresh(() => { loadData().catch(() => {}); }, hasLive);

  const filtered =
    selectedGroup === 'all'
      ? matches
      : matches.filter((m) => m.group === selectedGroup);

  const groupLetters = [...new Set(matches.map((m) => m.group))].sort();

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div className="page">
      <h1>Phase de groupes</h1>

      <div className="phase-tabs">
        <button
          className={selectedGroup === 'all' ? 'tab active' : 'tab'}
          onClick={() => setSelectedGroup('all')}
        >
          Tous
        </button>
        {groupLetters.map((g) => (
          <button
            key={g}
            className={selectedGroup === g ? 'tab active' : 'tab'}
            onClick={() => setSelectedGroup(g)}
          >
            Groupe {g}
          </button>
        ))}
      </div>

      <div className="two-col">
        <section className="section">
          <h2>Matchs</h2>
          <div className="match-list">
            {filtered.map((m) => (
              <MatchCard key={m.id} match={m} />
            ))}
          </div>
        </section>

        <section className="section">
          <h2>Classements</h2>
          {groups
            .filter((g) => selectedGroup === 'all' || g.group === selectedGroup)
            .map((g) => (
              <div key={g.group} className="standings-table">
                <h3>Groupe {g.group}</h3>
                <table>
                  <thead>
                    <tr>
                      <th>Équipe</th>
                      <th>Pts</th>
                      <th>BP</th>
                      <th>BC</th>
                      <th>Diff</th>
                    </tr>
                  </thead>
                  <tbody>
                    {g.standings.map((s) => (
                      <tr key={s.teamId}>
                        <td>
                          <Link to={`/equipe/${s.teamId}`} className="team-link">
                            {s.flag && <img src={s.flag} alt="" className="flag-mini" />}
                            {s.teamName}
                          </Link>
                        </td>
                        <td>{s.points}</td>
                        <td>{s.goalsFor}</td>
                        <td>{s.goalsAgainst}</td>
                        <td>{s.goalDifference > 0 ? '+' : ''}{s.goalDifference}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ))}
        </section>
      </div>
    </div>
  );
}
