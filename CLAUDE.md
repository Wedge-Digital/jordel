# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Jordel is a Blood Bowl league management system built with Java 25 and Spring Boot 3.5.6. The application implements Domain-Driven Design (DDD), Event Sourcing, and CQRS patterns with a focus on clean architecture and maintainability.

## Build, Test, and Run Commands

### Build and Run
```bash
# Build the project
./gradlew build

# Run the web application (default port 7001)
./gradlew bootRun

# Clean and rebuild
./gradlew clean build

# Build without tests
./gradlew build -x test
```

### Testing
```bash
# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "com.bloodbowlclub.auth.use_cases.login.LoginCommandTest"

# Run tests with pattern matching
./gradlew test --tests "*LoginTest"

# Run tests with verbose output
./gradlew test --info
```

### Database
```bash
# Start PostgreSQL via Docker
docker-compose -f docker_compose.yml up -d

# The application uses Liquibase for migrations (runs automatically on startup)
# Contexts: local, test, prod (set via EXEC_PROFILE environment variable)
```

### Environment Configuration
The application requires environment variables defined in `.env.{EXEC_PROFILE}.properties`:
- `.env.local.properties` - Local development
- `.env.test.properties` - Testing

Required variables: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `DB_DRIVER`, `DB_DIALECT`, `ALLOWED_HOSTS`, `SMTP_USER`, `SMTP_PASSWORD`

## Architecture Overview

### Core Pattern: Event Sourcing + CQRS

The application uses **event sourcing** as its persistence mechanism combined with **CQRS** for read optimization:

**Write Model (Event Store):**
- All state changes are captured as immutable domain events in the `event_log` table
- Aggregates can be fully reconstructed by replaying events in chronological order
- Events use CloudEvents specification for standardization
- Events stored as JSON with `@class` type information for polymorphic deserialization

**Read Model (Read Cache):**
- Denormalized aggregate snapshots stored in `read_cache` table for fast queries
- Updated asynchronously by Projectors when events are dispatched
- Eventually consistent with event store
- Uses PostgreSQL JSONB for flexible querying

### Data Flow: Commands → Aggregates → Events → Projectors

```
User Request
    ↓
Controller (io/web layer)
    ↓
CommandHandler (use_cases layer)
    ├─ 1. Load aggregate from EventStore (hydrate from events)
    ├─ 2. Apply policies (business rules validation)
    ├─ 3. Execute command on aggregate (domain layer)
    ├─ 4. Aggregate produces domain events
    ├─ 5. Persist events to event_log (EventStore)
    └─ 6. Dispatch events asynchronously
        ├─ EventHandlers (side effects: email, logging)
        └─ Projectors (update read_cache)
```

### Package Structure and Responsibilities

**`lib/` - Shared Infrastructure:**
- `domain/`: Base classes for DDD patterns (`AggregateRoot`, `ValueObject`, `DomainEvent`, `EntityID`)
- `domain/events/`: Event dispatcher with async virtual threads, `EventHandler`, `Projector` interfaces
- `persistance/event_store/`: JPA repository for events, `EventEntity`, `EventEntityFactory`
- `persistance/read_cache/`: Query repository for denormalized reads
- `services/result/`: Monadic error handling (`Result<T>`, `ResultMap<T>`) with typed exceptions
- `use_cases/`: `CommandHandler` base, `Policy` base, `UserCommand` interface
- `validators/`: Custom Bean Validation annotations (e.g., `@ULIDConstraint`)

**`auth/` - Authentication Bounded Context:**
- `domain/user_account/`: UserAccount aggregate root with polymorphic states
  - `BaseUserAccount` - Initial state after registration (cannot login)
  - `ActiveUserAccount` - Email validated, can login
  - `WaitingPasswordResetUserAccount` - Password reset in progress
- `domain/user_account/values/`: Value objects (`Username`, `Email`, `Password`, `UserRole`, `PasswordResetToken`)
- `domain/user_account/commands/`: Command objects for mutations
- `domain/user_account/events/`: Domain events raised by aggregate
- `use_cases/`: Command handlers, policies, event handlers, projectors
- `io/web/`: REST controllers, request/response DTOs
- `io/security/`: Spring Security config, JWT filters
- `io/services/`: JWT service, auth service

**`team_building/` - Team Management Bounded Context (in progress)**

### Key Architectural Patterns

**Polymorphic Event Sourcing:**
The aggregate changes Java type as events are applied. This is unique to this codebase:
```java
BaseUserAccount
  → apply(EmailValidatedEvent) → returns ActiveUserAccount
  → apply(PasswordResetStartedEvent) → returns WaitingPasswordResetUserAccount
  → apply(PasswordResetCompletedEvent) → returns ActiveUserAccount
```

This enforces valid state transitions at compile time. You can only call `.completeResetPassword()` on a `WaitingPasswordResetUserAccount`.

