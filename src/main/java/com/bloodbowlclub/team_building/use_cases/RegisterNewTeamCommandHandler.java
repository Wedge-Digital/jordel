package com.bloodbowlclub.team_building.use_cases;

import com.bloodbowlclub.lib.Command;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import com.bloodbowlclub.team_building.domain.commands.RegisterNewTeamCommand;
import com.bloodbowlclub.team_building.domain.team.BaseTeam;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("registerNewTeamCommandHandler")
public class RegisterNewTeamCommandHandler extends CommandHandler {

    public RegisterNewTeamCommandHandler(@Qualifier("eventStore") EventStore eventStore,
                                         AbstractEventDispatcher businessDispatcher) {
        super(eventStore, businessDispatcher);
    }

    @Override
    public ResultMap<Void> handle(Command command) {
        RegisterNewTeamCommand registerCommand = (RegisterNewTeamCommand) command;

        BaseTeam newTeam = new BaseTeam();
        ResultMap<Void> registerResult = newTeam.registerNewTeam(registerCommand);

        if (registerResult.isFailure()) {
            return registerResult;
        }

        saveAndDispatch(newTeam.domainEvents());

        return registerResult;
    }
}