package com.bloodbowlclub.test_utilities.team_creation;

import com.bloodbowlclub.team_building.domain.team_stuff.StuffID;
import com.bloodbowlclub.team_building.domain.team_stuff.StuffMaxQuantity;
import com.bloodbowlclub.team_building.domain.team_stuff.StuffName;
import com.bloodbowlclub.team_building.domain.team_stuff.StuffPrice;
import com.bloodbowlclub.team_building.domain.team_stuff.stuff_detail.Apothecary;
import com.bloodbowlclub.team_building.domain.team_stuff.stuff_detail.Cheerleaders;
import com.bloodbowlclub.team_building.domain.team_stuff.stuff_detail.CoachAssistant;

public class StaffCreator {

    public Cheerleaders createCheerleaders() {
        return Cheerleaders.builder()
                .stuffId(new StuffID("01KD333HSX1F82N7XPJ3S14YKH"))
                .name(new StuffName("Cheerleaders"))
                .price(new StuffPrice(10))
                .maxQuantity(new StuffMaxQuantity(6))
                .build();
    }

    public CoachAssistant createCoachAssistant() {
        return CoachAssistant.builder()
                .stuffId(new StuffID("01KD3P89ZPCCQ1TKW26C8QCMW2"))
                .name(new StuffName("CoachAssistant"))
                .price(new StuffPrice(10))
                .maxQuantity(new StuffMaxQuantity(6))
                .build();
    }

    public Apothecary createApothecary() {
        return Apothecary.builder()
                .stuffId(new StuffID("01KD3PDE0W0P8EXM72KKN20RF6"))
                .name(new StuffName("Apothecary"))
                .price(new StuffPrice(50))
                .maxQuantity(new StuffMaxQuantity(1))
                .build();
    }
}
