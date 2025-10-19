package com.auth;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private JwtService jwtService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private final DateService dateService = new DateService();
//
//    @BeforeEach
//    void setup() {
//    }
//
//    private void assertThatResponseContainsValidTokens(MvcResult result) throws JsonProcessingException, UnsupportedEncodingException {
//        String responseContent = result.getResponse().getContentAsString();
//        JwtTokens response = objectMapper.readValue(responseContent, JwtTokens.class);
//
//        // vérifier que les deux tokens jwt sont valides
//        Assertions.assertTrue(jwtService.validateJwtToken(response.getAccessToken()).isSuccess());
//        Assertions.assertTrue(jwtService.validateJwtToken(response.getRefreshToken()).isSuccess());
//    }
//
//    private String buildExpiredTokens(String username) {
//        Date issuedAt = dateService.dateTimeFromMysql("2023-01-01 00:00:00").getValue();
//        Date expireAt = dateService.dateTimeFromMysql("2023-02-01 00:00:00").getValue();
//        return jwtService.generateToken(username, issuedAt, expireAt );
//    }
//
//    @Test
//    void testLoginSucceed() throws Exception {
//        LoginRequest payload = new LoginRequest("user_one", "password");
//
//        MvcResult result;
//        result = mockMvc.perform(post("/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Accept-Language", "fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7")
//                        .content(objectMapper.writeValueAsString(payload)))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.size()").value(2))
//                        .andExpect(jsonPath("$.accessToken").isNotEmpty())
//                        .andExpect(jsonPath("$.refreshToken").isNotEmpty())
//                        .andReturn();
//
//        // Extraire le contenu de la réponse
//        assertThatResponseContainsValidTokens(result);
//    }
//
//    @Test
//    void testLoginWithBadPasswordFails() throws Exception {
//        LoginRequest payload = new LoginRequest("user_one", "password_bad");
//
//        MvcResult result;
//        mockMvc.perform(post("/authenticate")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(payload)))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    void testExtendAccessTokenFromValidRefreshTokenSucceed() throws Exception {
//        JwtTokens validTokens = jwtService.buildAuthTokens("user_one");
//        RefreshTokenRequest payload = new RefreshTokenRequest(validTokens.getRefreshToken());
//
//        MvcResult result;
//        result = mockMvc.perform(post("/refresh")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(payload)))
//                .andExpect(status().isOk()).andReturn();
//
//        // Extraire le contenu de la réponse
//        assertThatResponseContainsValidTokens(result);
//    }
//
//    @Test
//    void testExtendAccessTokenFromExpiredRefreshTokenFails() throws Exception {
//        String expiredToken = buildExpiredTokens("user_one");
//        RefreshTokenRequest payload = new RefreshTokenRequest(expiredToken);
//
//        mockMvc.perform(post("/refresh")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(payload)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testTryToGetMeWithoutJwtTokenFails() throws Exception {
//        mockMvc.perform(get("/me"))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void testGetMeWithMalformedHeadersReturnsBadRequest() throws Exception {
//        String authToken = "your_jwt_token_here";
//        mockMvc.perform(get("/me")
//                        .header("Authorization", authToken))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testGetMeWithMalformedTokenReturnsBadRequest() throws Exception {
//        String authToken = "Bearer your_jwt_token_here";
//        mockMvc.perform(get("/me")
//                        .header("Authorization", authToken))
//                .andExpect(status().isBadRequest());
//
//    }
//
//    @Test
//    void testGetMeWithExpiredTokenFails() throws Exception {
//        String expiredToken = buildExpiredTokens("user_two");
//        String authToken = String.format("Bearer %s", expiredToken);
//        mockMvc.perform(get("/me")
//                        .header("Authorization", authToken))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void testGetMeWithValidTokenAndUnknownUserRaisesUnauthorized() throws Exception {
//        JwtTokens expiredToken = jwtService.buildAuthTokens("toto l'asticot");
//        String authToken = String.format("Bearer %s", expiredToken.getAccessToken());
//        mockMvc.perform(get("/me")
//                        .header("Authorization", authToken))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void testGetMeWithValidTokenAndKnownUserRetrieveUserDetails() throws Exception {
//        JwtTokens expiredToken = jwtService.buildAuthTokens("user_two");
//        String authToken = String.format("Bearer %s", expiredToken.getAccessToken());
//
//        MvcResult result;
//        result = mockMvc.perform(get("/me")
//                .header("Authorization", authToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(7)).andReturn();
//
//        String responseContent = result.getResponse().getContentAsString();
//        CustomUser connectedUser = objectMapper.readValue(responseContent, CustomUser.class);
//
//        Assertions.assertEquals("user_two", connectedUser.getUsername());
//        Assertions.assertTrue(connectedUser.isAccountNonExpired());
//    }
//

}
