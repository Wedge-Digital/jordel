package com.bloodbowlclub.team_building.domain.commands;

import com.bloodbowlclub.lib.Command;
import com.bloodbowlclub.team_building.domain.roster.Roster;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChooseRosterCommand implements Command {
    Roster chosenRoster;
}
