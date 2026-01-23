package com.bloodbowlclub.team_building.io.web;

import com.bloodbowlclub.lib.services.TranslatableMessage;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.services.result.ResultToResponse;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import com.bloodbowlclub.lib.web.ApiResponse;
import com.bloodbowlclub.team_building.domain.commands.ChooseRulesetCommand;
import com.bloodbowlclub.team_building.domain.commands.RegisterNewTeamCommand;
import com.bloodbowlclub.team_building.io.web.requests.ChooseRulesetMapper;
import com.bloodbowlclub.team_building.io.web.requests.ChooseRulesetRequest;
import com.bloodbowlclub.team_building.io.web.requests.RegisterNewTeamMapper;
import com.bloodbowlclub.team_building.io.web.requests.RegisterNewTeamRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/team-building/v1")
public class TeamBuildingController {

    private final CommandHandler registerNewTeamHandler;
    private final CommandHandler chooseRulesetHandler;
    private final RegisterNewTeamMapper registerMapper = RegisterNewTeamMapper.INSTANCE;
    private final ChooseRulesetMapper chooseRulesetMapper;
    private final ResultToResponse<Void> commandConverter;

    public TeamBuildingController(
            @Qualifier("registerNewTeamCommandHandler") CommandHandler registerNewTeamHandler,
            @Qualifier("chooseRulesetCommandHandler") CommandHandler chooseRulesetHandler,
            ChooseRulesetMapper chooseRulesetMapper,
            MessageSource messageSource) {
        this.registerNewTeamHandler = registerNewTeamHandler;
        this.chooseRulesetHandler = chooseRulesetHandler;
        this.chooseRulesetMapper = chooseRulesetMapper;
        this.commandConverter = new ResultToResponse<>(messageSource);
    }

    @Operation(summary = "Enregistre une nouvelle équipe")
    @PostMapping("/teams")
    public ResponseEntity<ApiResponse<Void>> registerNewTeam(@Valid @RequestBody RegisterNewTeamRequest request) {
        RegisterNewTeamCommand command = registerMapper.requestToCommand(request);
        ResultMap<Void> result = registerNewTeamHandler.handle(command);
        return commandConverter.toResponse(result);
    }

    @Operation(summary = "Sélectionne un ruleset pour une équipe en brouillon")
    @PostMapping("/teams/{teamId}/ruleset")
    public ResponseEntity<ApiResponse<Void>> chooseRuleset(
            @PathVariable String teamId,
            @Valid @RequestBody ChooseRulesetRequest request) {

        // Vérifier que le teamId du path correspond à celui du body
        if (!teamId.equals(request.getTeamId())) {
            ResultMap<Void> error = ResultMap.failure(
                    "teamId",
                    new TranslatableMessage("team_creation.teamid_mismatch"),
                    ErrorCode.BAD_REQUEST
            );
            return commandConverter.toResponse(error);
        }

        // Convertir la requête en commande (charge le ruleset depuis le service)
        ChooseRulesetCommand command = chooseRulesetMapper.requestToCommand(request);

        // Si le ruleset n'est pas trouvé, retourner une erreur
        if (command == null) {
            ResultMap<Void> error = ResultMap.failure(
                    "rulesetId",
                    new TranslatableMessage("team_creation.ruleset_not_found"),
                    ErrorCode.NOT_FOUND
            );
            return commandConverter.toResponse(error);
        }

        // Exécuter la commande
        ResultMap<Void> result = chooseRulesetHandler.handle(command);
        return commandConverter.toResponse(result);
    }
}
