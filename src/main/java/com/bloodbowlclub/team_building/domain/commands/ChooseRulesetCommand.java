package com.bloodbowlclub.team_building.domain.commands;

import com.bloodbowlclub.lib.Command;
import com.bloodbowlclub.team_building.domain.ruleset.Ruleset;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChooseRulesetCommand implements Command {
    String teamId;
    Ruleset ruleset;
}
