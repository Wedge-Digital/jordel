package com.bloodbowlclub.team_building.io.web;

import com.bloodbowlclub.lib.persistance.event_store.EventEntity;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.team_building.domain.events.DraftTeamRegisteredEvent;
import com.bloodbowlclub.team_building.io.web.requests.RegisterNewTeamRequest;
import com.bloodbowlclub.test_utilities.ulid.UlidGenerator;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;

@SpringBootTest
public class TeamBuildingControllerTest {

    @Autowired
    private TeamBuildingController teamBuildingController;

    @Autowired
    private EventStore eventStore;

    @Test
    @Transactional
    @DisplayName("Register new team with valid data should succeed and save event in event store")
    void test_register_new_team_succeeds_and_saves_event() {
        // Arrange
        String teamId = UlidGenerator.generate();
        RegisterNewTeamRequest request = RegisterNewTeamRequest.builder()
                .teamId(teamId)
                .teamName("Les Ours de la Mort")
                .teamLogo("https://res.cloudinary.com/demo/image/upload/sample.jpg")
                .build();

        // Count events before
        int eventsCountBefore = eventStore.findAll().size();

        // Act
        ResponseEntity<Void> response = teamBuildingController.registerNewTeam(request);

        // Assert - HTTP Response
        Assertions.assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        // Assert - Event saved in event store
        List<EventEntity> allEvents = eventStore.findAll();
        Assertions.assertEquals(eventsCountBefore + 1, allEvents.size(),
            "One event should have been saved in event store");

        // Assert - Event is a DraftTeamRegisteredEvent
        List<EventEntity> teamEvents = eventStore.findBySubject(teamId);
        Assertions.assertEquals(1, teamEvents.size(),
            "One event should exist for this team");

        EventEntity savedEvent = teamEvents.get(0);
        Assertions.assertTrue(savedEvent.getData() instanceof DraftTeamRegisteredEvent,
            "Saved event should be a DraftTeamRegisteredEvent");

        DraftTeamRegisteredEvent draftEvent = (DraftTeamRegisteredEvent) savedEvent.getData();
        Assertions.assertNotNull(draftEvent.getTeam(), "Team should not be null in event");
        Assertions.assertEquals(teamId, draftEvent.getTeam().getTeamId().toString());
        Assertions.assertEquals("Les Ours de la Mort", draftEvent.getTeam().getName().toString());
        Assertions.assertEquals("https://res.cloudinary.com/demo/image/upload/sample.jpg",
            draftEvent.getTeam().getLogoUrl().toString());
    }

    @Test
    @Transactional
    @DisplayName("Register new team with invalid data should fail")
    void test_register_new_team_with_invalid_data_fails() {
        // Arrange - Invalid team name (too short)
        String teamId = UlidGenerator.generate();
        RegisterNewTeamRequest request = RegisterNewTeamRequest.builder()
                .teamId(teamId)
                .teamName("AB") // Too short
                .teamLogo("https://res.cloudinary.com/demo/image/upload/sample.jpg")
                .build();

        // Act & Assert - Should throw UnprocessableEntity
        try {
            teamBuildingController.registerNewTeam(request);
            Assertions.fail("Should have thrown UnprocessableEntity");
        } catch (com.bloodbowlclub.lib.services.result.exceptions.UnprocessableEntity e) {
            // Expected exception
            Assertions.assertNotNull(e.getErrors());
            Assertions.assertTrue(e.getErrors().containsKey("name"),
                "Should have validation error for name");
        }

        // Assert - No event saved in event store
        List<EventEntity> teamEvents = eventStore.findBySubject(teamId);
        Assertions.assertEquals(0, teamEvents.size(),
            "No event should have been saved for invalid team");
    }

    @Test
    @Transactional
    @DisplayName("Register new team with invalid ULID should fail")
    void test_register_new_team_with_invalid_ulid_fails() {
        // Arrange - Invalid ULID
        RegisterNewTeamRequest request = RegisterNewTeamRequest.builder()
                .teamId("invalid-ulid-123")
                .teamName("Valid Team Name")
                .teamLogo("https://res.cloudinary.com/demo/image/upload/sample.jpg")
                .build();

        // Act & Assert - Should throw UnprocessableEntity
        try {
            teamBuildingController.registerNewTeam(request);
            Assertions.fail("Should have thrown UnprocessableEntity");
        } catch (com.bloodbowlclub.lib.services.result.exceptions.UnprocessableEntity e) {
            // Expected exception
            Assertions.assertNotNull(e.getErrors());
            Assertions.assertTrue(e.getErrors().containsKey("teamId"),
                "Should have validation error for teamId");
        }

        // Assert - No event saved in event store
        List<EventEntity> teamEvents = eventStore.findBySubject("invalid-ulid-123");
        Assertions.assertEquals(0, teamEvents.size(),
            "No event should have been saved for invalid ULID");
    }

    @Test
    @Transactional
    @DisplayName("Register new team with invalid Cloudinary URL should fail")
    void test_register_new_team_with_invalid_cloudinary_url_fails() {
        // Arrange - Invalid Cloudinary URL
        String teamId = UlidGenerator.generate();
        RegisterNewTeamRequest request = RegisterNewTeamRequest.builder()
                .teamId(teamId)
                .teamName("Valid Team Name")
                .teamLogo("https://example.com/not-cloudinary.jpg")
                .build();

        // Act & Assert - Should throw UnprocessableEntity
        try {
            teamBuildingController.registerNewTeam(request);
            Assertions.fail("Should have thrown UnprocessableEntity");
        } catch (com.bloodbowlclub.lib.services.result.exceptions.UnprocessableEntity e) {
            // Expected exception
            Assertions.assertNotNull(e.getErrors());
            Assertions.assertTrue(e.getErrors().containsKey("logoUrl"),
                "Should have validation error for logoUrl");
        }

        // Assert - No event saved in event store
        List<EventEntity> teamEvents = eventStore.findBySubject(teamId);
        Assertions.assertEquals(0, teamEvents.size(),
            "No event should have been saved for invalid Cloudinary URL");
    }
}
