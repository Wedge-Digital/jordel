# Codebase Map - Navigation Rapide

Guide de navigation pour trouver rapidement les fichiers clés du projet Jordel.

---

## 📁 Structure des Packages

```
src/main/java/com/bloodbowlclub/
├── lib/                              # Infrastructure partagée
│   ├── domain/
│   │   ├── AggregateRoot.java       # ⭐ Base pour tous les agrégats
│   │   ├── ValueObject.java         # ⭐ Base pour value objects
│   │   ├── DomainEvent.java         # ⭐ Base pour événements
│   │   ├── EntityID.java            # Base pour IDs
│   │   └── events/
│   │       ├── EventDispatcher.java  # ⭐ Dispatch asynchrone (virtual threads)
│   │       ├── EventHandler.java     # Base pour side effects
│   │       └── Projector.java        # Base pour projections read cache
│   ├── persistance/
│   │   ├── event_store/
│   │   │   ├── EventStore.java      # ⭐ Save/load événements
│   │   │   ├── EventEntity.java     # Entité JPA pour event_log
│   │   │   └── EventEntityFactory.java
│   │   └── read_cache/
│   │       ├── ReadRepository.java  # ⭐ Queries sur read_cache JSONB
│   │       └── ReadCacheEntity.java
│   ├── services/
│   │   └── result/
│   │       ├── Result.java          # ⭐ Monad erreur unique
│   │       ├── ResultMap.java       # ⭐ Monad erreurs multiples
│   │       └── ErrorCode.java       # Enum codes erreur HTTP
│   ├── use_cases/
│   │   ├── CommandHandler.java      # ⭐ Base pour command handlers
│   │   ├── Policy.java              # ⭐ Base pour business rules
│   │   └── UserCommand.java         # Interface commands
│   ├── validators/
│   │   └── ULIDConstraint.java      # Custom Bean Validation
│   └── util/
│       └── ULID.java                # Helper génération ULID
│
├── auth/                             # ✅ Bounded Context - Auth
│   ├── domain/
│   │   └── user_account/
│   │       ├── UserAccount.java     # Interface agrégat polymorphique
│   │       ├── BaseUserAccount.java # État initial
│   │       ├── ActiveUserAccount.java  # ⭐ État actif (peut login)
│   │       ├── WaitingPasswordResetUserAccount.java
│   │       ├── values/
│   │       │   ├── Username.java    # ⭐ Value object identifiant
│   │       │   ├── Email.java
│   │       │   ├── Password.java    # ⭐ BCrypt hash + validation
│   │       │   ├── UserRole.java
│   │       │   └── PasswordResetToken.java
│   │       ├── commands/
│   │       │   ├── RegisterAccountCommand.java
│   │       │   ├── LoginCommand.java
│   │       │   ├── ValidateEmailCommand.java
│   │       │   ├── StartResetPasswordCommand.java
│   │       │   └── CompleteResetPasswordCommand.java
│   │       └── events/
│   │           ├── UserDomainEvent.java  # Base
│   │           ├── AccountRegisteredEvent.java
│   │           ├── EmailValidatedEvent.java
│   │           ├── UserLoggedEvent.java
│   │           ├── PasswordResetStartedEvent.java
│   │           └── PasswordResetCompletedEvent.java
│   ├── use_cases/
│   │   ├── register/
│   │   │   ├── RegisterAccountCommandHandler.java  # ⭐ Pattern handler
│   │   │   └── AgregateShallNotExistPolicy.java
│   │   ├── login/
│   │   │   ├── LoginCommandHandler.java
│   │   │   └── AgregateShallExistPolicy.java
│   │   ├── validate_email/
│   │   │   └── ValidateEmailCommandHandler.java
│   │   ├── start_reset_password/
│   │   │   └── StartResetPasswordCommandHandler.java
│   │   ├── complete_reset_password/
│   │   │   └── CompleteResetPasswordCommandHandler.java
│   │   ├── event_handlers/
│   │   │   └── SendEmailEventHandler.java  # ⭐ Side effect emails
│   │   └── projectors/
│   │       └── UserAccountProjector.java   # ⭐ Update read_cache
│   ├── io/
│   │   ├── web/
│   │   │   ├── RegisterController.java
│   │   │   ├── LoginController.java
│   │   │   ├── ValidateEmailController.java
│   │   │   ├── ResetPasswordController.java
│   │   │   └── routes/
│   │   │       ├── AuthOpenRoutes.java     # Routes publiques
│   │   │       └── AuthAuthenticatedRoutes.java
│   │   ├── security/
│   │   │   ├── SecurityConfig.java         # ⭐ Spring Security config
│   │   │   ├── JwtRequestFilter.java       # ⭐ JWT validation filter
│   │   │   └── TraceFilter.java
│   │   └── services/
│   │       ├── JWTService.java             # ⭐ Generate/validate JWT
│   │       ├── AuthService.java
│   │       └── EmailService.java
│   └── persistance/
│       └── UserAccountRepository.java      # JPA read_cache queries
│
├── team_building/                    # 🚧 Bounded Context - Teams (80%)
│   ├── domain/
│   │   └── team/
│   │       ├── Team.java            # Interface agrégat polymorphique
│   │       ├── BaseTeam.java        # État initial vide
│   │       ├── DraftTeam.java       # Équipe draft
│   │       ├── RulesetSelectedTeam.java
│   │       ├── RosterSelectedTeam.java  # ⭐ État principal (recrute joueurs)
│   │       ├── values/
│   │       │   ├── TeamName.java
│   │       │   ├── Budget.java
│   │       │   ├── Reroll.java
│   │       │   └── staff/
│   │       │       ├── TeamStaff.java      # Interface polymorphique
│   │       │       ├── Apothecary.java
│   │       │       ├── AssistantCoaches.java
│   │       │       ├── Cheerleaders.java
│   │       │       └── DedicatedFans.java
│   │       ├── commands/
│   │       │   ├── RegisterDraftTeamCommand.java
│   │       │   ├── SelectRulesetCommand.java
│   │       │   ├── ChooseRosterCommand.java
│   │       │   ├── HirePlayerCommand.java
│   │       │   ├── RemovePlayerCommand.java
│   │       │   ├── PurchaseTeamStaffCommand.java
│   │       │   └── PurchaseTeamRerollCommand.java
│   │       ├── events/
│   │       │   ├── TeamDomainEvent.java    # Base
│   │       │   ├── DraftTeamRegisteredEvent.java
│   │       │   ├── RulesetSelectedEvent.java
│   │       │   ├── RosterChosenEvent.java
│   │       │   ├── PlayerHiredEvent.java
│   │       │   ├── PlayerRemovedEvent.java
│   │       │   ├── TeamStaffPurchasedEvent.java
│   │       │   └── TeamRerollPurchasedEvent.java
│   │       └── validators/
│   │           └── PlayerLimitValidator.java  # ⭐ Validation limites complexes
│   └── use_cases/
│       └── [TODO: CommandHandlers à implémenter]
│
├── authoring/                        # ✅ Bounded Context - Blog (CRUD)
│   ├── entity/
│   │   ├── ArticleEntity.java       # JPA entity article
│   │   ├── ParagraphEntity.java     # JPA entity paragraphe
│   │   └── CommentEntity.java       # JPA entity commentaire
│   ├── service/
│   │   └── ArticleService.java      # Business logic
│   ├── web/
│   │   └── ArticleController.java   # REST endpoints
│   └── repository/
│       ├── ArticleRepository.java   # Spring Data JPA
│       └── CommentRepository.java
│
└── shared/                           # ✅ Services partagés
    ├── ref_data/
    │   ├── ReferenceDataService.java  # ⭐ Cache Blood Bowl rules
    │   ├── RefDataServiceConfig.java  # ⚠️ Non tracké
    │   ├── model/
    │   │   ├── Roster.java          # Roster Blood Bowl (Humans, etc.)
    │   │   ├── PlayerDefinition.java # Type joueur avec stats
    │   │   ├── Skill.java           # Compétence (Block, Dodge, etc.)
    │   │   └── SpecialRule.java     # Règle spéciale (Stunty, etc.)
    │   └── repositories/
    │       └── [In-memory repositories]
    └── values/                       # Value objects cross-context
        ├── TeamID.java              # ULID équipe
        └── CoachName.java           # Nom coach
```

