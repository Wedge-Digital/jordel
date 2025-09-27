package com.auth.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.aion.utils.Result;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("with-keycloak")
public class KeycloakChecker extends AbstractUserChecker{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(KeycloakChecker.class);

    @Value("${keycloak.server}")
    private String keycloakServer;

    private String introspectURL = "/auth/realms/TD-dev/protocol/openid-connect/token/introspect";

    @Value("${keycloak.client_id}")
    private String clientID;

    @Value("${keycloak.client_secret}")
    private String clientSecret;

    private String getFullIntrospectionUrl() {
        return keycloakServer + introspectURL;
    }

    public static String[] convertJsonNodeToStringArray(JsonNode jsonNode) {
        List<String> stringList = new ArrayList<>();
        jsonNode.forEach(node -> stringList.add(node.asText()));
        return stringList.toArray(new String[0]);
    }

    private Result<HttpURLConnection> connectToKeyCloak() {
        logger.info("Connecting to KeyCloak at " + getFullIntrospectionUrl());
        URL url;
        try {
            url = new URL(getFullIntrospectionUrl());
            logger.info("keycloak connexion Ok.");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        }
        catch (Exception e) {
            return Result.failure(e.getMessage());
        }
        return Result.success(conn);
    }

    private String buildPayload(String token) {
        return "token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
                + "&client_id=" + URLEncoder.encode(clientID, StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8);
    }

    private Result<KeycloakUser> getUserDetailsFromKeyCloak(HttpURLConnection conn, String token) {

        String body = buildPayload(token);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            return Result.failure(e.getMessage());
        }

        try {
            if (conn.getResponseCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode response = mapper.readTree(conn.getInputStream());
                logger.info("Response from KeyCloak: ");
                logger.info(response.toString());
                if (!response.get("active").asBoolean()) {
                    return Result.failure("Invalid or incorrect token, aborting.");
                }
                String usrName = "";
                String usrLastName = "";
                String usrGivenName = "";
                JsonNode node = response.get("username");
                if (node != null) {
                    usrName = node.asText();
                }

                node = response.get("given_name");
                if (node != null) {
                    usrLastName = node.asText();
                }

                node = response.get("family_name");
                if (node != null) {
                    usrGivenName = node.asText();
                }

                KeycloakUser found = new KeycloakUser(
                        usrName,
                        usrLastName,
                        usrGivenName,
                        response.get("sub").asText(),
                        convertJsonNodeToStringArray(response.get("resource_access").get("portal").get("roles"))
                );
                return Result.success(found);
            } else {
                return Result.failure("Introspection failed with HTTP code: " + conn.getResponseCode());
            }
        }
        catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @Override
    public Result<KeycloakUser> getUserInfosFromToken(String token) {

        Result<HttpURLConnection> keycloakConnection = connectToKeyCloak();
        if (keycloakConnection.isFailure()) {
            return Result.failure(keycloakConnection.getErrorMessage());
        }

        HttpURLConnection kcConnection = keycloakConnection.getValue();

        return getUserDetailsFromKeyCloak(kcConnection, token);
    }


}
