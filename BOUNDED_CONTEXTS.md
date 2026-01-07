# Bounded Contexts - Jordel

Ce document détaille l'état et les responsabilités de chaque bounded context du projet Jordel.

## Vue d'ensemble

| Context | Status | Architecture | Completion | Manque |
|---------|--------|--------------|------------|---------|
| **Auth** | ✅ Production Ready | Event Sourcing + CQRS | 95% | Token refresh endpoint, OTP SMS auth |
| **Team Building** | 🚧 In Progress | Event Sourcing + CQRS | 80% | CommandHandlers, REST Controllers, Projectors, Integration tests |
| **Authoring** | ✅ Production Ready | CRUD (JPA) | 100% | - |
| **Shared** | ✅ Production Ready | Service Layer | 100% | - |
| **Team Management** | 📋 Planned | TBD | 0% | Tout |

---

## Auth - Authentication & User Management

### 📊 Status: ✅ Production Ready (95%)

### 🎯 Responsabilité
Gestion complète de l'authentification utilisateur avec JWT stateless, inscription, validation email, login, et réinitialisation de mot de passe.

### 🏗️ Architecture
**Event Sourcing + CQRS**
- Event Store: `event_log` table (source de vérité)
- Read Cache: `read_cache` table (projections dénormalisées)
- Dispatch asynchrone avec virtual threads

### 📦 Agrégat: UserAccount (Polymorphique)

**Machine à états finie via types Java:**
```
BaseUserAccount
    │
    ├─[EmailValidatedEvent]──────────> ActiveUserAccount
    │                                         │
    │                                         ├─[PasswordResetStartedEvent]──> WaitingPasswordResetUserAccount
    │                                         │                                          │
    │                                         │                                          └─[PasswordResetCompletedEvent]──> ActiveUserAccount
    │                                         │
    │                                         └─[UserLoggedEvent]──────────────────────> ActiveUserAccount (lastLogin updated)
    └─...
```

**États et leurs capacités:**
- `BaseUserAccount` - Après inscription, ne peut PAS login, peut valider email
- `ActiveUserAccount` - Peut login, peut initier reset password, peut changer infos
- `WaitingPasswordResetUserAccount` - Ne peut PAS login, peut compléter reset password

### 🔑 Value Objects
- `Username` - Identifiant unique, 3-20 caractères
- `Email` - Email validé avec Bean Validation
- `Password` - Hash BCrypt, validation force (min 8 chars, majuscule, minuscule, chiffre, spécial)
- `UserRole` - Enum: SIMPLE_USER, ADMIN, SUPER_ADMIN
- `PasswordResetToken` - Token limité dans le temps (15 min)

### 📝 Commands
- `RegisterAccountCommand` - Inscription nouvel utilisateur
- `ValidateEmailCommand` - Validation email via token
- `LoginCommand` - Connexion utilisateur
- `StartResetPasswordCommand` - Demande reset password
- `CompleteResetPasswordCommand` - Complétion reset avec token

### 📢 Events
- `AccountRegisteredEvent` - Compte créé
- `EmailValidatedEvent` - Email confirmé
- `UserLoggedEvent` - Login réussi
- `PasswordResetStartedEvent` - Reset password initié
- `PasswordResetCompletedEvent` - Nouveau password défini

### 🎬 Use Cases (CommandHandlers)
- ✅ `RegisterAccountCommandHandler` - Policy: Username ne doit pas exister
- ✅ `ValidateEmailCommandHandler` - Policy: User doit exister
- ✅ `LoginCommandHandler` - Policy: User doit exister, génère JWT tokens
- ✅ `StartResetPasswordCommandHandler` - Génère token, envoie email
- ✅ `CompleteResetPasswordCommandHandler` - Valide token, change password

### 🔄 Projectors
- ✅ `UserAccountProjector` - Met à jour read_cache avec snapshots d'utilisateurs
  - Écoute tous les UserDomainEvents
  - Update asynchrone via virtual threads

### 🔔 Event Handlers
- ✅ `SendEmailEventHandler` - Envoie emails (validation, reset password)
  - Utilise Brevo SMTP
  - Templates EN/FR dans `email_template/`

### 🌐 Endpoints REST