---

## 🎯 Fichiers par Tâche Commune

### Ajouter un endpoint authentifié

1. **`auth/io/web/routes/AuthAuthenticatedRoutes.java`** - Ajouter route
2. **`auth/io/security/SecurityConfig.java`** - Configurer sécurité
3. Créer controller dans `auth/io/web/`

### Ajouter une règle Blood Bowl

1. Modifier JSON dans **`src/main/resources/reference/`**
2. Adapter model dans **`shared/ref_data/model/`**
3. **`ReferenceDataService`** charge automatiquement au startup

### Debugging Event Sourcing

1. **`EventStore.findBySubject(aggregateId)`** - Voir tous les événements
2. **`ReadRepository.findById(aggregateId)`** - Voir snapshot actuel
3. Comparer pour identifier désynchronisation

### Migration de schéma base de données

1. Créer changeset dans **`src/main/resources/liquibase/`**
2. Taguer avec contexte: `local`, `test`, ou `prod`
3. Redémarrer app → Liquibase applique automatiquement

### Ajouter une nouvelle commande Event Sourced

**Exemple pour Auth:**
1. Command: `auth/domain/user_account/commands/NewActionCommand.java`
2. Event: `auth/domain/user_account/events/NewActionEvent.java`
3. Méthode agrégat: `ActiveUserAccount.performNewAction()` + `.apply(NewActionEvent)`
4. Handler: `auth/use_cases/new_action/NewActionCommandHandler.java`
5. Projector: Mettre à jour `UserAccountProjector.java`
6. Controller: `auth/io/web/NewActionController.java`

