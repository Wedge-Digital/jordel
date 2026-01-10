package com.bloodbowlclub.team_building.ref_data;

import com.bloodbowlclub.team_building.domain.team_staff.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * Deserializer personnalisé pour convertir le JSON de référence en objets TeamStaff du domaine.
 * Mappe les champs JSON vers les Value Objects du domaine.
 */
public class TeamStaffDeserializer extends StdDeserializer<TeamStaff> {

    public TeamStaffDeserializer() {
        super(TeamStaff.class);
    }

    @Override
    public TeamStaff deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        // Mapper les champs JSON vers les Value Objects
        StaffID staffId = new StaffID(node.get("uid").asText());
        StaffName name = new StaffName(node.get("name").asText());
        StuffPrice price = new StuffPrice(node.get("price").asInt());
        StaffMaxQuantity maxQuantity = new StaffMaxQuantity(node.get("maxQuantity").asInt());

        return TeamStaff.builder()
                .staffId(staffId)
                .name(name)
                .price(price)
                .maxQuantity(maxQuantity)
                .build();
    }
}