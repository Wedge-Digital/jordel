package com.bloodbowlclub.lib.io.serialization.resolvers;

import com.bloodbowlclub.lib.domain.Entity;
import com.bloodbowlclub.team_building.domain.roster.PlayerDefinition;
import com.bloodbowlclub.team_building.domain.ruleset.RosterTier;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom TypeIdResolver for Entity polymorphic serialization.
 *
 * Manages the serialization of different entity types using stable string aliases
 * instead of full class names.
 *
 * Benefits:
 * - Entities can be renamed/moved without breaking deserialization
 * - More readable JSON in serialized form (short aliases vs full class names)
 * - Testable - easy to mock/verify type resolution
 * - Explicit registry prevents accidental type exposure
 */
public class EntityTypeIdResolver extends TypeIdResolverBase {

    private static final Map<String, Class<? extends Entity>> ALIAS_TO_CLASS = new HashMap<>();
    private static final Map<Class<? extends Entity>, String> CLASS_TO_ALIAS = new HashMap<>();

    static {
        // ===================================================================
        // Roster Domain Entities
        // ===================================================================
        register("entity.player.definition", PlayerDefinition.class);

        // ===================================================================
        // Ruleset Domain Entities
        // ===================================================================
        register("entity.roster.tier", RosterTier.class);
    }

    /**
     * Register an entity type with its stable alias.
     *
     * @param alias Stable string identifier (should never change)
     * @param entityClass The entity class
     */
    private static void register(String alias, Class<? extends Entity> entityClass) {
        ALIAS_TO_CLASS.put(alias, entityClass);
        CLASS_TO_ALIAS.put(entityClass, alias);
    }

    /**
     * Get the alias for a given entity class.
     * Used during serialization to write the type ID.
     */
    public static String getAlias(Class<? extends Entity> entityClass) {
        String alias = CLASS_TO_ALIAS.get(entityClass);
        if (alias == null) {
            throw new IllegalArgumentException(
                "No alias registered for entity type: " + entityClass.getName() +
                ". Please register it in EntityTypeIdResolver."
            );
        }
        return alias;
    }

    /**
     * Get the entity class for a given alias.
     * Used during deserialization to reconstruct the correct type.
     */
    public static Class<? extends Entity> getEntityClass(String alias) {
        Class<? extends Entity> entityClass = ALIAS_TO_CLASS.get(alias);
        if (entityClass == null) {
            throw new IllegalArgumentException(
                "Unknown entity type alias: '" + alias + "'. " +
                "This may indicate a missing entity type registration or corrupted data."
            );
        }
        return entityClass;
    }

    /**
     * Check if an alias is registered.
     * Useful for testing and validation.
     */
    public static boolean isRegistered(String alias) {
        return ALIAS_TO_CLASS.containsKey(alias);
    }

    /**
     * Check if an entity class is registered.
     * Useful for testing and validation.
     */
    public static boolean isRegistered(Class<? extends Entity> entityClass) {
        return CLASS_TO_ALIAS.containsKey(entityClass);
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
        return getAlias((Class<? extends Entity>) value.getClass());
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return idFromValue(value);
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        Class<? extends Entity> entityClass = getEntityClass(id);
        return TypeFactory.defaultInstance().constructType(entityClass);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