### Créer un nouvel agrégat Event Sourced

**Pattern à suivre (voir Auth ou Team Building):**
1. Interface agrégat extends `AggregateRoot`
2. États polymorphiques (BaseXXX, ActiveXXX, etc.)
3. Value objects dans `values/`
4. Commands dans `commands/`
5. Events dans `events/` extends `DomainEvent`
6. CommandHandlers dans `use_cases/`
7. Projector pour read_cache
8. Event handlers pour side effects

---

## 🧪 Tests par Module

### Auth
- **`auth/domain/user_account/UserAccountTest.java`** - Tests domaine ⭐ Excellent exemple
- **`auth/use_cases/*/CommandHandlerTest.java`** - Tests use cases
- **`auth/io/web/*ControllerIntegrationTest.java`** - Tests endpoints HTTP

### Team Building
- **`team_building/domain/team/TeamTest.java`** - ⭐⭐⭐ **Très complet, meilleur exemple**
- **`team_building/domain/team/events/TeamEventTest.java`** - Tests hydratation événements

### Lib (infrastructure)
- **`lib/persistance/event_store/EventStoreTest.java`**
- **`lib/services/result/ResultTest.java`**
- **`lib/domain/events/EventDispatcherTest.java`**

### Authoring
- **`authoring/service/ArticleServiceTest.java`**
- **`authoring/web/ArticleControllerIntegrationTest.java`**

---

## 📦 Ressources Statiques

### Reference Data (Blood Bowl rules)
```
src/main/resources/reference/
├── rosters/              # 24 fichiers JSON
│   ├── Humans.json
│   ├── Orcs.json
│   ├── WoodElves.json
│   └── ...
├── players/              # Définitions joueurs par roster
├── skills/               # Compétences Blood Bowl
└── special_rules/        # Règles spéciales
```

### Internationalisation (i18n)
```
src/main/resources/lang/
├── messages_en.properties              # Messages généraux EN
├── messages_fr.properties              # Messages généraux FR
├── error_en.properties                 # Erreurs EN
├── error_fr.properties                 # Erreurs FR
├── ValidationMessages_en.properties    # Bean Validation EN
└── ValidationMessages_fr.properties    # Bean Validation FR
```

**Utilisation:**
```java
String msg = messageSource.getMessage("error.user.not.found", null, locale);
```

### Email Templates
```
src/main/resources/email_template/
├── email_validation_en.html
├── email_validation_fr.html
├── password_reset_en.html
└── password_reset_fr.html
```

### Migrations Base de Données
```
src/main/resources/liquibase/
├── changelog-master.xml
├── changesets/
│   ├── 001-create-event-log.xml
│   ├── 002-create-read-cache.xml
│   └── ...
└── data/
    └── initial-data.sql
```

**Contextes Liquibase:**
- `local` - Développement local
- `test` - Tests automatisés
- `prod` - Production

---

## ⚙️ Configuration

### Profiles Spring
Variable d'environnement: **`EXEC_PROFILE`**
- `local` - Développement local
- `test` - Tests automatisés
- `prod` - Production

### Variables d'environnement (`.env.{profile}.properties`)

**Requis:**
```properties
# Database
DB_URL=jdbc:postgresql://localhost:5432/jordel
DB_USER=postgres
DB_PASSWORD=password
DB_DRIVER=org.postgresql.Driver
DB_DIALECT=org.hibernate.dialect.PostgreSQLDialect

# CORS
ALLOWED_HOSTS=http://localhost:3000,http://localhost:7001

# Email (Brevo)
SMTP_USER=your-brevo-user
SMTP_PASSWORD=your-brevo-password
```

### Fichiers de configuration
- **`application.properties`** - Config Spring Boot principale
- **`application-{profile}.properties`** - Config par environnement

---

## 🚀 Points d'Entrée

### WebApplication.java (actif)
**`src/main/java/com/bloodbowlclub/WebApplication.java`**

Responsabilités:
- Lance Spring Boot web server (port 7001)
- Active Spring Security avec JWT filters
- Démarre EventDispatcher
- Charge ReferenceDataService
- Expose REST API

### ShellApplication.java (inactif)
**`src/main/java/com/bloodbowlclub/ShellApplication.java`**

État: Non implémenté
Prévu pour: CLI interface avec Spring Shell

---

## 📚 Dépendances Clés (build.gradle)

