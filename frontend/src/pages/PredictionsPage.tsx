import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client';
import type { Prediction } from '../types';

export function PredictionsPage() {
  const [predictions, setPredictions] = useState<Prediction[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    api.getPredictions()
      .then(setPredictions)
      .catch(() => setError('Impossible de charger les prédictions.'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading">Analyse IA en cours...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="page">
      <h1>Prédictions IA</h1>
      <p className="subtitle">Prévisions pour les prochains matchs à venir</p>

      <div className="prediction-grid">
        {predictions.map((p) => (
          <div key={p.matchId} className="prediction-card">
            <div className="prediction-header">
              <Link to={`/match/${p.matchId}`} className="prediction-teams">
                {p.homeTeam} vs {p.awayTeam}
              </Link>
              <span className="prediction-date">{p.date}</span>
            </div>

            <div className="prediction-winner">
              Vainqueur prédit : <strong>{p.predictedWinner}</strong>
              <span className={`confidence confidence-${p.confidence.toLowerCase()}`}>
                Confiance {p.confidence}
              </span>
            </div>

            <div className="prob-bars">
              <div className="prob-row">
                <span>{p.homeTeam}</span>
                <div className="prob-bar">
                  <div className="prob-fill home" style={{ width: `${p.homeWinProbability}%` }} />
                </div>
                <span>{p.homeWinProbability}%</span>
              </div>
              <div className="prob-row">
                <span>Nul</span>
                <div className="prob-bar">
                  <div className="prob-fill draw" style={{ width: `${p.drawProbability}%` }} />
                </div>
                <span>{p.drawProbability}%</span>
              </div>
              <div className="prob-row">
                <span>{p.awayTeam}</span>
                <div className="prob-bar">
                  <div className="prob-fill away" style={{ width: `${p.awayWinProbability}%` }} />
                </div>
                <span>{p.awayWinProbability}%</span>
              </div>
            </div>

            <p className="prediction-analysis">{p.analysis}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
