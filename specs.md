# Spécifications Techniques - Jordel

Ce document liste les fonctionnalités planifiées et les TODOs techniques pour le projet Jordel.

---

## 🔐 Auth - Authentication

### ✅ Fonctionnalités Complétées

#### JWT Token Management
- [x] **Générer un token JWT pour un utilisateur** - Implémenté dans `JWTService.java`
  - Access token: 15 minutes
  - Refresh token: 30 jours
  - Retourné lors du login

- [x] **Refresh un access token** - Logique implémentée
  - Endpoint partiellement implémenté
  - Validation du refresh token OK

### 🚧 Fonctionnalités En Cours

#### Token Refresh Endpoint
- [ ] **Recréer un access token si le refresh token est valide**
  - **Status:** Logique métier OK, endpoint à finaliser
  - **Fichier:** `auth/io/web/RefreshTokenController.java` (à créer ou finaliser)
  - **Flow:**
    1. Client envoie refresh token
    2. Valider refresh token (expiration, signature)
    3. Charger utilisateur depuis read_cache
    4. Générer nouveau access token
    5. Retourner `{ accessToken: "..." }`
  - **Endpoint:** `POST /auth/refresh`
  - **Request:** `{ "refreshToken": "..." }`
  - **Response:** `{ "accessToken": "..." }`

### 📋 Fonctionnalités Planifiées

#### Token Rotation (Sécurité Avancée)
- [ ] **Refresh les 2 tokens à chaque requête**
  - **Objectif:** Améliorer la sécurité en invalidant les anciens tokens
  - **Impact:** Breaking change pour clients existants
  - **Flow proposé:**
    1. Client envoie refresh token
    2. Backend génère NOUVEAU access token ET NOUVEAU refresh token
    3. Ancien refresh token est invalidé
    4. Client doit stocker les 2 nouveaux tokens
  - **Complexité:** Nécessite tracking des refresh tokens (table ou cache)
  - **Questions à résoudre:**
    - Stocker refresh tokens en base ? (actuellement stateless)
    - Gestion de la revocation ?
    - Grace period pour éviter race conditions ?

#### OTP SMS Authentication
- [ ] **Send OTP qui renvoie un SMS avec un OTP à usage dans les 15 minutes**
  - **Objectif:** 2FA par SMS
  - **Provider SMS:** À déterminer (Twilio, Vonage, etc.)
  - **Flow:**
    1. User demande login OTP avec numéro téléphone
    2. Backend génère OTP 6 chiffres
    3. Stocker OTP avec expiration 15 min (Redis ou table temp)
    4. Envoyer SMS via provider
    5. Retourner sessionId au client
  - **Endpoint:** `POST /auth/send-otp`
  - **Request:** `{ "phoneNumber": "+33612345678" }`
  - **Response:** `{ "sessionId": "...", "expiresIn": 900 }`

- [ ] **Login qui renvoie un couple de token JWT, si le couple OTP / tel est OK**
  - **Flow:**
    1. Client envoie sessionId + OTP
    2. Backend valide OTP (correct + non expiré)
    3. Charger user par phoneNumber
    4. Générer access + refresh tokens
    5. Invalider OTP
  - **Endpoint:** `POST /auth/login-otp`
  - **Request:** `{ "sessionId": "...", "otp": "123456" }`
  - **Response:** `{ "accessToken": "...", "refreshToken": "..." }`
  - **Dépendances:**
    - Ajouter `phoneNumber` à `UserAccount` aggregate
    - Event `PhoneNumberAddedEvent`
    - Validation numéro international

---

## 🏉 Team Building - Team Creation

### ✅ Fonctionnalités Complétées (Domaine)

- [x] Agrégat Team polymorphique avec machine à états
- [x] Enregistrement équipe draft
- [x] Sélection ruleset (BB2020, BB2016, etc.)
- [x] Choix de roster (Humans, Orcs, etc.)
- [x] Recrutement joueurs avec validation:
  - Budget disponible
  - Limites par type de joueur
  - Cross-limits (ex: max 4 Blitzers + Catchers combinés)
  - Max 16 joueurs total
