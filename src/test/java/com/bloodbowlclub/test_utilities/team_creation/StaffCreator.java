package com.bloodbowlclub.test_utilities.team_creation;

import com.bloodbowlclub.team_building.domain.team_staff.StaffID;
import com.bloodbowlclub.team_building.domain.team_staff.StaffMaxQuantity;
import com.bloodbowlclub.team_building.domain.team_staff.StaffName;
import com.bloodbowlclub.team_building.domain.team_staff.StuffPrice;
import com.bloodbowlclub.team_building.domain.team_staff.staff_detail.Apothecary;
import com.bloodbowlclub.team_building.domain.team_staff.staff_detail.Cheerleaders;
import com.bloodbowlclub.team_building.domain.team_staff.staff_detail.CoachAssistant;

public class StaffCreator {

    public Cheerleaders createCheerleaders() {
        return Cheerleaders.builder()
                .staffId(new StaffID("01KD333HSX1F82N7XPJ3S14YKH"))
                .name(new StaffName("Cheerleaders"))
                .price(new StuffPrice(10))
                .maxQuantity(new StaffMaxQuantity(6))
                .build();
    }

    public CoachAssistant createCoachAssistant() {
        return CoachAssistant.builder()
                .staffId(new StaffID("01KD3P89ZPCCQ1TKW26C8QCMW2"))
                .name(new StaffName("CoachAssistant"))
                .price(new StuffPrice(10))
                .maxQuantity(new StaffMaxQuantity(6))
                .build();
    }

    public Apothecary createApothecary() {
        return Apothecary.builder()
                .staffId(new StaffID("01KD3PDE0W0P8EXM72KKN20RF6"))
                .name(new StaffName("Apothecary"))
                .price(new StuffPrice(50))
                .maxQuantity(new StaffMaxQuantity(1))
                .build();
    }
}
