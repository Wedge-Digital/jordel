package com.bloodbowlclub.team_building.use_cases;

import com.bloodbowlclub.lib.Command;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.TranslatableMessage;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import com.bloodbowlclub.team_building.domain.commands.ChooseRulesetCommand;
import com.bloodbowlclub.team_building.domain.team.DraftTeam;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("chooseRulesetCommandHandler")
public class ChooseRulesetCommandHandler extends CommandHandler {

    public ChooseRulesetCommandHandler(@Qualifier("eventStore") EventStore eventStore,
                                       AbstractEventDispatcher businessDispatcher) {
        super(eventStore, businessDispatcher);
    }

    @Override
    public ResultMap<Void> handle(Command command) {
        ChooseRulesetCommand chooseRulesetCommand = (ChooseRulesetCommand) command;

        // Charger l'équipe depuis l'EventStore
        Result<AggregateRoot> teamResult = eventStore.findTeam(chooseRulesetCommand.getTeamId());

        if (teamResult.isFailure()) {
            return ResultMap.failure(
                    "team",
                    new TranslatableMessage("team_creation.team_not_found"),
                    ErrorCode.NOT_FOUND
            );
        }

        AggregateRoot aggregate = teamResult.getValue();

        // Vérifier que l'équipe est au moins une DraftTeam
        if (!(aggregate instanceof DraftTeam)) {
            return ResultMap.failure(
                    "team",
                    new TranslatableMessage("team_creation.team_not_in_draft_state"),
                    ErrorCode.UNPROCESSABLE_ENTITY
            );
        }

        DraftTeam draftTeam = (DraftTeam) aggregate;

        // Appeler la méthode métier pour sélectionner le ruleset
        ResultMap<Void> selectResult = draftTeam.selectCreationRuleset(chooseRulesetCommand.getRuleset());

        if (selectResult.isFailure()) {
            return selectResult;
        }

        // Sauvegarder et dispatcher les événements
        saveAndDispatch(draftTeam.domainEvents());

        return selectResult;
    }
}
