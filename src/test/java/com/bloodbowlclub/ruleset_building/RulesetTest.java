package com.bloodbowlclub.ruleset_building;

import com.bloodbowlclub.reference.service.ReferenceDataService;
import com.bloodbowlclub.ruleset_building.domain.Ruleset;
import com.bloodbowlclub.ruleset_building.domain.Tier;
import com.bloodbowlclub.shared.ruleset.RulesetID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.util.List;
import java.util.Locale;

public class RulesetTest {

    ResourceLoader resourceLoader = new DefaultResourceLoader();

    ReferenceDataService refService = new ReferenceDataService(resourceLoader);

    @Test
    @DisplayName("Euro 2026 ruleset, shall be buildable")
    void ruleset_shall_contain_at_least_one_tier_list() {
    }

    @Test
    @DisplayName("StressCup 2026 ruleset, shall be buildable")
    void stress_cup_2026_shall_be_buildable() {
        // TODO: Complete this test when Ruleset builder is finalized
        // refService.loadAllReferenceData();

        // Tier tier1 = Tier.builder()
        //         .rosterList(refService.getAllRosters(Locale.getDefault()))
        //         .skillRules()
        //         .build();
        // Ruleset ruleset = Ruleset.builder()
        //         .id(new RulesetID("StressCup2026"))
        //         .tierList()
        //         .build();
    }

}
