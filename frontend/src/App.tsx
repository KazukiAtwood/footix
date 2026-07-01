import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { Layout } from './components/Layout';
import { FavoritesPage } from './pages/FavoritesPage';
import { GroupStagePage } from './pages/GroupStagePage';
import { HomePage } from './pages/HomePage';
import { KnockoutPage } from './pages/KnockoutPage';
import { MatchPage } from './pages/MatchPage';
import { PlayerPage } from './pages/PlayerPage';
import { PredictionsPage } from './pages/PredictionsPage';
import { TeamPage } from './pages/TeamPage';

function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/predictions" element={<PredictionsPage />} />
          <Route path="/groupes" element={<GroupStagePage />} />
          <Route path="/elimination" element={<KnockoutPage />} />
          <Route path="/match/:id" element={<MatchPage />} />
          <Route path="/equipe/:id" element={<TeamPage />} />
          <Route path="/joueur/:id" element={<PlayerPage />} />
          <Route path="/favoris" element={<FavoritesPage />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}

export default App;
