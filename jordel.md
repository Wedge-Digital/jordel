Objectif du Projet

Système de gestion de ligue Blood Bowl basé sur Java 25 et Spring Boot 3.5.6

Architecture Fondamentale

Event Sourcing + CQRS
- Write Model : Tous les changements d'état capturés comme événements immuables dans event_log
- Read Model : Snapshots dénormalisés dans read_cache (JSONB PostgreSQL)
- Reconstruction d'agrégats par rejeu chronologique des événements

Domain-Driven Design (DDD)
- Bounded contexts : auth/ (authentification), team_building/ (gestion d'équipes)
- Agrégats, Value Objects, Domain Events
- Clean architecture avec séparation stricte des responsabilités

Spécificités Techniques Uniques

Agrégats Polymorphiques
BaseUserAccount → EmailValidatedEvent → ActiveUserAccount
ActiveUserAccount → PasswordResetStartedEvent → WaitingPasswordResetUserAccount
Les changements d'état modifient le type Java de l'agrégat, garantissant les transitions valides à la compilation.

Double Dispatch pour les Événements
event.applyTo(aggregate) → aggregate.apply(specificEvent)

Result Monad
- Pas d'exceptions dans le domaine
- Result<T> et ResultMap<T> pour gestion d'erreurs typée

Event Dispatcher avec Virtual Threads
- Exécution asynchrone des EventHandlers et Projectors
- Pas de blocage des requêtes HTTP

Stack Technique

- Java 25, Spring Boot 3.5.6, Spring Security (JWT stateless)
- PostgreSQL + Liquibase
- CloudEvents, ULID (IDs chronologiques)
- Bean Validation, MapStruct, Lombok

Flux de Données

Controller → CommandHandler → Aggregate → Events → EventStore
↓
EventDispatcher
↓           ↓
Projectors   EventHandlers
(read_cache)  (side effects)

Le projet privilégie l'immutabilité, la traçabilité complète via events, et la séparation lectures/écritures.