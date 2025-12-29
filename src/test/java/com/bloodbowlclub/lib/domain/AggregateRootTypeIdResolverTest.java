package com.bloodbowlclub.lib.domain;

import com.bloodbowlclub.auth.domain.user_account.ActiveUserAccount;
import com.bloodbowlclub.lib.io.serialization.resolvers.AggregateRootTypeIdResolver;
import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.domain.user_account.WaitingPasswordResetUserAccount;
import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.bloodbowlclub.team_building.domain.ruleset.Ruleset;
import com.bloodbowlclub.team_building.domain.team.BaseTeam;
import com.bloodbowlclub.team_building.domain.team.DraftTeam;
import com.bloodbowlclub.team_building.domain.team.RosterSelectedTeam;
import com.bloodbowlclub.team_building.domain.team.RulesetSelectedTeam;
import com.bloodbowlclub.team_building.domain.team_staff.TeamStaff;
import com.bloodbowlclub.team_building.domain.team_staff.staff_detail.Apothecary;
import com.bloodbowlclub.team_building.domain.team_staff.staff_detail.Cheerleaders;
import com.bloodbowlclub.team_building.domain.team_staff.staff_detail.CoachAssistant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AggregateRootTypeIdResolver to ensure polymorphic serialization works correctly.
 */
public class AggregateRootTypeIdResolverTest {

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
    @DisplayName("All registered aggregate types should have bidirectional mapping")
    void testAllAggregateTypesAreRegistered() {
        // User Account Aggregates
        assertTrue(AggregateRootTypeIdResolver.isRegistered(BaseUserAccount.class));
        assertTrue(AggregateRootTypeIdResolver.isRegistered(ActiveUserAccount.class));
        assertTrue(AggregateRootTypeIdResolver.isRegistered(WaitingPasswordResetUserAccount.class));

        // Team Aggregates
        assertTrue(AggregateRootTypeIdResolver.isRegistered(BaseTeam.class));
        assertTrue(AggregateRootTypeIdResolver.isRegistered(DraftTeam.class));
        assertTrue(AggregateRootTypeIdResolver.isRegistered(RulesetSelectedTeam.class));
        assertTrue(AggregateRootTypeIdResolver.isRegistered(RosterSelectedTeam.class));

        // Domain Entities
        assertTrue(AggregateRootTypeIdResolver.isRegistered(Roster.class));
        assertTrue(AggregateRootTypeIdResolver.isRegistered(Ruleset.class));

        // Check aliases
        assertTrue(AggregateRootTypeIdResolver.isRegistered("user.account.base"));
        assertTrue(AggregateRootTypeIdResolver.isRegistered("user.account.active"));
        assertTrue(AggregateRootTypeIdResolver.isRegistered("team.base"));
        assertTrue(AggregateRootTypeIdResolver.isRegistered("team.roster.selected"));
        assertTrue(AggregateRootTypeIdResolver.isRegistered("roster"));
        assertTrue(AggregateRootTypeIdResolver.isRegistered("ruleset"));
    }

    @Test
    @DisplayName("getAlias should return correct alias for aggregate class")
    void testGetAlias() {
        assertEquals("user.account.base",
            AggregateRootTypeIdResolver.getAlias(BaseUserAccount.class));
        assertEquals("user.account.active",
            AggregateRootTypeIdResolver.getAlias(ActiveUserAccount.class));
        assertEquals("user.account.password.reset",
            AggregateRootTypeIdResolver.getAlias(WaitingPasswordResetUserAccount.class));
        assertEquals("team.base",
            AggregateRootTypeIdResolver.getAlias(BaseTeam.class));
        assertEquals("team.draft",
            AggregateRootTypeIdResolver.getAlias(DraftTeam.class));
        assertEquals("roster",
            AggregateRootTypeIdResolver.getAlias(Roster.class));
    }

    @Test
    @DisplayName("getAggregateClass should return correct class for alias")
    void testGetAggregateClass() {
        assertEquals(BaseUserAccount.class,
            AggregateRootTypeIdResolver.getAggregateClass("user.account.base"));
        assertEquals(ActiveUserAccount.class,
            AggregateRootTypeIdResolver.getAggregateClass("user.account.active"));
        assertEquals(WaitingPasswordResetUserAccount.class,
            AggregateRootTypeIdResolver.getAggregateClass("user.account.password.reset"));
        assertEquals(BaseTeam.class,
            AggregateRootTypeIdResolver.getAggregateClass("team.base"));
        assertEquals(DraftTeam.class,
            AggregateRootTypeIdResolver.getAggregateClass("team.draft"));
        assertEquals(Roster.class,
            AggregateRootTypeIdResolver.getAggregateClass("roster"));
    }

