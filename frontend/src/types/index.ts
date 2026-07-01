export interface Match {
  id: string;
  homeTeamId: string;
  awayTeamId: string;
  homeTeamName: string;
  awayTeamName: string;
  homeTeamLabel: string;
  awayTeamLabel: string;
  homeScore: string;
  awayScore: string;
  homeScorers: string;
  awayScorers: string;
  group: string;
  matchday: string;
  date: string;
  stadiumId: string;
  stadiumName: string;
  stadiumCity: string;
  finished: boolean;
  status: string;
  type: string;
  phaseLabel: string;
  homePenaltyScore: string;
  awayPenaltyScore: string;
}

export interface Player {
  id: number;
  name: string;
  position: string;
  number: number;
  yellowCards: number;
  redCards: number;
  teamId: string;
  teamName: string;
  photoUrl: string;
  starter: boolean;
  goals: number;
  assists: number;
  age: number;
}

export interface Team {
  id: string;
  name: string;
  fifaCode: string;
  group: string;
  flag: string;
  country: string;
  formation: string;
  starters: Player[];
  bench: Player[];
}

export interface MatchDetail {
  match: Match;
  homeFormation: string;
  awayFormation: string;
  homeStarters: Player[];
  homeBench: Player[];
  awayStarters: Player[];
  awayBench: Player[];
  homeStats: TeamMatchStats;
  awayStats: TeamMatchStats;
  unavailableStatistics: string[];
}

export interface MatchEvent {
  type: string;
  player: string;
  minute: string;
  label: string;
}

export interface TeamMatchStats {
  teamName: string;
  goals: number;
  goalEvents: MatchEvent[];
  penaltyShootoutScored: number;
  penaltyShootoutMissed: number;
  penaltyScorers: string[];
  penaltyMisses: string[];
}

export interface Group {
  group: string;
  standings: GroupStanding[];
}

export interface GroupStanding {
  teamId: string;
  teamName: string;
  flag: string;
  points: number;
  goalsFor: number;
  goalsAgainst: number;
  goalDifference: number;
}

export interface Prediction {
  matchId: string;
  homeTeam: string;
  awayTeam: string;
  date: string;
  predictedWinner: string;
  homeWinProbability: number;
  drawProbability: number;
  awayWinProbability: number;
  confidence: string;
  analysis: string;
}

export interface Favorite {
  id: number;
  matchId: string;
  homeTeam: string;
  awayTeam: string;
  matchDate: string;
}

export type Phase =
  | 'group'
  | 'r32'
  | 'r16'
  | 'qf'
  | 'sf'
  | 'third'
  | 'final';

export const PHASES: { key: Phase; label: string }[] = [
  { key: 'group', label: 'Phase de groupes' },
  { key: 'r32', label: 'Seizièmes de finale' },
  { key: 'r16', label: 'Huitièmes de finale' },
  { key: 'qf', label: 'Quarts de finale' },
  { key: 'sf', label: 'Demi-finales' },
  { key: 'third', label: 'Petite finale' },
  { key: 'final', label: 'Finale' },
];

export const POSITION_LABELS: Record<string, string> = {
  GK: 'Gardien',
  DF: 'Défenseur',
  MF: 'Milieu',
  FW: 'Attaquant',
};
