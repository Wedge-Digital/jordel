package com.bloodbowlclub.team_building.io.web;

import com.bloodbowlclub.lib.persistance.event_store.fake.FakeEventStore;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.lib.web.ApiResponse;
import com.bloodbowlclub.team_building.io.web.requests.RegisterNewTeamRequest;
import com.bloodbowlclub.team_building.use_cases.RegisterNewTeamCommandHandler;
import com.bloodbowlclub.test_utilities.dispatcher.FakeEventDispatcher;
import com.bloodbowlclub.test_utilities.ulid.UlidGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests pour valider le contenu JSON des réponses du TeamBuildingController
 * en utilisant la méthode assertEqualsResultset (golden file pattern)
 */
public class TeamBuildingControllerJsonResponseTest extends TestCase {


    FakeEventStore fakeEventStore = new FakeEventStore();
    FakeEventDispatcher eventDispatcher = new FakeEventDispatcher();
    private RegisterNewTeamCommandHandler cmdHandler = new RegisterNewTeamCommandHandler(fakeEventStore, eventDispatcher);
    private TeamBuildingController ctrl = new TeamBuildingController(cmdHandler, messageSource);

    @Test
    @Transactional
    @DisplayName("Register new team should return success JSON response - validated with assertEqualsResultset")
    void test_register_new_team_returns_success_json_response() throws Exception {
        // Arrange
        String teamId = UlidGenerator.generate();
        RegisterNewTeamRequest request = RegisterNewTeamRequest.builder()
                .teamId(teamId)
                .teamName("Les Ours de la Mort")
                .teamLogo("https://res.cloudinary.com/bloodbowlclub-com/image/upload/v1659445677/user_uploads/x44ke8sgvqrap91mry8i.jpg")
                .coachId("01KEPP269QA4Z021RCXQTFNPSF")
                .build();

        ResponseEntity<ApiResponse<Void>> entity = ctrl.registerNewTeam(request);


        assertEqualsResultset(entity.getBody());
    }

//    @Test
//    @Transactional
//    @DisplayName("Register new team with invalid name should return failure JSON with errors")
//    void test_register_new_team_with_invalid_name_returns_failure_json() throws Exception {
//        // Arrange - Nom trop court (min 3 caractères requis)
//        String teamId = UlidGenerator.generate();
//        RegisterNewTeamRequest request = RegisterNewTeamRequest.builder()
//                .teamId(teamId)
//                .teamName("AB") // Trop court
//                .teamLogo("https://res.cloudinary.com/demo/image/upload/sample.jpg")
//                .build();
//
//        String requestBody = objectMapper.writeValueAsString(request);
//
//        // Act
//        MvcResult result = mockMvc.perform(post("/team-building/v1/teams")
//                        .header("Accept-Language", "fr-FR")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isUnprocessableEntity())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andReturn();
//
//        // Parse la réponse JSON
//        String responseJson = result.getResponse().getContentAsString();
//        Object responseObject = objectMapper.readValue(responseJson, Object.class);
//
//        // Assert - Compare avec le golden file
//        // IMPORTANT: Le message d'erreur est localisé, donc il faudra valider
//        // que le fichier golden contient le bon message en français
//        assertEqualsResultset(responseObject);
//    }
//
//    @Test
//    @Transactional
//    @DisplayName("Register new team with invalid ULID should return failure JSON with teamId error")
//    void test_register_new_team_with_invalid_ulid_returns_failure_json() throws Exception {
//        // Arrange - ULID invalide
//        RegisterNewTeamRequest request = RegisterNewTeamRequest.builder()
//                .teamId("invalid-ulid-123")
//                .teamName("Valid Team Name")
//                .teamLogo("https://res.cloudinary.com/demo/image/upload/sample.jpg")
//                .build();
//
//        String requestBody = objectMapper.writeValueAsString(request);
//
//        // Act
//        MvcResult result = mockMvc.perform(post("/team-building/v1/teams")
//                        .header("Accept-Language", "en-US")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isUnprocessableEntity())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andReturn();
//
//        // Parse la réponse JSON
//        String responseJson = result.getResponse().getContentAsString();
//        Object responseObject = objectMapper.readValue(responseJson, Object.class);
//
//        // Assert - Compare avec le golden file (en anglais cette fois)
//        assertEqualsResultset(responseObject);
//    }
//
//    @Test
//    @Transactional
//    @DisplayName("Register new team with invalid Cloudinary URL should return failure JSON with logoUrl error")
//    void test_register_new_team_with_invalid_cloudinary_url_returns_failure_json() throws Exception {
//        // Arrange - URL Cloudinary invalide
//        String teamId = UlidGenerator.generate();
//        RegisterNewTeamRequest request = RegisterNewTeamRequest.builder()
//                .teamId(teamId)
//                .teamName("Valid Team Name")
//                .teamLogo("https://example.com/not-cloudinary.jpg")
//                .build();
//
//        String requestBody = objectMapper.writeValueAsString(request);
//
//        // Act
//        MvcResult result = mockMvc.perform(post("/team-building/v1/teams")
//                        .header("Accept-Language", "fr-FR")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isUnprocessableEntity())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andReturn();
//
//        // Parse la réponse JSON
//        String responseJson = result.getResponse().getContentAsString();
//        Object responseObject = objectMapper.readValue(responseJson, Object.class);
//
//        // Assert - Compare avec le golden file
//        assertEqualsResultset(responseObject);
//    }
//
//    @Test
//    @Transactional
//    @DisplayName("Register new team with multiple validation errors should return all errors in JSON")
//    void test_register_new_team_with_multiple_errors_returns_all_errors_in_json() throws Exception {
//        // Arrange - Plusieurs erreurs de validation
//        RegisterNewTeamRequest request = RegisterNewTeamRequest.builder()
//                .teamId("invalid-ulid")
//                .teamName("") // Vide
//                .teamLogo("not-a-valid-url")
//                .build();
//
//        String requestBody = objectMapper.writeValueAsString(request);
//
//        // Act
//        MvcResult result = mockMvc.perform(post("/team-building/v1/teams")
//                        .header("Accept-Language", "fr-FR")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isUnprocessableEntity())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                // On peut aussi valider la structure avec jsonPath
//                .andExpect(jsonPath("$.result").value("failure"))
//                .andExpect(jsonPath("$.content").isNotEmpty())
//                .andReturn();
//
//        // Parse la réponse JSON
//        String responseJson = result.getResponse().getContentAsString();
//        Object responseObject = objectMapper.readValue(responseJson, Object.class);
//
//        // Assert - Compare avec le golden file qui doit contenir toutes les erreurs
//        assertEqualsResultset(responseObject);
//    }
}