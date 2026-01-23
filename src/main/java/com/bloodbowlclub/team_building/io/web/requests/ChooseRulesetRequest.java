package com.bloodbowlclub.team_building.io.web.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChooseRulesetRequest {

    @NotBlank(message = "{team.id.required}")
    private String teamId;

    @NotBlank(message = "{ruleset.id.required}")
    private String rulesetId;
}
