package com.bloodbowlclub.team_building.ref_data;

import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.bloodbowlclub.shared.ruleset.CreationBudget;
import com.bloodbowlclub.team_building.domain.ruleset.RosterTier;
import com.bloodbowlclub.team_building.domain.ruleset.Ruleset;
import com.bloodbowlclub.shared.ruleset.RulesetID;
import com.bloodbowlclub.shared.ruleset.RulesetName;
import com.bloodbowlclub.shared.ruleset.TierID;
import com.bloodbowlclub.shared.ruleset.TierName;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service gérant les rulesets disponibles pour la création d'équipes.
 * Pour l'instant, les rulesets sont hardcodés.
 * TODO: À terme, charger depuis un fichier JSON ou une base de données.
 */
@Service
public class RulesetService {

    private final ReferenceDataService referenceDataService;
    private Map<String, Ruleset> rulesetsById;

    public RulesetService(ReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }

    @PostConstruct
    public void initializeRulesets() {
        this.rulesetsById = new HashMap<>();

        // Créer un ruleset "Standard" avec tous les rosters disponibles
        List<Roster> allRosters = referenceDataService.getAllRosters();
        RosterTier standardTier = RosterTier.builder()
                .tierID(new TierID("01STANDARDTIER0000000000"))
                .name(new TierName("Standard Tier"))
                .teamBudget(new CreationBudget(1000000)) // 1 million de gold
                .rosterList(allRosters)
                .build();

        Ruleset standardRuleset = Ruleset.builder()
                .rulesetID(new RulesetID("01STANDARD00000000000000"))
                .name(new RulesetName("Standard Ruleset"))
                .tierList(List.of(standardTier))
                .build();

        rulesetsById.put(standardRuleset.getId(), standardRuleset);
    }

    /**
     * Récupère un ruleset par son ID.
     */
    public Optional<Ruleset> getRulesetById(String rulesetId) {
        return Optional.ofNullable(rulesetsById.get(rulesetId));
    }

    /**
     * Récupère tous les rulesets disponibles.
     */
    public List<Ruleset> getAllRulesets() {
        return List.copyOf(rulesetsById.values());
    }
}
