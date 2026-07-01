import { NavLink } from 'react-router-dom';

const links = [
  { to: '/', label: 'Accueil' },
  { to: '/predictions', label: 'Prédictions IA' },
  { to: '/groupes', label: 'Phase de groupes' },
  { to: '/elimination', label: 'Phase finale' },
  { to: '/favoris', label: 'Favoris' },
];

export function Layout({ children }: { children: React.ReactNode }) {
  return (
    <div className="app">
      <header className="header">
        <div className="header-inner">
          <NavLink to="/" className="logo">
            <span className="logo-icon">⚽</span>
            <span className="logo-text">
              Footix <small>Coupe du Monde 2026</small>
            </span>
          </NavLink>
          <nav className="nav">
            {links.map((l) => (
              <NavLink
                key={l.to}
                to={l.to}
                className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
                end={l.to === '/'}
              >
                {l.label}
              </NavLink>
            ))}
          </nav>
        </div>
      </header>
      <main className="main">{children}</main>
      <footer className="footer">
        Footix — Données via{' '}
        <a href="https://worldcup26.ir" target="_blank" rel="noreferrer">
          World Cup 2026 API
        </a>
      </footer>
    </div>
  );
}
