package com.bloodbowlclub.team_building.domain.commands;

import com.bloodbowlclub.lib.Command;
import com.bloodbowlclub.team_building.domain.Roster;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChoosenRosterCommand implements Command {
    Roster chosenRoster;
}