- [x] Retrait joueurs
- [x] Achat staff (Apothecary, Coaches, Cheerleaders, Fans)
- [x] Retrait staff
- [x] Achat rerolls (max 8)
- [x] Retrait rerolls
- [x] Changement de roster (reset équipe)
- [x] Tous les événements définis
- [x] Tests domaine complets

### 🚧 Fonctionnalités En Cours

#### Infrastructure Use Cases
- [ ] **CommandHandlers** - Pattern Auth à suivre
  - [ ] `RegisterDraftTeamCommandHandler`
  - [ ] `SelectRulesetCommandHandler`
  - [ ] `ChooseRosterCommandHandler`
  - [ ] `HirePlayerCommandHandler`
  - [ ] `RemovePlayerCommandHandler`
  - [ ] `PurchaseTeamStaffCommandHandler`
  - [ ] `RemoveTeamStaffCommandHandler`
  - [ ] `PurchaseTeamRerollCommandHandler`
  - [ ] `RemoveTeamRerollCommandHandler`
  - [ ] `ChangeRosterCommandHandler`

#### Projectors
- [ ] **TeamProjector** - Update read_cache
  - Écouter tous les TeamDomainEvents
  - Créer/mettre à jour snapshots équipes
  - Structure read cache à définir:
    ```json
    {
      "teamId": "...",
      "teamName": "...",
      "coachName": "...",
      "roster": { "id": "...", "name": "..." },
      "budget": 150000,
      "players": [...],
      "staff": [...],
      "rerolls": 3
    }
    ```

#### REST API
- [ ] **TeamController** - Endpoints CRUD
  - `POST /teams/draft` - Créer équipe draft
  - `POST /teams/{id}/select-ruleset` - Choisir ruleset
  - `POST /teams/{id}/choose-roster` - Choisir roster
  - `POST /teams/{id}/hire-player` - Recruter joueur
  - `DELETE /teams/{id}/players/{playerId}` - Retirer joueur
  - `POST /teams/{id}/purchase-staff` - Acheter staff
  - `POST /teams/{id}/purchase-reroll` - Acheter reroll
  - `GET /teams/{id}` - Détail équipe
  - `GET /teams?coach={coachName}` - Liste équipes d'un coach

#### Tests
- [ ] **Unit tests CommandHandlers** - Avec `FakeEventStore`
- [ ] **Integration tests** - Tests HTTP endpoints complets

### 📋 Fonctionnalités Planifiées

#### Validation Équipe Complète
- [ ] **Vérifier qu'une équipe est valide pour commencer à jouer**
  - Min 11 joueurs requis
  - Budget dépensé/restant
  - Composition valide selon règles roster
  - Status "ready" vs "draft"

#### Team Template/Import
- [ ] **Sauvegarder équipe comme template**
  - Permettre réutilisation composition
  - Export JSON

- [ ] **Importer équipe depuis template**
  - Validation budget
  - Adaptation si roster changé

---

## 📝 Authoring - Blog

### ✅ Fonctionnalités Complétées

- [x] CRUD articles complet
- [x] Gestion paragraphes
- [x] Système commentaires
- [x] Pagination (20 articles/page)
- [x] Support Markdown et emojis

### 📋 Fonctionnalités Planifiées

#### Modération
- [ ] **Système de modération commentaires**
  - Flag commentaire comme spam
  - Modération par admin
  - Auto-modération avec mots-clés

#### Organisation
- [ ] **Catégories d'articles**
  - Créer/modifier catégories
  - Associer articles à catégories
  - Filtrer par catégorie

- [ ] **Tags d'articles**
  - Tags libres
  - Autocomplétion
  - Recherche par tag

#### Édition
- [ ] **Brouillons**
  - État "draft" vs "published"
  - Prévisualisation avant publication

- [ ] **Historique versions**
  - Tracking modifications
  - Rollback version précédente

---

## 🎮 Shared - Reference Data

### ✅ Fonctionnalités Complétées

- [x] Chargement JSON au startup
- [x] Cache in-memory thread-safe
- [x] 24 rosters Blood Bowl
- [x] 300+ player definitions
- [x] Skills et special rules

### 🚧 Tâches En Cours

- [ ] **Tracker `RefDataServiceConfig.java`**
  - Fichier actuellement non tracké
  - À ajouter au repo

### 📋 Fonctionnalités Planifiées

