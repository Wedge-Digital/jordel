# Quick Start - Jordel Development

## Contexte en 30 secondes

**Jordel** est un système de gestion de ligue Blood Bowl avec :
- **Java 25** + **Spring Boot 3.5.6**
- **Event Sourcing + CQRS** pour auth et team_building
- **CRUD classique** pour authoring (blog)
- **DDD** avec bounded contexts séparés

## Démarrage en 5 minutes

### 1. Base de données
```bash
docker-compose -f docker_compose.yml up -d
```

### 2. Configuration
Créer `.env.local.properties` avec :
```properties
DB_URL=jdbc:postgresql://localhost:5432/jordel
DB_USER=postgres
DB_PASSWORD=password
DB_DRIVER=org.postgresql.Driver
DB_DIALECT=org.hibernate.dialect.PostgreSQLDialect
ALLOWED_HOSTS=http://localhost:3000,http://localhost:7001
SMTP_USER=your-brevo-user
SMTP_PASSWORD=your-brevo-password
```

### 3. Build et Run
```bash
# Build complet
./gradlew build

# Run l'application (port 7001)
./gradlew bootRun

# Build sans tests (plus rapide)
./gradlew build -x test
```

## Tests

```bash
# Tous les tests
./gradlew test

# Classe spécifique
./gradlew test --tests "com.bloodbowlclub.auth.use_cases.login.LoginCommandTest"

# Pattern matching
./gradlew test --tests "*LoginTest"

# Avec détails
./gradlew test --info
```

## Bounded Contexts - État Actuel

| Context | Status | Architecture | Completion | Prochaines étapes |
|---------|--------|--------------|------------|-------------------|
| **Auth** | ✅ Prod | Event Sourcing | 95% | Token refresh, OTP SMS |
| **Team Building** | 🚧 Dev | Event Sourcing | 80% | CommandHandlers, Controllers, Projectors |
| **Authoring** | ✅ Prod | CRUD | 100% | - |
| **Shared** | ✅ Prod | Service | 100% | - |

## Ajouter une nouvelle commande Event Sourced

### Exemple : Ajouter une action à UserAccount

#### 1. Créer la commande
`auth/domain/user_account/commands/NewActionCommand.java`
```java
public record NewActionCommand(
    Username username,
    String actionData
) implements Command {}
```

#### 2. Créer l'événement
`auth/domain/user_account/events/NewActionEvent.java`
```java
public class NewActionEvent extends UserDomainEvent {
    private String actionData;

    @Override
    public Result<AggregateRoot> applyTo(AggregateRoot aggregate) {
        return aggregate.apply(this);
    }
}
```

#### 3. Ajouter la méthode à l'agrégat
`auth/domain/user_account/ActiveUserAccount.java`
```java
public ResultMap<Void> performNewAction(NewActionCommand cmd) {
    // Valider
    if (!isValid()) {
        return ResultMap.failure(validationErrors());
    }

    // Ajouter événement
    addEvent(new NewActionEvent(cmd.username(), cmd.actionData()));
    return ResultMap.success(null);
}

public Result<AggregateRoot> apply(NewActionEvent event) {
    // Appliquer changement d'état
    this.actionData = event.getActionData();
    return Result.success(this);
}
```

#### 4. Créer le CommandHandler
`auth/use_cases/new_action/NewActionCommandHandler.java`
```java
@Component
public class NewActionCommandHandler extends CommandHandler<NewActionCommand, Void> {

    public NewActionCommandHandler(
        EventStore eventStore,
        Policy policy,
        EventDispatcher dispatcher
    ) {
        super(eventStore, policy, dispatcher);
    }

    @Override
    protected ResultMap<Void> handleCommand(NewActionCommand cmd) {
        // 1. Charger l'agrégat
        Result<ActiveUserAccount> result = eventStore.findUser(cmd.username());
        if (result.isFailure()) {
            return ResultMap.failure("user", "User not found", ErrorCode.NOT_FOUND);
        }

        // 2. Exécuter la commande
        ActiveUserAccount user = result.getValue();
        ResultMap<Void> actionResult = user.performNewAction(cmd);
        if (actionResult.isFailure()) {
            return actionResult;
        }

        // 3. Sauvegarder et dispatcher
        eventStore.saveAll(user.domainEvents());
        dispatcher.asyncDispatchList(user.domainEvents());

        return ResultMap.success(null);
    }
}
```

#### 5. Mettre à jour le Projector
`auth/use_cases/projectors/UserAccountProjector.java`
```java
@Component
public class UserAccountProjector extends Projector<UserDomainEvent> {

    @PostConstruct
    public void initSubscription() {
        dispatcher.subscribe(NewActionEvent.class, this);
        // ... autres événements
    }

    @Override
    protected void receive(UserDomainEvent event) {
        if (event instanceof NewActionEvent e) {
            updateReadCacheForNewAction(e);
        }
        // ... autres événements
    }

    private void updateReadCacheForNewAction(NewActionEvent event) {
        // Mettre à jour le read_cache
        readRepository.updateActionData(event.getUsername(), event.getActionData());
    }
}
```