**Routes publiques (`/auth`):**
- `POST /auth/register` - Inscription
- `POST /auth/validate-email` - Validation email
- `POST /auth/login` - Connexion (retourne access + refresh tokens)
- `POST /auth/start-reset-password` - Demande reset
- `POST /auth/complete-reset-password` - Finalise reset

**Routes authentifiées:**
- `GET /auth/me` - Infos utilisateur courant
- `POST /auth/refresh` - ⚠️ **Partiellement implémenté**

### 🔒 Sécurité
- JWT stateless (access token 15 min, refresh token 30 jours)
- `JwtRequestFilter` - Valide token, charge user depuis read_cache, set Spring Security context
- BCrypt password hashing
- CSRF disabled (API stateless)
- `TraceFilter` - Request tracing

### 📂 Fichiers clés
- **Agrégat:** `auth/domain/user_account/UserAccount.java` (interface), implémentations polymorphiques
- **Events:** `auth/domain/user_account/events/*.java`
- **Handlers:** `auth/use_cases/{register,login,validate_email,start_reset_password,complete_reset_password}/`
- **Projector:** `auth/use_cases/projectors/UserAccountProjector.java`
- **Security:** `auth/io/security/{SecurityConfig,JwtRequestFilter}.java`
- **JWT:** `auth/io/services/JWTService.java`

### ✅ Complet
- Inscription avec envoi email validation
- Validation email
- Login avec génération JWT
- Password reset flow complet
- Projectors et read cache
- Event handlers pour emails
- Security configuration
- Integration tests

### ❌ Manque (5%)
- **Token refresh endpoint** - Partiellement implémenté, doit être finalisé
- **OTP SMS authentication** - Planifié (specs.md)
- **Token rotation** - Planifié

### 🧪 Tests
- ✅ Unit tests domaine (`UserAccountTest.java`)
- ✅ Unit tests CommandHandlers avec `FakeEventStore`
- ✅ Integration tests endpoints
- ✅ Event hydration tests
- **Coverage:** ~85%

---

## Team Building - Team Creation & Configuration

### 📊 Status: 🚧 In Progress (80%)

### 🎯 Responsabilité
Création et configuration d'équipes Blood Bowl selon les règles officielles, avec gestion de budget, recrutement de joueurs, achat de staff et rerolls.

### 🏗️ Architecture
**Event Sourcing + CQRS**
- Event Store: `event_log` table
- Read Cache: ⚠️ **Projectors pas encore implémentés**
- Dispatch asynchrone (infrastructure prête)

### 📦 Agrégat: Team (Polymorphique)

**Machine à états finie:**
```
BaseTeam
    │
    ├─[DraftTeamRegisteredEvent]──> DraftTeam
                                        │
                                        ├─[RulesetSelectedEvent]──────────────> RulesetSelectedTeam
                                                                                    │
                                                                                    ├─[RosterChosenEvent]──────> RosterSelectedTeam
                                                                                                                    │
                                                                                                                    ├─[PlayerHiredEvent]
                                                                                                                    ├─[PlayerRemovedEvent]
                                                                                                                    ├─[TeamStaffPurchasedEvent]
                                                                                                                    ├─[TeamStaffRemovedEvent]
                                                                                                                    ├─[TeamRerollPurchasedEvent]
                                                                                                                    ├─[TeamRerollRemovedEvent]
                                                                                                                    └─[RosterChangedEvent]
```

**États:**
- `BaseTeam` - État initial vide
- `DraftTeam` - Équipe enregistrée avec coach et nom
- `RulesetSelectedTeam` - Ruleset choisi (budget, limites)
- `RosterSelectedTeam` - Roster choisi (Humans, Orcs, etc.), peut recruter joueurs/staff

### 🔑 Entités et Value Objects

**Entités:**
- `Roster` - Définition roster Blood Bowl (Humans, Orcs, etc.)
- `PlayerDefinition` - Type de joueur avec stats, skills, coût
- `TeamStaff` - Polymorphique:
  - `Apothecary` (50k)
  - `AssistantCoaches` (10k/unit, max 6)
  - `Cheerleaders` (10k/unit, max 12)
  - `DedicatedFans` (10k/unit, max 6)

**Value Objects:**
- `TeamID` - ULID unique
- `TeamName` - Nom d'équipe
- `CoachName` - Nom du coach
- `Budget` - Budget disponible avec calculs
- `Reroll` - Reroll d'équipe (coût variable selon roster, max 8)