#### API REST Reference Data
- [ ] **Endpoints publics pour reference data**
  - `GET /ref/rosters` - Liste rosters
  - `GET /ref/rosters/{id}` - Détail roster
  - `GET /ref/players?roster={id}` - Joueurs d'un roster
  - `GET /ref/skills` - Liste skills
  - Utile pour frontend

#### Versioning Rules
- [ ] **Support multiple versions règles Blood Bowl**
  - BB2020, BB2016, etc.
  - Permettre choix version lors création équipe
  - Namespace JSON par version

---

## 🏆 Team Management - Future Module

### 📋 Fonctionnalités Planifiées

**Status:** Module non démarré (0%)

#### Match Management
- [ ] Créer match entre 2 équipes
- [ ] Enregistrer résultat match
- [ ] Calcul SPP (Star Player Points)
- [ ] Mise à jour statistiques équipes

#### Player Progression
- [ ] Montée de niveau joueurs
- [ ] Gain de skills
- [ ] Augmentation caractéristiques
- [ ] Blessures permanentes
- [ ] Mort de joueurs

#### Team Evolution
- [ ] Achat joueurs post-création
- [ ] Vente joueurs
- [ ] Gain revenus
- [ ] Valeur d'équipe (Team Value)
- [ ] Inducements

#### League Management
- [ ] Créer ligue
- [ ] Inscription équipes à ligue
- [ ] Calendrier matchs
- [ ] Classement
- [ ] Playoffs

---

## 🔧 Infrastructure & DevOps

### 📋 Améliorations Techniques

#### Performance
- [ ] **Caching stratégique**
  - Cache read_cache queries fréquentes (Redis ?)
  - Cache reference data déjà en RAM
  - Cache résultats validation

#### Monitoring
- [ ] **Logging structuré**
  - Adopter SLF4J structured logging
  - Correlation IDs pour tracing

- [ ] **Métriques**
  - Spring Actuator metrics
  - Event dispatch latency
  - Read cache hit ratio

#### Testing
- [ ] **Contract testing**
  - Spring Cloud Contract pour API
  - Garantir compatibilité clients

- [ ] **Performance testing**
  - JMeter ou Gatling
  - Event store performance sous charge

#### Documentation
- [ ] **OpenAPI/Swagger**
  - Documentation API auto-générée
  - Swagger UI pour tester endpoints

---

## 📚 Documentation

### ✅ Complété

- [x] CLAUDE.md - Guide pour Claude Code
- [x] QUICK_START.md - Démarrage rapide
- [x] BOUNDED_CONTEXTS.md - État modules
- [x] CODEBASE_MAP.md - Navigation code
- [x] Authoring.md - Specs blog

### 📋 À Créer

- [ ] **ARCHITECTURE.md** - Détails techniques approfondis
  - Event Store implementation
  - Polymorphic serialization
  - Read Cache strategy
  - Testing patterns

- [ ] **DEPLOYMENT.md** - Guide déploiement
  - Docker compose production
  - Variables environnement
  - Migrations database
  - Backup/restore

- [ ] **API.md** - Documentation complète API
  - Tous les endpoints
  - Exemples requêtes/réponses
  - Codes erreur
  - Flow d'authentification

---

## ✅ Checklist Before Production

### Auth Module
- [ ] Finaliser token refresh endpoint
- [ ] Tests load pour JWT validation
- [ ] Rate limiting login attempts
- [ ] Audit log connexions

### Team Building Module
- [ ] Implémenter tous les CommandHandlers
- [ ] Créer REST API complète
- [ ] Tests integration complets
- [ ] Performance tests création équipe

### Infrastructure
- [ ] Backup automatique event_log
- [ ] Monitoring événements
- [ ] Alerts erreurs critiques
- [ ] Documentation API complète

### Sécurité
- [ ] Audit sécurité complet
- [ ] Penetration testing
- [ ] HTTPS obligatoire
- [ ] Secrets management (vault)

---

## 📝 Notes

**Priorités court terme:**
1. Finaliser Team Building (CommandHandlers + API)
2. Token refresh endpoint Auth
3. Tests integration Team Building

**Priorités moyen terme:**
4. OTP SMS authentication
5. Team Management module design
6. Monitoring et observability

**Priorités long terme:**
7. Team Management implementation
8. Advanced features (templates, versioning)
9. Performance optimization