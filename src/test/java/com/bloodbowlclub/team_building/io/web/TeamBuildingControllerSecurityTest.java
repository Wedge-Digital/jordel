package com.bloodbowlclub.team_building.io.web;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.domain.user_account.events.EmailValidatedEvent;
import com.bloodbowlclub.auth.io.services.JwtService;
import com.bloodbowlclub.auth.io.web.UserTestUtils;
import com.bloodbowlclub.lib.persistance.event_store.EventEntity;
import com.bloodbowlclub.lib.persistance.event_store.EventEntityFactory;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.team_building.io.web.requests.RegisterNewTeamRequest;
import com.bloodbowlclub.test_utilities.ulid.UlidGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TeamBuildingControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EventStore eventStore;

    private final EventEntityFactory factory = new EventEntityFactory();

    @Test
    @Transactional
    @DisplayName("Creating a team without authentication should return 401 Unauthorized")
    void test_create_team_without_auth_fails() throws Exception {
        // Arrange
        RegisterNewTeamRequest request = RegisterNewTeamRequest.builder()
                .teamId(UlidGenerator.generate())
                .teamName("Test Team")
                .teamLogo("https://res.cloudinary.com/demo/image/upload/sample.jpg")
                .build();

        String requestBody = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/team-building/v1/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @DisplayName("Creating a team with valid authentication should succeed")
    void test_create_team_with_auth_succeeds() throws Exception {
        // Arrange - Create an active user
        String username = "testuser";
        BaseUserAccount userAccount = UserTestUtils.createUser(username, eventStore);

        // Validate email to make user active
        EmailValidatedEvent emailValidatedEvent = new EmailValidatedEvent(userAccount);
        EventEntity eventEntity = factory.build(emailValidatedEvent);
        eventStore.save(eventEntity);

        // Generate JWT token
        String accessToken = jwtService.buildAuthTokens(username).getAccessToken();

        RegisterNewTeamRequest request = RegisterNewTeamRequest.builder()
                .teamId(UlidGenerator.generate())
                .teamName("Authenticated Team")
                .teamLogo("https://res.cloudinary.com/demo/image/upload/sample.jpg")
                .coachId("01KEPP68Z376XXA7VRT9WCW7VA")
                .build();

        String requestBody = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/team-building/v1/teams")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @DisplayName("Creating a team with invalid JWT token should return 401 Unauthorized")
    void test_create_team_with_invalid_token_fails() throws Exception {
        // Arrange
        String invalidToken = "invalid.jwt.token";

        RegisterNewTeamRequest request = RegisterNewTeamRequest.builder()
                .teamId(UlidGenerator.generate())
                .teamName("Test Team")
                .teamLogo("https://res.cloudinary.com/demo/image/upload/sample.jpg")
                .build();

        String requestBody = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/team-building/v1/teams")
                        .header("Authorization", "Bearer " + invalidToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }
}
