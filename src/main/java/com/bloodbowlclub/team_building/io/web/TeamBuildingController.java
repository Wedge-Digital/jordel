package com.bloodbowlclub.team_building.io.web;

import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.services.result.ResultToResponse;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import com.bloodbowlclub.lib.web.ApiResponse;
import com.bloodbowlclub.team_building.domain.commands.RegisterNewTeamCommand;
import com.bloodbowlclub.team_building.io.web.requests.RegisterNewTeamMapper;
import com.bloodbowlclub.team_building.io.web.requests.RegisterNewTeamRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/team-building/v1")
public class TeamBuildingController {

    private final CommandHandler registerNewTeamHandler;
    private final RegisterNewTeamMapper mapper = RegisterNewTeamMapper.INSTANCE;
    private final ResultToResponse<Void> commandConverter;

    public TeamBuildingController(
            @Qualifier("registerNewTeamCommandHandler") CommandHandler registerNewTeamHandler,
            MessageSource messageSource) {
        this.registerNewTeamHandler = registerNewTeamHandler;
        this.commandConverter = new ResultToResponse<>(messageSource);
    }

    @Operation(summary = "Enregistre une nouvelle équipe")
    @PostMapping("/teams")
    public ResponseEntity<ApiResponse<Void>> registerNewTeam(@Valid @RequestBody RegisterNewTeamRequest request) {
        RegisterNewTeamCommand command = mapper.requestToCommand(request);
        ResultMap<Void> result = registerNewTeamHandler.handle(command);
        return commandConverter.toResponse(result);
    }
}
