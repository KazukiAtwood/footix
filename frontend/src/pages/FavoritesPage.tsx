import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client';
import type { Favorite } from '../types';

export function FavoritesPage() {
  const [favorites, setFavorites] = useState<Favorite[]>([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    api.getFavorites()
      .then(setFavorites)
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const remove = async (matchId: string) => {
    await api.removeFavoriteById(matchId);
    setFavorites((f) => f.filter((x) => x.matchId !== matchId));
  };

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div className="page">
      <h1>Mes matchs favoris</h1>
      {favorites.length === 0 ? (
        <p className="empty-state">
          Aucun match suivi. Cliquez sur « Suivre » depuis une feuille de match.
        </p>
      ) : (
        <div className="favorites-list">
          {favorites.map((f) => (
            <div key={f.id} className="favorite-item">
              <Link to={`/match/${f.matchId}`} className="favorite-match">
                <strong>{f.homeTeam}</strong> vs <strong>{f.awayTeam}</strong>
                <span className="favorite-date">{f.matchDate}</span>
              </Link>
              <button className="btn-remove" onClick={() => remove(f.matchId)}>
                Retirer
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
