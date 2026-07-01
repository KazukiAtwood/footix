import type { Match } from '../types';

export type WinnerSide = 'home' | 'away' | 'draw' | null;

export function parseScore(s: string): number {
  const n = parseInt(s, 10);
  return Number.isNaN(n) ? 0 : n;
}

export function hasPenalties(match: Match): boolean {
  return !!(match.homePenaltyScore && match.awayPenaltyScore);
}

export function getWinner(match: Match): WinnerSide {
  if (!match.finished) return null;

  const home = parseScore(match.homeScore);
  const away = parseScore(match.awayScore);

  if (home > away) return 'home';
  if (away > home) return 'away';

  if (hasPenalties(match)) {
    const hp = parseScore(match.homePenaltyScore);
    const ap = parseScore(match.awayPenaltyScore);
    if (hp > ap) return 'home';
    if (ap > hp) return 'away';
  }

  return 'draw';
}

export function getWinnerName(match: Match): string | null {
  const winner = getWinner(match);
  if (winner === 'home') return match.homeTeamName;
  if (winner === 'away') return match.awayTeamName;
  return null;
}

export function formatScoreDisplay(match: Match): string {
  const home = match.homeScore ?? '0';
  const away = match.awayScore ?? '0';
  if (hasPenalties(match)) {
    return `${home} - ${away} (${match.homePenaltyScore}-${match.awayPenaltyScore} TAB)`;
  }
  return `${home} - ${away}`;
}

export function isLiveMatch(match: Match): boolean {
  return !match.finished && match.status !== 'notstarted';
}

export function needsLiveRefresh(matches: Match[]): boolean {
  return matches.some((m) => isLiveMatch(m) || (!m.finished && m.status === 'notstarted'));
}
