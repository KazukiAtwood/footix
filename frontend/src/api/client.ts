import type { Favorite, Group, Match, MatchDetail, Phase, Player, Prediction, Team } from '../types';

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

async function fetchJson<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  if (!res.ok) {
    throw new Error(`Erreur API (${res.status})`);
  }
  return res.json();
}

export const api = {
  getMatches: (phase?: Phase) =>
    fetchJson<Match[]>(phase ? `/matches?phase=${phase}` : '/matches'),

  getMatch: (id: string) => fetchJson<Match>(`/matches/${id}`),

  getMatchDetail: (id: string) => fetchJson<MatchDetail>(`/matches/${id}?detail=true`),

  getTeams: (group?: string) =>
    fetchJson<Team[]>(group ? `/teams?group=${group}` : '/teams'),

  getTeam: (id: string) => fetchJson<Team>(`/teams/${id}`),

  getPlayer: (id: number) => fetchJson<Player>(`/players/${id}`),

  getGroups: () => fetchJson<Group[]>('/groups'),

  getPredictions: () => fetchJson<Prediction[]>('/predictions'),

  getPrediction: (matchId: string) => fetchJson<Prediction>(`/predictions/${matchId}`),

  getFavorites: () => fetchJson<Favorite[]>('/favorites'),

  addFavorite: (matchId: string) =>
    fetchJson<Favorite>(`/matches/${matchId}/favorite`, { method: 'POST' }),

  removeFavorite: (matchId: string) =>
    fetch(`${API_BASE}/matches/${matchId}/favorite`, { method: 'DELETE' }),

  removeFavoriteById: (matchId: string) =>
    fetch(`${API_BASE}/favorites/${matchId}`, { method: 'DELETE' }),
};

export function getStreamUrl(matchId: string): string {
  return `${API_BASE}/matches/${matchId}/stream`;
}
