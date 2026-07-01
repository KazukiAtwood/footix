import type { MatchEvent, TeamMatchStats } from '../types';

interface MatchStatsPanelProps {
  home: TeamMatchStats;
  away: TeamMatchStats;
  homePenaltyScore?: string;
  awayPenaltyScore?: string;
  unavailableStatistics: string[];
}

function StatRow({ label, home, away }: { label: string; home: string | number; away: string | number }) {
  return (
    <tr>
      <td className="stat-home">{home}</td>
      <td className="stat-label">{label}</td>
      <td className="stat-away">{away}</td>
    </tr>
  );
}

function EventList({ events, icon }: { events: MatchEvent[]; icon: string }) {
  if (events.length === 0) return <span className="no-data">—</span>;
  return (
    <ul className="event-list">
      {events.map((e, i) => (
        <li key={i}>{icon} {e.label || e.player}</li>
      ))}
    </ul>
  );
}

export function MatchStatsPanel({
  home,
  away,
  homePenaltyScore,
  awayPenaltyScore,
  unavailableStatistics,
}: MatchStatsPanelProps) {
  const hasPenalties =
    (homePenaltyScore && awayPenaltyScore) ||
    home.penaltyScorers.length > 0 ||
    away.penaltyScorers.length > 0;

  return (
    <section className="section match-stats-section">
      <h2>Statistiques & événements</h2>

      <div className="stats-team-headers">
        <span className="stats-team-name">{home.teamName}</span>
        <span className="stats-vs">vs</span>
        <span className="stats-team-name">{away.teamName}</span>
      </div>

      <table className="stats-table">
        <thead>
          <tr>
            <th>{home.teamName}</th>
            <th>Statistique</th>
            <th>{away.teamName}</th>
          </tr>
        </thead>
        <tbody>
          <StatRow label="Buts" home={home.goals} away={away.goals} />
          <tr className="stat-events-row">
            <td><EventList events={home.goalEvents} icon="⚽" /></td>
            <td className="stat-label">Buteurs</td>
            <td><EventList events={away.goalEvents} icon="⚽" /></td>
          </tr>
          {hasPenalties && (
            <>
              <StatRow
                label="Tirs au but (score)"
                home={homePenaltyScore || home.penaltyShootoutScored}
                away={awayPenaltyScore || away.penaltyShootoutScored}
              />
              <tr className="stat-events-row">
                <td>
                  {home.penaltyScorers.length > 0 ? (
                    <ul className="event-list">
                      {home.penaltyScorers.map((p, i) => <li key={i}>✅ {p}</li>)}
                    </ul>
                  ) : <span className="no-data">—</span>}
                </td>
                <td className="stat-label">TAB réussis</td>
                <td>
                  {away.penaltyScorers.length > 0 ? (
                    <ul className="event-list">
                      {away.penaltyScorers.map((p, i) => <li key={i}>✅ {p}</li>)}
                    </ul>
                  ) : <span className="no-data">—</span>}
                </td>
              </tr>
              <tr className="stat-events-row">
                <td>
                  {home.penaltyMisses.length > 0 ? (
                    <ul className="event-list">
                      {home.penaltyMisses.map((p, i) => <li key={i}>❌ {p}</li>)}
                    </ul>
                  ) : <span className="no-data">—</span>}
                </td>
                <td className="stat-label">TAB ratés</td>
                <td>
                  {away.penaltyMisses.length > 0 ? (
                    <ul className="event-list">
                      {away.penaltyMisses.map((p, i) => <li key={i}>❌ {p}</li>)}
                    </ul>
                  ) : <span className="no-data">—</span>}
                </td>
              </tr>
            </>
          )}
        </tbody>
      </table>

      {unavailableStatistics.length > 0 && (
        <div className="stats-unavailable">
          <p>
            Données non fournies par l&apos;API World Cup 2026 pour ce match :
          </p>
          <ul>
            {unavailableStatistics.map((s) => (
              <li key={s}>{s}</li>
            ))}
          </ul>
        </div>
      )}
    </section>
  );
}
