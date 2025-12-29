package com.bloodbowlclub.lib.io.serialization.resolvers;

import com.bloodbowlclub.auth.domain.user_account.ActiveUserAccount;
import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.domain.user_account.WaitingPasswordResetUserAccount;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.bloodbowlclub.team_building.domain.ruleset.Ruleset;
import com.bloodbowlclub.team_building.domain.team.BaseTeam;
import com.bloodbowlclub.team_building.domain.team.DraftTeam;
import com.bloodbowlclub.team_building.domain.team.RosterSelectedTeam;
import com.bloodbowlclub.team_building.domain.team.RulesetSelectedTeam;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom TypeIdResolver for AggregateRoot polymorphic serialization.
 *
 * Instead of using @class with full class names (which breaks when refactoring),
 * this resolver uses stable string aliases for each aggregate type.
 *
 * Benefits:
 * - Aggregates can be renamed/moved without breaking deserialization from event store
 * - More readable JSON in read_cache (short aliases vs full class names)
 * - Testable - easy to mock/verify type resolution
 * - Explicit registry prevents accidental type exposure
 * - Supports polymorphic state transitions (e.g., BaseUserAccount → ActiveUserAccount)
 */
public class AggregateRootTypeIdResolver extends TypeIdResolverBase {

    // Bidirectional mapping between aliases and classes
    private static final Map<String, Class<? extends AggregateRoot>> ALIAS_TO_CLASS = new HashMap<>();
    private static final Map<Class<? extends AggregateRoot>, String> CLASS_TO_ALIAS = new HashMap<>();

    static {
        // ===================================================================
        // User Account Aggregates (Polymorphic State Machine)
        // ===================================================================
        // State transitions: BaseUserAccount → ActiveUserAccount ↔ WaitingPasswordResetUserAccount
        register("user.account.base", BaseUserAccount.class);
        register("user.account.active", ActiveUserAccount.class);
        register("user.account.password.reset", WaitingPasswordResetUserAccount.class);

        // ===================================================================
        // Team Building Aggregates (Polymorphic State Machine)
        // ===================================================================
        // State transitions: BaseTeam → DraftTeam → RulesetSelectedTeam → RosterSelectedTeam
        register("team.base", BaseTeam.class);
        register("team.draft", DraftTeam.class);
        register("team.ruleset.selected", RulesetSelectedTeam.class);
        register("team.roster.selected", RosterSelectedTeam.class);

        // ===================================================================
        // Domain Entities (Non-polymorphic but serialized with aggregates)
        // ===================================================================
        register("roster", Roster.class);
        register("ruleset", Ruleset.class);
    }

    /**
     * Register an aggregate type with its stable alias.
     *
     * @param alias Stable string identifier (should never change)
     * @param aggregateClass The aggregate class
     */
    private static void register(String alias, Class<? extends AggregateRoot> aggregateClass) {
        ALIAS_TO_CLASS.put(alias, aggregateClass);
        CLASS_TO_ALIAS.put(aggregateClass, alias);
    }

    /**
     * Get the alias for a given aggregate class.
     * Used during serialization to write the type ID.
     */
    public static String getAlias(Class<? extends AggregateRoot> aggregateClass) {
        String alias = CLASS_TO_ALIAS.get(aggregateClass);
        if (alias == null) {
            throw new IllegalArgumentException(
                "No alias registered for aggregate type: " + aggregateClass.getName() +
                ". Please register it in AggregateRootTypeIdResolver."
            );
        }
        return alias;
    }

    /**
     * Get the aggregate class for a given alias.
     * Used during deserialization to reconstruct the correct type.
     */
    public static Class<? extends AggregateRoot> getAggregateClass(String alias) {
        Class<? extends AggregateRoot> aggregateClass = ALIAS_TO_CLASS.get(alias);
        if (aggregateClass == null) {
            throw new IllegalArgumentException(
                "Unknown aggregate type alias: '" + alias + "'. " +
                "This may indicate a missing aggregate type registration or corrupted data."
            );
        }
        return aggregateClass;
    }

    /**
     * Check if an alias is registered.
     * Useful for testing and validation.
     */
    public static boolean isRegistered(String alias) {
        return ALIAS_TO_CLASS.containsKey(alias);
    }

    /**
     * Check if an aggregate class is registered.
     * Useful for testing and validation.
     */
    public static boolean isRegistered(Class<? extends AggregateRoot> aggregateClass) {
        return CLASS_TO_ALIAS.containsKey(aggregateClass);
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
        return getAlias((Class<? extends AggregateRoot>) value.getClass());
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return idFromValue(value);
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        Class<? extends AggregateRoot> aggregateClass = getAggregateClass(id);
        return TypeFactory.defaultInstance().constructType(aggregateClass);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
