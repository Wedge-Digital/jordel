package com.bloodbowlclub.team_building.domain.commands;

import com.bloodbowlclub.lib.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegisterNewTeamCommand implements Command {
        String teamId;
        String teamName;
        String teamLogo;
        String coachId;
}