#### 6. Ajouter le Controller
`auth/io/web/NewActionController.java`
```java
@RestController
@RequestMapping("/auth")
public class NewActionController {

    private final NewActionCommandHandler handler;

    @PostMapping("/new-action")
    public ResponseEntity<?> performNewAction(@RequestBody NewActionRequest request) {
        NewActionCommand cmd = new NewActionCommand(
            new Username(request.username()),
            request.actionData()
        );

        ResultMap<Void> result = handler.handle(cmd);
        return ResultToResponse.toResponse(result);
    }
}
```

#### 7. Tests
```java
// Unit test domaine
@Test
void testPerformNewAction() {
    ActiveUserAccount user = new ActiveUserAccount("john");
    NewActionCommand cmd = new NewActionCommand(new Username("john"), "data");

    ResultMap<Void> result = user.performNewAction(cmd);

    assertTrue(result.isSuccess());
    assertEquals(1, user.domainEvents().size());
    assertTrue(user.domainEvents().get(0) instanceof NewActionEvent);
}

// Unit test CommandHandler
@Test
void testNewActionCommandHandler() {
    FakeEventStore eventStore = new FakeEventStore();
    eventStore.saveUser(new ActiveUserAccount("john"));
    FakeEventDispatcher dispatcher = new FakeEventDispatcher();

    NewActionCommandHandler handler = new NewActionCommandHandler(
        eventStore, new SuccessPolicy(), dispatcher
    );

    NewActionCommand cmd = new NewActionCommand(new Username("john"), "data");
    ResultMap<Void> result = handler.handle(cmd);

    assertTrue(result.isSuccess());
    assertTrue(dispatcher.getDispatchedEvents().contains(NewActionEvent.class));
}
```

## Patterns Essentiels

### Event Sourcing Polymorphique
```java
// L'agrégat change de TYPE Java lors de l'application d'événements
BaseUserAccount base = new BaseUserAccount("john");
ActiveUserAccount active = (ActiveUserAccount) base.apply(emailValidatedEvent);

// Cela force les transitions valides à la compilation
active.login("password");      // ✅ Compile - ActiveUserAccount a cette méthode
base.login("password");         // ❌ Ne compile pas - BaseUserAccount n'a pas cette méthode
```

### Result Monad (pas d'exceptions)
```java
// Single error
Result<User> result = eventStore.findUser("john");
if (result.isFailure()) {
    return Result.failure("User not found", ErrorCode.NOT_FOUND);
}
User user = result.getValue();

// Multiple validation errors
ResultMap<Void> validation = team.hirePlayer(player, messageSource);
if (validation.isFailure()) {
    Map<String, String> errors = validation.errorMap();
    // errors = {"team.budget": "Insufficient budget", "team.player": "..."}
}
```

### Policy Pattern
```java
@Component
public class AggregateShallNotExistPolicy extends Policy {
    public ResultMap<Void> isValid(String aggregateId) {
        if (eventStore.findBySubject(aggregateId).isEmpty()) {
            return ResultMap.success(null);
        }
        return ResultMap.failure("username", "Username already exists", ErrorCode.CONFLICT);
    }
}
```

### Double Dispatch pour Événements
```java
// 1er dispatch : L'événement décide quelle méthode appeler
event.applyTo(aggregate);

// 2ème dispatch : L'agrégat appelle le handler type-spécifique
aggregate.apply(PasswordResetStartedEvent event) {
    return new WaitingPasswordResetUserAccount(...);
}
```

## Architecture Event Sourcing

### Flux de données
```
User Request
    ↓
Controller
    ↓
CommandHandler
    ├─ 1. Load aggregate from EventStore (replay events)
    ├─ 2. Apply policy (business rules)
    ├─ 3. Execute command on aggregate
    ├─ 4. Aggregate produces domain events
    ├─ 5. Save events to event_log
    └─ 6. Dispatch events asynchronously
        ├─ Projectors → update read_cache
        └─ EventHandlers → side effects (email, etc.)
```

### Write Model vs Read Model
```java
// WRITE - Via Event Store (source of truth)
aggregate.execute(command);
eventStore.saveAll(aggregate.domainEvents());
dispatcher.dispatch(events);

// READ - Via Read Cache (fast, denormalized)
UserReadModel user = readRepository.findById(username);
// Pas de replay d'événements, juste une query JSONB rapide
```

## Fichiers Clés à Connaître

### Infrastructure (lib/)
- `lib/domain/AggregateRoot.java` - Base pour tous les agrégats
- `lib/persistance/event_store/EventStore.java` - Save/load événements
- `lib/domain/events/EventDispatcher.java` - Dispatch asynchrone avec virtual threads
- `lib/services/result/Result.java` - Monad pour gestion d'erreurs
- `lib/use_cases/CommandHandler.java` - Base pour command handlers

### Auth (exemple complet Event Sourcing)
- `auth/domain/user_account/UserAccount.java` - Agrégat polymorphique
- `auth/use_cases/login/LoginCommandHandler.java` - Pattern CommandHandler
- `auth/use_cases/projectors/UserAccountProjector.java` - Pattern Projector
- `auth/io/security/JwtRequestFilter.java` - JWT validation filter

