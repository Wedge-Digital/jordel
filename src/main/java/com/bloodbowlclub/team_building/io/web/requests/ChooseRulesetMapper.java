package com.bloodbowlclub.team_building.io.web.requests;

import com.bloodbowlclub.team_building.domain.commands.ChooseRulesetCommand;
import com.bloodbowlclub.team_building.domain.ruleset.Ruleset;
import com.bloodbowlclub.team_building.ref_data.RulesetService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ChooseRulesetMapper {

    private final RulesetService rulesetService;

    public ChooseRulesetMapper(RulesetService rulesetService) {
        this.rulesetService = rulesetService;
    }

    /**
     * Convertit une requête HTTP en commande en chargeant le ruleset depuis le service.
     * Retourne null si le ruleset n'est pas trouvé (sera géré par le controller).
     */
    public ChooseRulesetCommand requestToCommand(ChooseRulesetRequest request) {
        Optional<Ruleset> ruleset = rulesetService.getRulesetById(request.getRulesetId());

        if (ruleset.isEmpty()) {
            return null;
        }

        return ChooseRulesetCommand.builder()
                .teamId(request.getTeamId())
                .ruleset(ruleset.get())
                .build();
    }
}
