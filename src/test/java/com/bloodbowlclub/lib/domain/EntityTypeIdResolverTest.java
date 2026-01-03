package com.bloodbowlclub.lib.domain;

import com.bloodbowlclub.lib.io.serialization.resolvers.EntityTypeIdResolver;
import com.bloodbowlclub.team_building.domain.roster.PlayerDefinition;
import com.bloodbowlclub.team_building.domain.ruleset.RosterTier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for EntityTypeIdResolver to ensure polymorphic serialization works correctly.
 */
public class EntityTypeIdResolverTest {

    // ===================================================================
    // Alias Registry Tests
    // ===================================================================

    @Test
    @DisplayName("All registered entity types should have bidirectional mapping")
    void testAllEntityTypesAreRegistered() {
        // Roster Domain Entities
        assertTrue(EntityTypeIdResolver.isRegistered(PlayerDefinition.class));

        // Ruleset Domain Entities
        assertTrue(EntityTypeIdResolver.isRegistered(RosterTier.class));

        // Check aliases
        assertTrue(EntityTypeIdResolver.isRegistered("entity.player.definition"));
        assertTrue(EntityTypeIdResolver.isRegistered("entity.roster.tier"));
    }

    @Test
    @DisplayName("getAlias should return correct alias for entity class")
    void testGetAlias() {
        assertEquals("entity.player.definition",
            EntityTypeIdResolver.getAlias(PlayerDefinition.class));
        assertEquals("entity.roster.tier",
            EntityTypeIdResolver.getAlias(RosterTier.class));
    }

    @Test
    @DisplayName("getEntityClass should return correct class for alias")
    void testGetEntityClass() {
        assertEquals(PlayerDefinition.class,
            EntityTypeIdResolver.getEntityClass("entity.player.definition"));
        assertEquals(RosterTier.class,
            EntityTypeIdResolver.getEntityClass("entity.roster.tier"));
    }

    @Test
    @DisplayName("getAlias should throw exception for unregistered entity class")
    void testGetAliasThrowsForUnregisteredClass() {
        class UnregisteredEntity extends Entity {
            @Override
            public String getId() {
                return "test";
            }
        }

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            EntityTypeIdResolver.getAlias(UnregisteredEntity.class);
        });

        assertTrue(exception.getMessage().contains("No alias registered for entity type"));
    }

    @Test
    @DisplayName("getEntityClass should throw exception for unknown alias")
    void testGetEntityClassThrowsForUnknownAlias() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            EntityTypeIdResolver.getEntityClass("unknown.entity.type");
        });

        assertTrue(exception.getMessage().contains("Unknown entity type alias"));
    }

    // ===================================================================
    // Type Resolution Tests (Direct Resolver Testing)
    // ===================================================================

    @Test
    @DisplayName("TypeIdResolver should return correct type ID from value")
    void testIdFromValue() {
        // Given
        EntityTypeIdResolver resolver = new EntityTypeIdResolver();
        PlayerDefinition player = PlayerDefinition.builder().build();

        // When
        String typeId = resolver.idFromValue(player);

        // Then
        assertEquals("entity.player.definition", typeId);
    }

    @Test
    @DisplayName("TypeIdResolver should return correct type ID for different entity types")
    void testIdFromValueVariousTypes() {
        // Given
        EntityTypeIdResolver resolver = new EntityTypeIdResolver();

        // Test PlayerDefinition
        PlayerDefinition player = PlayerDefinition.builder().build();
        assertEquals("entity.player.definition", resolver.idFromValue(player));

        // Test RosterTier
        RosterTier tier = RosterTier.builder().build();
        assertEquals("entity.roster.tier", resolver.idFromValue(tier));
    }

    @Test
    @DisplayName("TypeIdResolver should handle null value gracefully")
    void testIdFromValueWithNull() {
        // Given
        EntityTypeIdResolver resolver = new EntityTypeIdResolver();

        // When
        String typeId = resolver.idFromValue(null);

        // Then
        assertNull(typeId);
    }

    // ===================================================================
    // Alias Readability Tests
    // ===================================================================

    @Test
    @DisplayName("Aliases should be more readable than full class names")
    void testAliasReadability() {
        // Test that aliases are significantly shorter than full class names

        // PlayerDefinition example
        String playerAlias = EntityTypeIdResolver.getAlias(PlayerDefinition.class);
        String playerFullClassName = "com.bloodbowlclub.team_building.domain.roster.PlayerDefinition";
        assertTrue(playerAlias.length() < playerFullClassName.length() / 2,
            "Alias should be at least 50% shorter than full class name");
        assertEquals("entity.player.definition", playerAlias);

        // RosterTier example
        String tierAlias = EntityTypeIdResolver.getAlias(RosterTier.class);
        String tierFullClassName = "com.bloodbowlclub.team_building.domain.ruleset.RosterTier";
        assertTrue(tierAlias.length() < tierFullClassName.length() / 2,
            "Alias should be at least 50% shorter than full class name");
        assertEquals("entity.roster.tier", tierAlias);
    }

    // ===================================================================
    // Bidirectional Mapping Consistency Tests
    // ===================================================================

    @Test
    @DisplayName("All registered aliases should have round-trip mapping")
    void testRoundTripMapping() {
        // For each registered entity class, verify that:
        // class -> alias -> class returns the original class

        // PlayerDefinition
        Class<? extends Entity> playerClass = PlayerDefinition.class;
        String playerAlias = EntityTypeIdResolver.getAlias(playerClass);
        Class<? extends Entity> playerClassBack = EntityTypeIdResolver.getEntityClass(playerAlias);
        assertEquals(playerClass, playerClassBack);

        // RosterTier
        Class<? extends Entity> tierClass = RosterTier.class;
        String tierAlias = EntityTypeIdResolver.getAlias(tierClass);
        Class<? extends Entity> tierClassBack = EntityTypeIdResolver.getEntityClass(tierAlias);
        assertEquals(tierClass, tierClassBack);
    }
}
