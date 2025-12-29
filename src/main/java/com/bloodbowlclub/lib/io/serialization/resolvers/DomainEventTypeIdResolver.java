package com.bloodbowlclub.lib.io.serialization.resolvers;

import com.bloodbowlclub.auth.domain.user_account.events.*;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.team_building.domain.events.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom TypeIdResolver for DomainEvent polymorphic serialization.
 *
 * Instead of using @class with full class names (which breaks when refactoring),
 * this resolver uses stable string aliases for each event type.
 *
 * Benefits:
 * - Events can be renamed/moved without breaking deserialization
 * - More readable JSON (short aliases vs full class names)
 * - Testable - easy to mock/verify type resolution
 * - Explicit registry prevents accidental type exposure
 */
public class DomainEventTypeIdResolver extends TypeIdResolverBase {

    // Bidirectional mapping between aliases and classes
    private static final Map<String, Class<? extends DomainEvent>> ALIAS_TO_CLASS = new HashMap<>();
    private static final Map<Class<? extends DomainEvent>, String> CLASS_TO_ALIAS = new HashMap<>();

    static {
        // ===================================================================
        // User Account Events
        // ===================================================================
        register("user.account.registered", AccountRegisteredEvent.class);
        register("user.email.validated", EmailValidatedEvent.class);
        register("user.logged", UserLoggedEvent.class);
        register("user.password.reset.started", PasswordResetStartedEvent.class);
        register("user.password.reset.completed", PasswordResetCompletedEvent.class);

        // ===================================================================
        // Team Building Events
        // ===================================================================
        register("team.draft.registered", DraftTeamRegisteredEvent.class);
        register("team.ruleset.selected", RulesetSelectedEvent.class);
        register("team.roster.chosen", RosterChosenEvent.class);
        register("team.player.hired", PlayerHiredEvent.class);
        register("team.player.removed", PlayerRemovedEvent.class);
        register("team.staff.purchased", TeamStaffPurchasedEvent.class);
        register("team.staff.removed", TeamStaffRemovedEvent.class);
        register("team.reroll.purchased", TeamRerollPurchasedEvent.class);
        register("team.reroll.removed", TeamRerollRemovedEvent.class);
    }

    /**
     * Register an event type with its stable alias.
     *
     * @param alias Stable string identifier (should never change)
     * @param eventClass The event class
     */
    private static void register(String alias, Class<? extends DomainEvent> eventClass) {
        ALIAS_TO_CLASS.put(alias, eventClass);
        CLASS_TO_ALIAS.put(eventClass, alias);
    }

    /**
     * Get the alias for a given event class.
     * Used during serialization to write the type ID.
     */
    public static String getAlias(Class<? extends DomainEvent> eventClass) {
        String alias = CLASS_TO_ALIAS.get(eventClass);
        if (alias == null) {
            throw new IllegalArgumentException(
                "No alias registered for event type: " + eventClass.getName() +
                ". Please register it in DomainEventTypeIdResolver."
            );
        }
        return alias;
    }

    /**
     * Get the event class for a given alias.
     * Used during deserialization to reconstruct the correct type.
     */
    public static Class<? extends DomainEvent> getEventClass(String alias) {
        Class<? extends DomainEvent> eventClass = ALIAS_TO_CLASS.get(alias);
        if (eventClass == null) {
            throw new IllegalArgumentException(
                "Unknown event type alias: '" + alias + "'. " +
                "This may indicate a missing event type registration or corrupted data."
            );
        }
        return eventClass;
    }

    /**
     * Check if an alias is registered.
     * Useful for testing and validation.
     */
    public static boolean isRegistered(String alias) {
        return ALIAS_TO_CLASS.containsKey(alias);
    }

    /**
     * Check if an event class is registered.
     * Useful for testing and validation.
     */
    public static boolean isRegistered(Class<? extends DomainEvent> eventClass) {
        return CLASS_TO_ALIAS.containsKey(eventClass);
    }

    // ===================================================================
    // TypeIdResolverBase Implementation
    // ===================================================================

    private JavaType baseType;

    @Override
    public void init(JavaType baseType) {
        this.baseType = baseType;
    }

    @Override
    public String idFromValue(Object value) {
        if (value == null) {
            return null;
        }
        return getAlias((Class<? extends DomainEvent>) value.getClass());
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return idFromValue(value);
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        Class<? extends DomainEvent> eventClass = getEventClass(id);
        return TypeFactory.defaultInstance().constructType(eventClass);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