### 📝 Commands
- `RegisterDraftTeamCommand` - Créer équipe draft
- `SelectRulesetCommand` - Choisir règles de création
- `ChooseRosterCommand` - Sélectionner roster
- `HirePlayerCommand` - Recruter joueur
- `RemovePlayerCommand` - Retirer joueur
- `PurchaseTeamStaffCommand` - Acheter staff
- `RemoveTeamStaffCommand` - Retirer staff
- `PurchaseTeamRerollCommand` - Acheter reroll
- `RemoveTeamRerollCommand` - Retirer reroll
- `ChangeRosterCommand` - Changer de roster (réinitialise équipe)

### 📢 Events
- `DraftTeamRegisteredEvent` - Équipe créée
- `RulesetSelectedEvent` - Règles choisies
- `RosterChosenEvent` - Roster sélectionné
- `PlayerHiredEvent` - Joueur recruté
- `PlayerRemovedEvent` - Joueur retiré
- `TeamStaffPurchasedEvent` - Staff acheté
- `TeamStaffRemovedEvent` - Staff retiré
- `TeamRerollPurchasedEvent` - Reroll acheté
- `TeamRerollRemovedEvent` - Reroll retiré
- `RosterChangedEvent` - Roster changé

### 🎯 Règles Métier (Validation Complexe)

**Budget:**
- Création budget défini par ruleset (ex: 1,150,000 pour BB2020)
- Budget décrémenté par recrutements/achats
- Validation: coût ≤ budget disponible

**Limites joueurs:**
- Max 16 joueurs par équipe
- Max par type de joueur (ex: max 4 Blitzers)
- **Cross-limits** (ex: max 4 Blitzers + Catchers combinés)
- Validation complexe avec `PlayerLimitValidator`

**Staff:**
- `Apothecary` - Max 1
- `AssistantCoaches` - Max 6
- `Cheerleaders` - Max 12
- `DedicatedFans` - Max 6

**Rerolls:**
- Max 8 rerolls par équipe
- Coût variable selon roster

### 🎬 Use Cases
- ❌ **CommandHandlers non implémentés**
  - `RegisterDraftTeamCommandHandler`
  - `SelectRulesetCommandHandler`
  - `ChooseRosterCommandHandler`
  - `HirePlayerCommandHandler`
  - etc.

### 🔄 Projectors
- ❌ **Non implémenté** - `TeamProjector` à créer pour read_cache

### 🌐 Endpoints REST
- ❌ **Aucun controller implémenté**

### 📂 Fichiers clés
- **Agrégat:** `team_building/domain/team/Team.java` (interface)
- **Implémentations:** `BaseTeam`, `DraftTeam`, `RulesetSelectedTeam`, `RosterSelectedTeam`
- **Events:** `team_building/domain/team/events/*.java`
- **Commands:** `team_building/domain/team/commands/*.java`
- **Validators:** `team_building/domain/team/validators/PlayerLimitValidator.java`
- **Tests:** `team_building/domain/team/TeamTest.java` ⭐ **Très complet, excellent exemple**

### ✅ Complet (80%)
- ✅ Agrégat Team polymorphique complet
- ✅ Tous les événements définis et implémentés
- ✅ Toutes les commandes définies
- ✅ Validation métier complexe (budget, limites, cross-limits)
- ✅ Event hydration (reconstruction depuis événements)
- ✅ Tests domaine très complets (~20 tests)
- ✅ Test d'hydratation événements

### ❌ Manque (20%)
- ❌ **CommandHandlers** - Aucun handler implémenté
- ❌ **REST Controllers** - Aucun endpoint exposé
- ❌ **Projectors** - Read cache pas mis à jour
- ❌ **Policies** - Validation au niveau use case
- ❌ **Integration tests** - Tests HTTP endpoints
- ❌ **Event handlers** - Side effects (notifications, etc.)

### 🚀 Prochaines étapes (Par ordre de priorité)
1. **Implémenter CommandHandlers** - Pattern identique à Auth
2. **Créer TeamProjector** - Update read_cache
3. **Créer REST Controllers** - Exposer endpoints HTTP
4. **Ajouter Policies** - Validation règles métier
5. **Integration tests** - Tests endpoints complets