### Team Building (exemple en développement)
- `team_building/domain/team/Team.java` - Agrégat avec règles métier complexes
- `team_building/domain/team/TeamTest.java` - Tests très complets (excellent exemple)

### Authoring (exemple CRUD classique)
- `authoring/service/ArticleService.java` - Business logic traditionnelle
- `authoring/web/ArticleController.java` - REST endpoints

### Shared (services communs)
- `shared/ref_data/ReferenceDataService.java` - Cache Blood Bowl rules en RAM

## Pièges à Éviter

### ❌ Ne JAMAIS query l'event_log par autre chose que subject
```java
// ❌ WRONG - Pas d'index, très lent
eventStore.findByType("EmailValidatedEvent");

// ✅ RIGHT - Index sur subject (aggregate ID)
eventStore.findBySubject(username);
```

### ❌ Ne JAMAIS modifier directement le read_cache
```java
// ❌ WRONG - Bypass event sourcing
readRepository.save(updatedUser);

// ✅ RIGHT - Passer par événements
aggregate.performAction();
eventStore.saveAll(aggregate.domainEvents());
dispatcher.dispatch(events); // Projector met à jour le read_cache
```

### ❌ Ne JAMAIS lancer d'exceptions depuis le domaine
```java
// ❌ WRONG
if (budget < cost) {
    throw new InsufficientBudgetException();
}

// ✅ RIGHT - Utiliser Result
if (budget < cost) {
    return ResultMap.failure("team.budget", "Insufficient budget", ErrorCode.BAD_REQUEST);
}
```

### ❌ Ne JAMAIS oublier de valider avant d'ajouter un événement
```java
// ❌ WRONG - Événement invalide peut être persisté
this.addEvent(new PlayerHiredEvent(...));

// ✅ RIGHT - Toujours valider d'abord
if (!this.isValid()) {
    return ResultMap.failure(this.validationErrors());
}
this.addEvent(new PlayerHiredEvent(...));
```

### ❌ Ne JAMAIS utiliser @class complet dans JSON
```json
// ❌ WRONG - Refactoring casse la désérialisation
{"@class": "com.bloodbowlclub.auth.domain.user_account.ActiveUserAccount"}

// ✅ RIGHT - Utiliser alias stable
{"@type": "user.account.active"}
```

## Testing

### Test Infrastructure
- **FakeEventStore** - In-memory event store pour unit tests
- **FakeEventDispatcher** - Capture événements sans dispatch asynchrone
- **TestCase** - Base class avec MessageSource configuré
- **SuccessPolicy / FailurePolicy** - Mock policies

### Structure de test typique
```java
@Test
void testCommandHandler() {
    // Arrange
    FakeEventStore eventStore = new FakeEventStore();
    FakeEventDispatcher dispatcher = new FakeEventDispatcher();
    Policy policy = new SuccessPolicy();

    CommandHandler handler = new SomeCommandHandler(eventStore, policy, dispatcher);
    SomeCommand command = new SomeCommand(...);

    // Act
    ResultMap<Void> result = handler.handle(command);

    // Assert
    assertTrue(result.isSuccess());
    assertEquals(1, eventStore.findAll().size());
    assertTrue(dispatcher.getDispatchedEvents().contains(ExpectedEvent.class));
}
```

## Internationalisation

Messages en français et anglais :
```
src/main/resources/lang/
├── messages_en.properties       # Messages généraux
├── messages_fr.properties
├── error_en.properties          # Messages d'erreur
├── error_fr.properties
├── ValidationMessages_en.properties  # Bean validation
└── ValidationMessages_fr.properties
```

Utilisation :
```java
String message = messageSource.getMessage("error.user.not.found", null, locale);
```

Locale extraite du header `Accept-Language` via `LocaleHeaderFilter`.

## Technologies Clés

- **Java 25** - Preview features enabled
- **Spring Boot 3.5.6** - Framework principal
- **Spring Security** - JWT stateless auth
- **PostgreSQL** - Event store + Read cache (JSONB)
- **Liquibase** - Migrations automatiques
- **CloudEvents** - Spécification événements
- **ULID** - IDs chronologiquement triables
- **Virtual Threads** - Java 21+ pour dispatch asynchrone
- **Bean Validation** - Hibernate Validator
- **MapStruct** - Mapping DTO
- **Lombok** - Réduction boilerplate
- **Brevo** - Service SMTP email

## Prochaines Étapes

### Pour Team Building (80% fait)
1. Implémenter CommandHandlers (RegisterTeamCommandHandler, etc.)
2. Créer REST Controllers
3. Implémenter Projectors pour read_cache
4. Ajouter integration tests

### Pour Auth (95% fait)
1. Finaliser endpoint refresh token
2. Implémenter OTP SMS authentication

## Resources Utiles

- **CLAUDE.md** - Guide complet pour Claude Code
- **BOUNDED_CONTEXTS.md** - État détaillé de chaque module
- **CODEBASE_MAP.md** - Navigation rapide du code
- **Authoring.md** - Spécifications du module blog
- **specs.md** - TODOs et fonctionnalités planifiées