```gradle
dependencies {
    // Spring
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'

    // Event Sourcing
    implementation 'io.cloudevents:cloudevents-json-jackson:2.5.0'
    implementation 'com.github.guepardoapps:kulid:2.0.0.0'  // ULID

    // Security
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'

    // Database
    implementation 'org.postgresql:postgresql'
    implementation 'org.liquibase:liquibase-core'

    // Utils
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    implementation 'org.mapstruct:mapstruct:1.5.5.Final'
    annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.5.Final'

    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
}
```

---

## 🔍 Recherche Rapide par Concept

### Event Sourcing
- **Agrégat base:** `lib/domain/AggregateRoot.java`
- **Event base:** `lib/domain/DomainEvent.java`
- **Event store:** `lib/persistance/event_store/EventStore.java`
- **Dispatcher:** `lib/domain/events/EventDispatcher.java`
- **Exemple complet:** `auth/domain/user_account/`

### CQRS
- **Write (Commands):** `auth/use_cases/*/CommandHandler.java`
- **Read (Queries):** `lib/persistance/read_cache/ReadRepository.java`
- **Projectors:** `auth/use_cases/projectors/UserAccountProjector.java`

### Polymorphisme
- **Agrégats:** `auth/domain/user_account/` (3 types: Base, Active, WaitingReset)
- **Staff:** `team_building/domain/team/values/staff/` (4 types)
- **Events:** Double dispatch avec `applyTo()` et `apply()`

### Validation
- **Bean Validation:** Annotations `@NotNull`, `@Size`, etc.
- **Custom validators:** `lib/validators/`
- **Domain validation:** `AggregateRoot.isValid()` et `validationErrors()`
- **Policies:** `auth/use_cases/*/Policy.java`

### Sécurité
- **JWT:** `auth/io/services/JWTService.java`
- **Filter:** `auth/io/security/JwtRequestFilter.java`
- **Config:** `auth/io/security/SecurityConfig.java`
- **Password:** `auth/domain/user_account/values/Password.java` (BCrypt)

### Erreurs
- **Result monad:** `lib/services/result/Result.java`
- **ResultMap:** `lib/services/result/ResultMap.java`
- **Error codes:** `lib/services/result/ErrorCode.java`
- **HTTP mapping:** Controllers avec `ResultToResponse`

---

## 💡 Tips de Navigation

### Trouver un agrégat
1. Aller dans `{context}/domain/{aggregate_name}/`
2. Chercher l'interface (ex: `UserAccount.java`, `Team.java`)
3. Les implémentations sont à côté (ex: `ActiveUserAccount.java`)

### Trouver un endpoint
1. Aller dans `{context}/io/web/`
2. Les controllers sont nommés par action (ex: `LoginController.java`)
3. Les routes sont définies dans `routes/`

### Trouver un événement
1. Aller dans `{context}/domain/{aggregate}/events/`
2. Les événements sont nommés au passé (ex: `AccountRegisteredEvent.java`)

### Trouver un test
Les tests mirrorent la structure du code:
- `src/test/java/com/bloodbowlclub/auth/domain/` → tests domaine Auth
- `src/test/java/com/bloodbowlclub/lib/` → tests infrastructure

### Ajouter un message i18n
1. Ouvrir `src/main/resources/lang/error_fr.properties` (ou `error_en.properties`)
2. Ajouter clé: `error.my.new.error=Message en français`
3. Utiliser: `messageSource.getMessage("error.my.new.error", null, locale)`

---

## 🎓 Fichiers Exemples à Étudier

**Pour comprendre Event Sourcing:**
1. **`auth/domain/user_account/ActiveUserAccount.java`** - Agrégat polymorphique
2. **`auth/use_cases/login/LoginCommandHandler.java`** - Pattern handler
3. **`auth/use_cases/projectors/UserAccountProjector.java`** - Pattern projector

**Pour comprendre la validation complexe:**
1. **`team_building/domain/team/RosterSelectedTeam.java`** - Validation budget/limites
2. **`team_building/domain/team/validators/PlayerLimitValidator.java`** - Cross-limits

**Pour les tests:**
1. **`team_building/domain/team/TeamTest.java`** - ⭐ Meilleur exemple de tests complets
2. **`auth/domain/user_account/UserAccountTest.java`** - Tests agrégat polymorphique
3. **`auth/use_cases/login/LoginCommandHandlerTest.java`** - Tests avec FakeEventStore

**Pour CRUD simple:**
1. **`authoring/service/ArticleService.java`** - Business logic JPA
2. **`authoring/web/ArticleController.java`** - REST endpoints

---

## 📖 Documentation Complémentaire

- **QUICK_START.md** - Démarrage rapide en 5 minutes
- **CLAUDE.md** - Guide complet pour Claude Code
- **BOUNDED_CONTEXTS.md** - État détaillé de chaque module
- **Authoring.md** - Spécifications du module blog
- **specs.md** - TODOs et fonctionnalités planifiées
