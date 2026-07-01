# Footix — Coupe du Monde 2026

Application web de suivi de la Coupe du Monde FIFA 2026, avec **React** (frontend) et **Java Spring Boot** (backend).

## Fonctionnalités

- Consultation des matchs par phase (groupes, seizièmes, huitièmes, quarts, demis, petite finale, finale)
- Feuille de match détaillée avec scores et buteurs
- Mises à jour en temps réel (SSE + fallback polling 15s)
- Prédictions IA pour les matchs à venir
- Fiches équipes et joueurs
- Classements des groupes
- Favoris locaux (suivre un match)

## Architecture

```
footix/
├── backend/     → Java Spring Boot (port 8080)
└── frontend/    → React + Vite (port 5173)
```

Le backend agit comme **proxy sécurisé** vers l'API [World Cup 2026](https://github.com/rezarahiminia/worldcup2026) : le token JWT n'est jamais exposé au navigateur.

## Prérequis

- Java 21+
- Node.js 20+

## Démarrage

### 1. Backend

```bash
cd backend
./mvnw spring-boot:run        # Linux/Mac
.\mvnw.cmd spring-boot:run    # Windows
```

Le backend s'enregistre automatiquement auprès de l'API World Cup 2026 au démarrage.

### 2. Frontend

```bash
cd frontend
npm install
npm run dev
```

Ouvrir [http://localhost:5173](http://localhost:5173)

## Configuration (optionnelle)

Dans `backend/src/main/resources/application.properties` :

```properties
worldcup.api.token=VOTRE_TOKEN_JWT
# ou
worldcup.api.email=votre@email.com
worldcup.api.password=votre_mot_de_passe
```

## API REST Footix

| Endpoint | Description |
|----------|-------------|
| `GET /api/matches?phase=group` | Matchs par phase |
| `GET /api/matches/{id}` | Détail d'un match |
| `GET /api/matches/{id}/stream` | SSE temps réel |
| `GET /api/teams` | Équipes |
| `GET /api/teams/{id}` | Fiche équipe + effectif |
| `GET /api/players/{id}` | Fiche joueur |
| `GET /api/groups` | Classements |
| `GET /api/predictions` | Prédictions IA |
| `GET /api/favorites` | Favoris locaux |

## Sécurité

- Token API stocké côté serveur uniquement
- Validation des entrées (IDs match, sanitization)
- CORS restreint au frontend
- Communication HTTPS recommandée en production

## Technologies

- **Frontend** : React 19, TypeScript, Vite, React Router
- **Backend** : Spring Boot 4, JPA/H2, WebClient, SSE
- **API externe** : [worldcup26.ir](https://worldcup26.ir)