**Double Dispatch for Event Application:**
```java
// First dispatch: Event → Aggregate
event.applyTo(aggregate)

// Second dispatch: Aggregate → Event-specific handler
aggregate.apply(PasswordResetStartedEvent event)
```

This allows adding new event types without modifying existing code.

**Result Monad for Error Handling:**
- `Result<T>` - Single error case with typed exceptions (`NotFound`, `Unauthorized`, `BadRequest`, etc.)
- `ResultMap<T>` - Multiple validation errors as `Map<String, String>`
- Eliminates null pointers and forces explicit error handling

**Event Dispatcher with Virtual Threads:**
Events are dispatched asynchronously using Java 21+ virtual threads:
```java
Thread.ofVirtual().start(() -> {
    // Each EventHandler and Projector runs in its own lightweight thread
    // Side effects (email) don't block HTTP request
});
```

**Policy Pattern for Business Rules:**
Reusable business rule validators:
- `AgregateShallNotExistPolicy` - Username must not exist (registration)
- `AgregateShallExistPolicy` - User must exist (login)

Policies query the event store directly and return localized error messages via `MessageSource`.

### Authentication Flow

**JWT-Based Stateless Authentication:**
1. User registers → `BaseUserAccount` created, cannot login yet
2. User validates email → Transitions to `ActiveUserAccount`
3. User logs in → Returns `{ accessToken (15 min), refreshToken (30 days) }`
4. Subsequent requests include `Authorization: Bearer <accessToken>`
5. `JwtRequestFilter` validates token, loads user from read cache, sets Spring Security context

**Security Configuration:**
- Stateless session management
- CSRF disabled (JWT-based API)
- `JwtRequestFilter` extracts and validates tokens
- `TraceFilter` adds request tracing
- Routes defined in `AuthOpenRoutes` and `AuthAuthenticatedRoutes`

**User States (Finite State Machine):**
Enforced via polymorphic aggregate types:
- `BaseUserAccount` → can't login, can validate email
- `ActiveUserAccount` → can login, can start password reset
- `WaitingPasswordResetUserAccount` → can't login, can complete password reset

### Event Store Details

Events follow CloudEvents specification and are stored with:
- `id` - ULID (chronologically sortable, better than UUID)
- `subject` - Aggregate ID (e.g., username)
- `type` - Event class simple name
- `time` - Event timestamp
- `data` - Serialized event with `@class` type info (JSON)
- `source` - User who triggered event or "Anonymous"

**Aggregate Reconstruction:**
```java
EventStore.findUser(username)
    ├─ Query all events WHERE subject = username ORDER BY time ASC
    ├─ Create initial aggregate: new BaseUserAccount(username)
    └─ Call aggregate.hydrate(events)
       └─ For each event: aggregate = event.applyTo(aggregate)
          └─ Aggregate may change type (polymorphic state transitions)
```

### Value Objects and Serialization

Value objects extend `ValueObject` base class:
- Immutable
- Equality based on value, not identity
- Custom Jackson serialization: serialize as string, deserialize from string
- Example: `Password` stores BCrypt hash, provides `matches(plaintext)` method

When creating custom deserializers for value objects, extend `StdDeserializer<T>` and handle JSON as text value.

## Testing Approach

**Test Infrastructure:**
- `FakeEventStore` - In-memory event store for unit tests
- `FakeEventDispatcher` - Captures dispatched events without async execution
- `TestCase` - Base test class with `MessageSource` configuration
- Mock policies: `SuccessPolicy`, `FailurePolicy`

**Unit Test Structure:**
```java
@Test
void testCommandHandler() {
    // Arrange
    FakeEventStore eventStore = new FakeEventStore();
    CommandHandler handler = new SomeCommandHandler(eventStore, policy, dispatcher);

    // Act
    ResultMap<Void> result = handler.handle(command);

    // Assert
    assertTrue(result.isSuccess());
    assertEquals(1, eventStore.findAll().size());
    assertTrue(dispatcher.getDispatchedEvents().contains(ExpectedEvent.class));
}
```

**Integration Tests:**
Use `@SpringBootTest` with `@AutoConfigureMockMvc` for full HTTP request testing.

## Common Development Tasks

### Adding a New Command to Existing Aggregate

1. **Create Command** in `domain/{aggregate}/commands/`:
   ```java
   public record NewActionCommand(AggregateID id, /* params */) implements Command {}
   ```

2. **Add Event** in `domain/{aggregate}/events/`:
   ```java
   public class NewActionEvent extends UserDomainEvent {
       @Override
       public Result<AggregateRoot> applyTo(AggregateRoot aggregate) {
           return aggregate.apply(this);
       }
   }
   ```