### 🧪 Tests
- ✅ Unit tests domaine (`TeamTest.java`) - **Très complet, 20+ tests**
- ✅ Event hydration tests
- ❌ Unit tests CommandHandlers
- ❌ Integration tests endpoints
- **Coverage domaine:** ~90%
- **Coverage global:** ~40% (handlers/controllers manquants)

---

## Authoring - Blog & Content Management

### 📊 Status: ✅ Production Ready (100%)

### 🎯 Responsabilité
Système de blog simple avec articles, paragraphes et commentaires. Permet création, modification, suppression d'articles et ajout de commentaires.

### 🏗️ Architecture
**CRUD Traditionnel (JPA)**
- Pas d'Event Sourcing (simplicité volontaire)
- Accès direct à la base via JPA repositories
- Pattern: Controller → Service → Repository

**Pourquoi CRUD et pas Event Sourcing?**
- Domaine simple sans besoin d'audit complet
- Pas de règles métier complexes
- Performance lecture/écriture équivalente
- Moins de complexité pour un blog basique

### 📦 Entités JPA

**ArticleEntity:**
- `id` (ULID)
- `title` (255 chars max)
- `description` (200 chars)
- `authorName` (coach name)
- `authorProfilePicture` (URL)
- `imageUrl` (Cloudinary)
- `createdAt` (auto)
- `leadParagraph` (10,000 chars max)
- `paragraphs` (OneToMany)
- `comments` (OneToMany)

**ParagraphEntity:**
- `id` (ULID)
- `title`
- `content` (10,000 chars max, Markdown)
- `article` (ManyToOne)

**CommentEntity:**
- `id` (ULID)
- `authorName`
- `authorProfilePicture`
- `content` (1,000 chars max)
- `createdAt` (auto)
- `article` (ManyToOne)

### 🌐 Endpoints REST

**Articles:**
- `GET /authoring/article?page={page}` - Liste paginée (20/page, ordre chronologique inverse)
  - Retourne: `{ articles: [...], totalCount: N, currentPage: M }`
- `GET /authoring/article/{id}` - Détail article avec commentaires
- `POST /authoring/article` - Créer article
- `PUT /authoring/article/{id}` - Modifier article
- `DELETE /authoring/article/{id}` - Supprimer article

**Commentaires:**
- `POST /authoring/article/{id}/comment` - Ajouter commentaire
- `DELETE /authoring/article/{id}/comment/{commentId}` - Supprimer commentaire

### 📂 Fichiers clés
- **Entities:** `authoring/entity/{ArticleEntity,ParagraphEntity,CommentEntity}.java`
- **Service:** `authoring/service/ArticleService.java`
- **Controller:** `authoring/web/ArticleController.java`
- **Repositories:** `authoring/repository/{ArticleRepository,CommentRepository}.java`

### ✅ Complet (100%)
- ✅ CRUD complet articles
- ✅ Gestion paragraphes
- ✅ Système commentaires
- ✅ Pagination
- ✅ DTOs avec MapStruct
- ✅ Validation Bean Validation
- ✅ Integration tests

### 🧪 Tests
- ✅ Unit tests service
- ✅ Integration tests endpoints
- **Coverage:** ~75%

