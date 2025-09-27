package com.auth.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.td.aion.io.repositories.user.UserEntity;
import com.td.aion.io.repositories.user.UserRole;
import com.td.aion.utils.Result;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class KeycloakUser {

    private String username;

    @JsonProperty("given_name")
    private String firstName;

    @JsonProperty("family_name")
    private String lastName;

    @JsonProperty("sub")
    private String externalId;

    @JsonProperty("role")
    private String[] roles;

    public KeycloakUser(String username, String firstName, String lastName, String externalId, String[] roles) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.externalId = externalId;
//        this.customerId = customerId;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public String getExternalId() {
        return externalId;
    }

    public String[] getRoles() {
        return roles;
    }

    public String getCheckSum() {
        return this.computeChecksum().getValue();
    }

    private boolean stringMatchesUserRoles(String toMatch) {
        try {
            UserRole.valueOf(toMatch);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public UserEntity toNewUserEntity(String newUserEntityId) {
        UserEntity userEntity = new UserEntity(
                newUserEntityId,
                username,
                firstName,
                lastName,
                externalId,
                this.computeChecksum().getValue(),
                null
        );
        userEntity.activate();
        for (String r : this.roles) {
            if (stringMatchesUserRoles(r)) {
                userEntity.addRole(r);
            }
        }
        return userEntity;
    }

    private String concatAllRoles() {
        Arrays.sort(roles);

        String res = "";
        for (String r : roles) {
            if (stringMatchesUserRoles(r)) {
                res += r;
            }
        }
        // Concaténer les éléments du tableau trié
        return res;
    }

    public Result<String> computeChecksum() {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            return Result.failure("SHA-256 not available");
        }
        Field[] fields = this.getClass().getDeclaredFields();

        String asString = "";

        // Parcourir chaque champ et mettre à jour le digest
        for (Field field : fields) {
            Object value = null;
            try {
                value = field.get(this);
            } catch (IllegalAccessException e) {
                return Result.failure("Illegal access to field " + field.getName());
            }

            if (field.getName().equals("roles")) {
                value = concatAllRoles();
            }

            // Convertir la valeur en chaîne et mettre à jour le digest
            if (value != null) {
                String stringValue = value.toString();
                asString += stringValue;
            }
        }
        digest.update(asString.getBytes(StandardCharsets.UTF_8));
        // Obtenir le checksum sous forme de tableau de bytes
        byte[] hashBytes = digest.digest();

        // Convertir le tableau de bytes en une chaîne hexadécimale
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return Result.success(hexString.toString());
    }
}
