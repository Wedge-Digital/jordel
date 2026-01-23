package com.bloodbowlclub.ruleset_building.domain;

import com.bloodbowlclub.shared.ruleset.RulesetID;
import com.bloodbowlclub.shared.ruleset.RulesetName;
import jakarta.validation.Valid;
import lombok.Builder;

import java.util.List;

@Builder
public class Ruleset {
    @Valid
    private RulesetID id;

    @Valid
    private RulesetName name;

    @Valid
    private List<Tier> tierList;
}