    @Test
    @DisplayName("getAlias should throw exception for unregistered aggregate class")
    void testGetAliasThrowsForUnregisteredClass() {
        class UnregisteredAggregate extends AggregateRoot {
            @Override
            public String getId() {
                return "test";
            }
        }

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            AggregateRootTypeIdResolver.getAlias(UnregisteredAggregate.class);
        });

        assertTrue(exception.getMessage().contains("No alias registered for aggregate type"));
    }

    @Test
    @DisplayName("getAggregateClass should throw exception for unknown alias")
    void testGetAggregateClassThrowsForUnknownAlias() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            AggregateRootTypeIdResolver.getAggregateClass("unknown.aggregate.type");
        });

        assertTrue(exception.getMessage().contains("Unknown aggregate type alias"));
    }

    // ===================================================================
    // Type Resolution Tests (Direct Resolver Testing)
    // ===================================================================

    @Test
    @DisplayName("TypeIdResolver should return correct type ID from value")
    void testIdFromValue() {
        // Given
        AggregateRootTypeIdResolver resolver = new AggregateRootTypeIdResolver();
        BaseUserAccount userAccount = new BaseUserAccount("testuser");

        // When
        String typeId = resolver.idFromValue(userAccount);

        // Then
        assertEquals("user.account.base", typeId);
    }

    @Test
    @DisplayName("TypeIdResolver should return correct type ID from value for different aggregate types")
    void testIdFromValueVariousTypes() {
        // Given
        AggregateRootTypeIdResolver resolver = new AggregateRootTypeIdResolver();

        // Test ActiveUserAccount
        ActiveUserAccount activeUser = new ActiveUserAccount(new BaseUserAccount("testuser"));
        assertEquals("user.account.active", resolver.idFromValue(activeUser));

        // Note: We cannot easily instantiate complex aggregates like Team, Roster, Ruleset
        // without providing all required value objects. These are tested via the alias registry tests.
    }

    @Test
    @DisplayName("TypeIdResolver should handle null value gracefully")
    void testIdFromValueWithNull() {
        // Given
        AggregateRootTypeIdResolver resolver = new AggregateRootTypeIdResolver();

        // When
        String typeId = resolver.idFromValue(null);

        // Then
        assertNull(typeId);
    }

    @Test
    @DisplayName("Deserialization should fail gracefully with unknown type alias")
    void testDeserializationFailsWithUnknownAlias() {
        // Given - JSON with unknown alias
        String json = """
            {
                "@type": "unknown.aggregate.type",
                "id": "test123"
            }
            """;

        // When/Then
        assertThrows(Exception.class, () -> {
            objectMapper.readValue(json, AggregateRoot.class);
        });
    }

    // ===================================================================
    // Backward Compatibility Tests (Migration)
    // ===================================================================

    @Test
    @DisplayName("Old aggregates with @class should not deserialize with new resolver")
    void testOldFormatDoesNotDeserialize() {
        // Given - Old format JSON with @class
        String oldJson = """
            {
                "@class": "com.bloodbowlclub.team_building.domain.roster.Roster",
                "rosterId": "roster123",
                "name": "Human"
            }
            """;

        // When/Then - Should fail because resolver expects @type
        assertThrows(Exception.class, () -> {
            objectMapper.readValue(oldJson, AggregateRoot.class);
        });
    }

    // ===================================================================
    // Polymorphic State Transitions Tests
    // ===================================================================

    @Test
    @DisplayName("User account state transitions should have correct alias mappings")
    void testUserAccountStateTransitions() {
        // Test BaseUserAccount
        assertEquals("user.account.base",
            AggregateRootTypeIdResolver.getAlias(BaseUserAccount.class));
        assertEquals(BaseUserAccount.class,
            AggregateRootTypeIdResolver.getAggregateClass("user.account.base"));

        // Test ActiveUserAccount
        assertEquals("user.account.active",
            AggregateRootTypeIdResolver.getAlias(ActiveUserAccount.class));
        assertEquals(ActiveUserAccount.class,
            AggregateRootTypeIdResolver.getAggregateClass("user.account.active"));

        // Test WaitingPasswordResetUserAccount
        assertEquals("user.account.password.reset",
            AggregateRootTypeIdResolver.getAlias(WaitingPasswordResetUserAccount.class));
        assertEquals(WaitingPasswordResetUserAccount.class,
            AggregateRootTypeIdResolver.getAggregateClass("user.account.password.reset"));
    }

    @Test
    @DisplayName("Team state transitions should have correct alias mappings")
    void testTeamStateTransitions() {
        // Test BaseTeam
        assertEquals("team.base",
            AggregateRootTypeIdResolver.getAlias(BaseTeam.class));
        assertEquals(BaseTeam.class,
            AggregateRootTypeIdResolver.getAggregateClass("team.base"));

        // Test DraftTeam
        assertEquals("team.draft",
            AggregateRootTypeIdResolver.getAlias(DraftTeam.class));
        assertEquals(DraftTeam.class,
            AggregateRootTypeIdResolver.getAggregateClass("team.draft"));

        // Test RulesetSelectedTeam
        assertEquals("team.ruleset.selected",
            AggregateRootTypeIdResolver.getAlias(RulesetSelectedTeam.class));
        assertEquals(RulesetSelectedTeam.class,
            AggregateRootTypeIdResolver.getAggregateClass("team.ruleset.selected"));

        // Test RosterSelectedTeam
        assertEquals("team.roster.selected",
            AggregateRootTypeIdResolver.getAlias(RosterSelectedTeam.class));
        assertEquals(RosterSelectedTeam.class,
            AggregateRootTypeIdResolver.getAggregateClass("team.roster.selected"));
    }

    // ===================================================================
    // JSON Readability Tests
    // ===================================================================

    @Test
    @DisplayName("Aliases should be more readable than full class names")
    void testAliasReadability() {
        // Test that aliases are significantly shorter than full class names

        // Roster example
        String rosterAlias = AggregateRootTypeIdResolver.getAlias(Roster.class);
        String rosterFullClassName = "com.bloodbowlclub.team_building.domain.roster.Roster";
        assertTrue(rosterAlias.length() < rosterFullClassName.length() / 2,
            "Alias should be at least 50% shorter than full class name");
        assertEquals("roster", rosterAlias);

        // UserAccount example
        String userAlias = AggregateRootTypeIdResolver.getAlias(ActiveUserAccount.class);
        String userFullClassName = "com.bloodbowlclub.auth.domain.user_account.ActiveUserAccount";
        assertTrue(userAlias.length() < userFullClassName.length() / 2,
            "Alias should be at least 50% shorter than full class name");
        assertEquals("user.account.active", userAlias);

        // Team example
        String teamAlias = AggregateRootTypeIdResolver.getAlias(RosterSelectedTeam.class);
        String teamFullClassName = "com.bloodbowlclub.team_building.domain.team.RosterSelectedTeam";
        assertTrue(teamAlias.length() < teamFullClassName.length() / 2,
            "Alias should be at least 50% shorter than full class name");
        assertEquals("team.roster.selected", teamAlias);
    }

    // ===================================================================
    // TeamStaff Polymorphic Tests (TeamStaffTypeIdResolver)
    // ===================================================================

    @Test
    @DisplayName("TeamStaff subclasses should use TeamStaffTypeIdResolver with correct aliases")
    void testTeamStaffPolymorphism() {
        // Note: TeamStaff uses its own TypeIdResolver (TeamStaffTypeIdResolver)
        // Test that the TeamStaffTypeIdResolver is properly configured

        // Test Apothecary
        assertTrue(com.bloodbowlclub.lib.io.serialization.resolvers.TeamStaffTypeIdResolver
            .isRegistered(Apothecary.class));
        assertEquals("staff.apothecary",
            com.bloodbowlclub.lib.io.serialization.resolvers.TeamStaffTypeIdResolver
                .getAlias(Apothecary.class));

        // Test Cheerleaders
        assertTrue(com.bloodbowlclub.lib.io.serialization.resolvers.TeamStaffTypeIdResolver
            .isRegistered(Cheerleaders.class));
        assertEquals("staff.cheerleaders",
            com.bloodbowlclub.lib.io.serialization.resolvers.TeamStaffTypeIdResolver
                .getAlias(Cheerleaders.class));

        // Test CoachAssistant
        assertTrue(com.bloodbowlclub.lib.io.serialization.resolvers.TeamStaffTypeIdResolver
            .isRegistered(CoachAssistant.class));
        assertEquals("staff.coach.assistant",
            com.bloodbowlclub.lib.io.serialization.resolvers.TeamStaffTypeIdResolver
                .getAlias(CoachAssistant.class));
    }
}