3. **Add Method to Aggregate** in `domain/{aggregate}/`:
   ```java
   public ResultMap<Void> performNewAction(NewActionCommand cmd) {
       // Validate
       // Mutate state
       addEvent(new NewActionEvent(...));
       return ResultMap.success(null);
   }

   public Result<AggregateRoot> apply(NewActionEvent event) {
       // Apply event to reconstruct state
       return Result.success(this);
   }
   ```

4. **Create CommandHandler** in `use_cases/`:
   ```java
   @Component
   public class NewActionCommandHandler extends CommandHandler<NewActionCommand, Void> {
       @Override
       protected ResultMap<Void> handleCommand(NewActionCommand cmd) {
           // Load aggregate from event store
           // Execute command on aggregate
           // Save and dispatch events
       }
   }
   ```

5. **Update Projector** in `use_cases/projectors/` to handle new event

6. **Add Controller Endpoint** in `io/web/` if needed

### Adding a New Value Object

1. Extend `ValueObject` base class
2. Make class immutable (record or final fields)
3. Add Bean Validation annotations if needed
4. Create custom Jackson deserializer if complex:
   ```java
   public class MyValueDeserializer extends StdDeserializer<MyValue> {
       @Override
       public MyValue deserialize(JsonParser p, DeserializationContext ctxt) {
           return new MyValue(p.getText());
       }
   }
   ```

### Database Schema Changes

1. Create Liquibase changeset in `src/main/resources/liquibase/`
2. Use appropriate context: `local`, `test`, or `prod`
3. Changes apply automatically on next startup

### Adding Event Handlers (Side Effects)

1. **Create Handler** in `use_cases/event_handlers/`:
   ```java
   @Component
   public class MyEventHandler extends EventHandler<MyEvent> {
       @PostConstruct
       public void initSubscription() {
           dispatcher.subscribe(MyEvent.class, this);
       }

       @Override
       protected void receive(MyEvent event) {
           // Perform side effect (email, logging, etc.)
       }
   }
   ```

2. Handler executes asynchronously in virtual thread when event dispatched

## Internationalization

Messages support French and English via `MessageSource`:
- `lang/messages_{locale}.properties` - General messages
- `lang/error_{locale}.properties` - Error messages
- `ValidationMessages_{locale}.properties` - Bean validation errors

Access in code:
```java
msgSource.getMessage("error.user.not.found", null, locale);
```

Locale extracted from `Accept-Language` header via `LocaleHeaderFilter`.

## Important Conventions

**Aggregate Validation:**
- All aggregates must implement `isValid()` and `validationErrors()`
- Use Bean Validation annotations where possible
- Custom validators for domain-specific rules
- Validation runs before events are added

**Command Naming:**
- Imperative: `RegisterAccountCommand`, `LoginCommand`, `CompleteResetPasswordCommand`
- Not past tense

**Event Naming:**
- Past tense: `AccountRegisteredEvent`, `UserLoggedEvent`, `PasswordResetCompletedEvent`

**Error Handling:**
- Never throw exceptions from domain layer
- Use `Result<T>` or `ResultMap<T>` for all failures
- Map results to HTTP status codes in controllers via `ResultToResponse`

**Testing:**
- Always use fake implementations for unit tests (no database)
- Test event dispatch and persistence separately
- Test aggregate methods in isolation
- Integration tests only for full HTTP flows

**Event Store Queries:**
- Never query event store by fields other than `subject` (aggregate ID)
- Use read cache for all queries
- Event store is append-only, never update or delete

## Technology Stack

- **Java**: 25 (language level)
- **Spring Boot**: 3.5.6
- **Spring Security**: JWT-based stateless auth
- **Spring Data JPA**: Repository pattern with PostgreSQL
- **Liquibase**: Database migrations
- **Hibernate Validator**: Bean validation
- **Lombok**: Boilerplate reduction (`@Data`, `@AllArgsConstructor`)
- **MapStruct**: DTO mapping
- **JJWT**: JWT creation and validation
- **ULID4J**: Chronologically sortable IDs
- **Brevo**: SMTP email service
- **Jackson**: JSON serialization with polymorphic type handling
- **JUnit 5**: Unit testing
- **PostgreSQL**: Event store and read cache

## Application Entry Points

**WebApplication** (active):
- Runs Spring Boot web server on port 7001
- Enables Spring Security with JWT filters
- Starts event dispatcher
- Full REST API available

**ShellApplication** (inactive):
- Intended for CLI interface using Spring Shell
- Currently not implemented

## Notes on Modified Files (git status)

Several files show pending changes related to:
- Password deserialization improvements
- Event handling enhancements
- Message property updates
- Domain event and aggregate root refinements

These appear to be in-progress improvements to the authentication module. When committing, group related changes by feature (e.g., "password reset improvements" vs "event handling refactor").

## Future Work (from specs.md)

- Token refresh endpoint (partially implemented)
- OTP-based SMS authentication
- Token rotation on each request