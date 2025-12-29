package com.bloodbowlclub.lib.domain.events;

import com.bloodbowlclub.lib.io.serialization.resolvers.DomainEventTypeIdResolver;
import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.auth.domain.user_account.events.EmailValidatedEvent;
import com.bloodbowlclub.auth.domain.user_account.events.UserLoggedEvent;
import com.bloodbowlclub.team_building.domain.events.PlayerHiredEvent;
import com.bloodbowlclub.team_building.domain.events.TeamRerollPurchasedEvent;
import com.bloodbowlclub.team_building.domain.events.TeamStaffPurchasedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DomainEventTypeIdResolver to ensure polymorphic serialization works correctly.
 */
public class DomainEventTypeIdResolverTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ===================================================================
    // Alias Registry Tests
    // ===================================================================

    @Test
    @DisplayName("All registered event types should have bidirectional mapping")
    void testAllEventTypesAreRegistered() {
        // User Account Events
        assertTrue(DomainEventTypeIdResolver.isRegistered(AccountRegisteredEvent.class));
        assertTrue(DomainEventTypeIdResolver.isRegistered(EmailValidatedEvent.class));
        assertTrue(DomainEventTypeIdResolver.isRegistered(UserLoggedEvent.class));

        // Team Building Events
        assertTrue(DomainEventTypeIdResolver.isRegistered(PlayerHiredEvent.class));
        assertTrue(DomainEventTypeIdResolver.isRegistered(TeamStaffPurchasedEvent.class));
        assertTrue(DomainEventTypeIdResolver.isRegistered(TeamRerollPurchasedEvent.class));

        // Check aliases
        assertTrue(DomainEventTypeIdResolver.isRegistered("user.account.registered"));
        assertTrue(DomainEventTypeIdResolver.isRegistered("team.player.hired"));
        assertTrue(DomainEventTypeIdResolver.isRegistered("team.reroll.purchased"));
    }

    @Test
    @DisplayName("getAlias should return correct alias for event class")
    void testGetAlias() {
        assertEquals("user.account.registered",
            DomainEventTypeIdResolver.getAlias(AccountRegisteredEvent.class));
        assertEquals("user.email.validated",
            DomainEventTypeIdResolver.getAlias(EmailValidatedEvent.class));
        assertEquals("team.player.hired",
            DomainEventTypeIdResolver.getAlias(PlayerHiredEvent.class));
    }

    @Test
    @DisplayName("getEventClass should return correct class for alias")
    void testGetEventClass() {
        assertEquals(AccountRegisteredEvent.class,
            DomainEventTypeIdResolver.getEventClass("user.account.registered"));
        assertEquals(EmailValidatedEvent.class,
            DomainEventTypeIdResolver.getEventClass("user.email.validated"));
        assertEquals(PlayerHiredEvent.class,
            DomainEventTypeIdResolver.getEventClass("team.player.hired"));
    }

    @Test
    @DisplayName("getAlias should throw exception for unregistered event class")
    void testGetAliasThrowsForUnregisteredClass() {
        class UnregisteredEvent extends DomainEvent {
            @Override
            public com.bloodbowlclub.lib.services.result.Result<com.bloodbowlclub.lib.domain.AggregateRoot> applyTo(
                com.bloodbowlclub.lib.domain.AggregateRoot aggregate) {
                return null;
            }
        }

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            DomainEventTypeIdResolver.getAlias(UnregisteredEvent.class);
        });

        assertTrue(exception.getMessage().contains("No alias registered"));
    }

    @Test
    @DisplayName("getEventClass should throw exception for unknown alias")
    void testGetEventClassThrowsForUnknownAlias() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            DomainEventTypeIdResolver.getEventClass("unknown.event.type");
        });

        assertTrue(exception.getMessage().contains("Unknown event type alias"));
    }

    // ===================================================================
    // Serialization Tests
    // ===================================================================

    @Test
    @DisplayName("Serialization should use @type with alias instead of @class")
    void testSerializationUsesAlias() throws JsonProcessingException {
        // Given - Create JSON manually to test deserialization
        String json = """
            {
                "@type": "user.account.registered",
                "aggregateId": "testUser",
                "timeStampedAt": "2025-01-15T10:30:00Z"
            }
            """;

        // When
        DomainEvent event = objectMapper.readValue(json, DomainEvent.class);

        // Then - Re-serialize to verify @type is used
        String serialized = objectMapper.writeValueAsString(event);
        System.out.println("Serialized JSON: " + serialized);  // Debug
        assertTrue(serialized.contains("\"@type\":\"user.account.registered\""),
            "JSON should contain @type with alias. Actual JSON: " + serialized);
        assertFalse(serialized.contains("@class"),
            "JSON should NOT contain @class property. Actual JSON: " + serialized);
        assertFalse(serialized.contains("AccountRegisteredEvent"),
            "JSON should NOT contain class name. Actual JSON: " + serialized);
    }

    @Test
    @DisplayName("Deserialization should reconstruct correct event type from alias")
    void testDeserializationFromAlias() throws JsonProcessingException {
        // Given - JSON with @type alias
        String json = """
            {
                "@type": "user.email.validated",
                "aggregateId": "testUser",
                "timeStampedAt": "2025-01-15T10:30:00Z"
            }
            """;

        // When
        DomainEvent event = objectMapper.readValue(json, DomainEvent.class);

        // Then
        assertNotNull(event);
        assertEquals(EmailValidatedEvent.class, event.getClass());
        assertEquals("testUser", event.getAggregateId());
    }

    @Test
    @DisplayName("Round-trip serialization should preserve event type and data")
    void testRoundTripSerialization() throws JsonProcessingException {
        // Given - JSON with specific event data
        String originalJson = """
            {
                "@type": "user.logged",
                "aggregateId": "testUser",
                "timeStampedAt": "2025-01-15T10:30:45.123Z"
            }
            """;

        // When - Deserialize and re-serialize
        DomainEvent event = objectMapper.readValue(originalJson, DomainEvent.class);
        String reserializedJson = objectMapper.writeValueAsString(event);
        DomainEvent deserializedAgain = objectMapper.readValue(reserializedJson, DomainEvent.class);

        // Then
        assertNotNull(event);
        assertEquals(UserLoggedEvent.class, event.getClass());
        assertEquals("testUser", event.getAggregateId());
        assertNotNull(deserializedAgain);
        assertEquals(UserLoggedEvent.class, deserializedAgain.getClass());
    }

    @Test
    @DisplayName("Deserialization should fail gracefully with unknown type alias")
    void testDeserializationFailsWithUnknownAlias() {
        // Given - JSON with unknown alias
        String json = """
            {
                "@type": "unknown.event.type",
                "aggregateId": "testUser"
            }
            """;

        // When/Then
        assertThrows(Exception.class, () -> {
            objectMapper.readValue(json, DomainEvent.class);
        });
    }

    // ===================================================================
    // Backward Compatibility Tests (Migration)
    // ===================================================================

    @Test
    @DisplayName("Old events with @class should not deserialize with new resolver")
    void testOldFormatDoesNotDeserialize() {
        // Given - Old format JSON with @class
        String oldJson = """
            {
                "@class": "com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent",
                "aggregateId": "testUser"
            }
            """;

        // When/Then - Should fail because resolver expects @type
        assertThrows(Exception.class, () -> {
            objectMapper.readValue(oldJson, DomainEvent.class);
        });
    }

    // ===================================================================
    // Team Building Events Tests
    // ===================================================================

    @Test
    @DisplayName("Team building events should serialize with correct aliases")
    void testTeamBuildingEventsSerialization() throws JsonProcessingException {
        // Test PlayerHiredEvent
        String playerJson = """
            {
                "@type": "team.player.hired",
                "aggregateId": "team123"
            }
            """;
        DomainEvent playerEvent = objectMapper.readValue(playerJson, DomainEvent.class);
        assertEquals(PlayerHiredEvent.class, playerEvent.getClass());

        // Test TeamStaffPurchasedEvent
        String staffJson = """
            {
                "@type": "team.staff.purchased",
                "aggregateId": "team123"
            }
            """;
        DomainEvent staffEvent = objectMapper.readValue(staffJson, DomainEvent.class);
        assertEquals(TeamStaffPurchasedEvent.class, staffEvent.getClass());

        // Test TeamRerollPurchasedEvent
        String rerollJson = """
            {
                "@type": "team.reroll.purchased",
                "aggregateId": "team123"
            }
            """;
        DomainEvent rerollEvent = objectMapper.readValue(rerollJson, DomainEvent.class);
        assertEquals(TeamRerollPurchasedEvent.class, rerollEvent.getClass());
    }

    // ===================================================================
    // JSON Readability Tests
    // ===================================================================

    @Test
    @DisplayName("Serialized JSON should be more readable with aliases than with full class names")
    void testJsonReadability() throws JsonProcessingException {
        // Given
        String json = """
            {
                "@type": "user.account.registered",
                "aggregateId": "testUser",
                "timeStampedAt": "2025-01-15T10:30:00Z"
            }
            """;

        DomainEvent event = objectMapper.readValue(json, DomainEvent.class);

        // When - Re-serialize with pretty printing
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(event);

        // Then - Verify alias is used and is human-readable
        assertTrue(prettyJson.contains("user.account.registered"));

        // Verify it's shorter and more readable than full class name
        String expectedFullClassName = "com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent";
        assertFalse(prettyJson.contains(expectedFullClassName));

        // Alias should be much shorter
        int aliasLength = "user.account.registered".length();
        int fullClassNameLength = expectedFullClassName.length();
        assertTrue(aliasLength < fullClassNameLength / 2,
            "Alias should be at least 50% shorter than full class name");
    }
}