### 📝 Notes
- Contenu supporte **Markdown** et **emojis**
- Images hébergées sur **Cloudinary**
- Pas de système de modération commentaires (pour l'instant)
- Pas de système de catégories/tags (pour l'instant)

---

## Shared - Services & Data Partagés

### 📊 Status: ✅ Production Ready (100%)

### 🎯 Responsabilité
Services et données partagés entre bounded contexts, notamment les règles Blood Bowl (rosters, joueurs, skills, règles spéciales).

### 🏗️ Architecture
**Service Layer avec Cache In-Memory**
- Chargement JSON au démarrage (`@PostConstruct`)
- Cache immuable thread-safe
- Pas de base de données pour reference data

### 🎮 ReferenceDataService

**Responsabilité:**
Charger et fournir accès rapide aux règles Blood Bowl officielles.

**Données chargées:**
- **24 Rosters** (Humans, Orcs, Elves, etc.)
- **300+ PlayerDefinitions** (stats, skills, coûts)
- **80+ Skills** (Block, Dodge, Sure Hands, etc.)
- **50+ Special Rules** (Stunty, Titchy, etc.)

**Source:** Fichiers JSON dans `src/main/resources/reference/`
```
reference/
├── rosters/           # Humans.json, Orcs.json, ...
├── players/           # Par roster
├── skills/
└── special_rules/
```

**Configuration:**
- `RefDataServiceConfig.java` ⚠️ **Non tracké** - À ajouter au repo
- Charge les JSONs au startup
- Maps immuables exposées via getters

**API:**
```java
@Service
public class ReferenceDataService {
    public Optional<Roster> findRosterById(String id);
    public List<Roster> findAllRosters();
    public Optional<PlayerDefinition> findPlayerById(String id);
    public List<PlayerDefinition> findPlayersByRoster(String rosterId);
    // etc.
}
```

### 🔑 Value Objects Partagés
- `TeamID` - ULID d'équipe (partagé entre Team Building et futurs contextes)
- `CoachName` - Nom coach (partagé Auth et Team Building)
- Autres value objects cross-context

### 📂 Fichiers clés
- **Service:** `shared/ref_data/ReferenceDataService.java`
- **Config:** `shared/ref_data/RefDataServiceConfig.java` ⚠️ **À tracker**
- **Models:** `shared/ref_data/model/{Roster,PlayerDefinition,Skill,SpecialRule}.java`
- **Repos:** `shared/ref_data/repositories/` (in-memory)
- **JSON Data:** `src/main/resources/reference/`

### ✅ Complet (100%)
- ✅ Chargement JSON au startup
- ✅ Cache in-memory thread-safe
- ✅ API query pour tous types de données
- ✅ Tests unitaires
- ✅ 24 rosters complets avec tous les joueurs
- ✅ Skills et special rules

### 🧪 Tests
- ✅ Tests chargement JSON
- ✅ Tests query API
- **Coverage:** ~80%

### 📝 Notes
- **~100KB de données JSON** chargées en RAM
- **Immuable** - Pas de modification runtime
- **Thread-safe** - Collections immuables
- Mise à jour des règles = Modifier JSON + redéployer

---

## Team Management - Gestion d'Équipes en Ligue

### 📊 Status: 📋 Planned (0%)

### 🎯 Responsabilité
Gestion des équipes pendant le jeu (matchs, blessures, montées de niveau, achats/ventes).

### 🏗️ Architecture
**Event Sourcing + CQRS** (prévu)

### 📦 Agrégats prévus
- `PlayingTeam` - Équipe active en ligue
- `Match` - Match entre deux équipes
- `Player` - Joueur avec progression

### 📝 Events prévus
- Blessures joueurs
- Montées de niveau
- Achats/ventes joueurs
- Matchs joués
- SPP gagnés

### ❌ Status
**Aucun code implémenté** - Répertoire vide

---

## Résumé des Priorités

### 🔥 Haute Priorité
1. **Team Building** - Finaliser les 20% manquants (CommandHandlers, Controllers, Projectors)
2. **Auth** - Token refresh endpoint

### 🔧 Moyenne Priorité
3. **Auth** - OTP SMS authentication
4. **Shared** - Tracker `RefDataServiceConfig.java`

### 📋 Basse Priorité
5. **Team Management** - Démarrer la conception
6. **Authoring** - Système de modération commentaires
7. **Authoring** - Catégories/tags

---

## Dépendances Entre Contextes

```
Auth ──────────> Shared (value objects: CoachName)
    │
    └──────────> lib (infrastructure)

Team Building ─> Shared (ReferenceDataService, TeamID, CoachName)
    │
    └──────────> lib (infrastructure)

Authoring ─────> lib (infrastructure uniquement)

Shared ────────> lib (infrastructure)

Team Management (futur)
    ├──────────> Team Building (lecture équipes créées)
    ├──────────> Shared (reference data)
    └──────────> lib (infrastructure)
```

**Règles:**
- ✅ Bounded contexts → lib (OK)
- ✅ Bounded contexts → shared (OK)
- ❌ Bounded contexts → autres bounded contexts (éviter autant que possible)
- ❌ lib → bounded contexts (INTERDIT)
- ❌ shared → bounded contexts spécifiques (INTERDIT)
