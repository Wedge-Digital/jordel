package com.bloodbowlclub.shared.ref_data;

import com.bloodbowlclub.shared.roster.RosterID;
import com.bloodbowlclub.shared.roster.RosterName;
import com.bloodbowlclub.team_building.domain.roster.*;
import com.bloodbowlclub.team_building.domain.team_staff.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Deserializer personnalisé pour convertir le JSON de référence en objets Roster du domaine.
 * Mappe les champs JSON vers les Value Objects du domaine.
 */
public class RosterDeserializer extends StdDeserializer<Roster> {

    private Map<String, TeamStaff> staffByUid;

    public RosterDeserializer() {
        super(Roster.class);
        this.staffByUid = Collections.emptyMap();
    }

    public RosterDeserializer(Map<String, TeamStaff> staffByUid) {
        super(Roster.class);
        this.staffByUid = staffByUid != null ? staffByUid : Collections.emptyMap();
    }

    @Override
    public Roster deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        // Mapper les champs JSON vers les Value Objects
        RosterID rosterId = new RosterID(node.get("uid").asText());
        RosterName name = new RosterName(node.get("name").asText());
        RerollBasePrice rerollPrice = new RerollBasePrice(node.get("rerollCost").asInt() / 1000); // Convertir de gold en k gold

        // Mapper les joueurs disponibles
        List<PlayerDefinition> playerDefinitions = new ArrayList<>();
        JsonNode playersNode = node.get("availablePlayers");
        if (playersNode != null && playersNode.isArray()) {
            for (JsonNode playerNode : playersNode) {
                PlayerDefinition player = PlayerDefinition.builder()
                        .playerDefinitionID(new PlayerDefinitionID(playerNode.get("uid").asText()))
                        .name(new PlayerDefinitionName(playerNode.get("positionName").asText()))
                        .price(new PlayerPrice(playerNode.get("cost").asInt() / 1000)) // Convertir de gold en k gold
                        .maxQuantity(new PlayerMaxQuantity(playerNode.get("max_quantity").asInt()))
                        .build();
                playerDefinitions.add(player);
            }
        }

        // Mapper les cross limits (support des deux formats: "max"/"in" et "limit"/"limitedPlayerIds")
        List<CrossLimit> crossLimits = new ArrayList<>();
        JsonNode crossLimitsNode = node.get("cross_limit");
        if (crossLimitsNode != null && crossLimitsNode.isArray()) {
            for (JsonNode limitNode : crossLimitsNode) {
                List<String> limitedPlayerIds = new ArrayList<>();

                // Support du format "in" ou "limitedPlayerIds"
                JsonNode idsNode = limitNode.get("in");
                if (idsNode == null) {
                    idsNode = limitNode.get("limitedPlayerIds");
                }

                if (idsNode != null && idsNode.isArray()) {
                    for (JsonNode idNode : idsNode) {
                        limitedPlayerIds.add(idNode.asText());
                    }
                }

                // Support du format "max" ou "limit"
                int maxValue;
                JsonNode maxNode = limitNode.get("max");
                if (maxNode != null) {
                    maxValue = maxNode.asInt();
                } else {
                    maxValue = limitNode.get("limit").asInt();
                }

                CrossLimit crossLimit = new CrossLimit(maxValue, limitedPlayerIds);
                crossLimits.add(crossLimit);
            }
        }

        // Mapper les staff autorisés
        List<TeamStaff> allowedTeamStaff = new ArrayList<>();
        JsonNode staffNode = node.get("allowedStaff");
        if (staffNode != null && staffNode.isArray()) {
            for (JsonNode staffUidNode : staffNode) {
                String staffUid = staffUidNode.asText();
                TeamStaff staffData = staffByUid.get(staffUid);

                if (staffData != null) {
                    // Utiliser directement l'objet TeamStaff du cache de référence
                    allowedTeamStaff.add(staffData);
                }
            }
        }

        return Roster.builder()
                .rosterId(rosterId)
                .name(name)
                .playerDefinitions(playerDefinitions)
                .allowedTeamStaff(allowedTeamStaff)
                .crossLimits(crossLimits)
                .rerollPrice(rerollPrice)
                .build();
    }
}
