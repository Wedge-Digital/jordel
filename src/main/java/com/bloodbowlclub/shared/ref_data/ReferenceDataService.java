package com.bloodbowlclub.shared.ref_data;

import com.bloodbowlclub.team_building.domain.roster.PlayerDefinition;
import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.bloodbowlclub.team_building.domain.team_staff.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de cache en RAM pour les données de référence (rosters, players, special rules).
 * Les données sont chargées au démarrage depuis les fichiers JSON et stockées dans des collections immuables.
 * Thread-safe en lecture seule.
 *
 * Usage:
 * - Récupérer un roster: referenceDataService.getRosterById("HUMAN")
 * - Récupérer un joueur: referenceDataService.getPlayerById("HUMAN__LINEMAN")
 * - Lister tous les rosters: referenceDataService.getAllRosters()
 */
@Service
public class ReferenceDataService {

    private static final Logger log = LoggerFactory.getLogger(ReferenceDataService.class);

    private final ResourceLoader resourceLoader;

    // ObjectMapper dédié pour les données de référence (sans polymorphisme)
    private final ObjectMapper refDataObjectMapper;

    // Caches immuables - Rosters
    private Map<String, Roster> rostersById;
    private List<Roster> allRosters;

    // Caches immuables - Players (index global de tous les joueurs de tous les rosters)
    private Map<String, PlayerDefinition> playersById;

    // Caches immuables - Special Rules
    private Map<String, SpecialRule> specialRulesById;
    private List<SpecialRule> allSpecialRules;

    // Caches immuables - Staff
    private Map<String, TeamStaff> staffById;
    private List<TeamStaff> allStaff;

    public ReferenceDataService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;

        // Créer un ObjectMapper dédié sans configuration polymorphique pour les ref data
        this.refDataObjectMapper = new ObjectMapper();

        // Utiliser un mix-in pour désactiver @JsonTypeInfo sur Roster/AggregateRoot et TeamStaff
        this.refDataObjectMapper.addMixIn(com.bloodbowlclub.lib.domain.AggregateRoot.class, IgnoreTypeInfoMixin.class);
        this.refDataObjectMapper.addMixIn(TeamStaff.class, IgnoreTypeInfoMixin.class);

        // Enregistrer le deserializer pour TeamStaff
        SimpleModule staffModule = new SimpleModule();
        staffModule.addDeserializer(TeamStaff.class, new TeamStaffDeserializer());
        this.refDataObjectMapper.registerModule(staffModule);
    }

    /**
     * Mix-in pour ignorer les annotations @JsonTypeInfo sur AggregateRoot lors du chargement des ref data
     */
    @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NONE)
    private abstract static class IgnoreTypeInfoMixin {
    }

    /**
     * Charge toutes les données de référence au démarrage de l'application.
     * Lance une RuntimeException si le chargement échoue (empêche le démarrage).
     */
    @PostConstruct
    public void loadReferenceData() {
        log.info("Loading reference data into memory...");
        try {
            // Charger d'abord les staff pour les utiliser lors de la désérialisation des rosters
            Map<String, TeamStaff> staffMap = loadStaff();

            // Configurer le deserializer personnalisé sur l'ObjectMapper dédié avec les données staff
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Roster.class, new RosterDeserializer(staffMap));
            refDataObjectMapper.registerModule(module);

            loadSpecialRules();
            loadRosters();

            log.info("Reference data loaded successfully: {} rosters, {} players, {} special rules, {} staff",
                    allRosters.size(), playersById.size(), allSpecialRules.size(), allStaff.size());
        } catch (Exception e) {
            log.error("Failed to load reference data", e);
            throw new RuntimeException("Cannot start application: reference data loading failed", e);
        }
    }

    private void loadRosters() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:reference/teams_en.json");

        // Lire le fichier JSON
        JsonNode root = refDataObjectMapper.readTree(resource.getInputStream());
        JsonNode teamsNode = root.get("teams");

        List<Roster> rosters = new ArrayList<>();
        if (teamsNode != null && teamsNode.isArray()) {
            for (JsonNode teamNode : teamsNode) {
                Roster roster = refDataObjectMapper.treeToValue(teamNode, Roster.class);
                rosters.add(roster);
            }
        }

        // Créer des collections immuables pour les rosters
        this.allRosters = Collections.unmodifiableList(new ArrayList<>(rosters));
        this.rostersById = Collections.unmodifiableMap(
                rosters.stream().collect(Collectors.toMap(
                        Roster::getId,
                        roster -> roster
                ))
        );

        // Créer un index global de tous les joueurs (de tous les rosters)
        Map<String, PlayerDefinition> playersIndex = new HashMap<>();
        for (Roster roster : rosters) {
            if (roster.getPlayerDefinitions() != null) {
                for (PlayerDefinition player : roster.getPlayerDefinitions()) {
                    playersIndex.put(player.getId(), player);
                }
            }
        }
        this.playersById = Collections.unmodifiableMap(playersIndex);
    }

    private void loadSpecialRules() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:reference/special_rules_en.json");

        SpecialRulesWrapper wrapper = refDataObjectMapper.readValue(
                resource.getInputStream(),
                SpecialRulesWrapper.class
        );

        List<SpecialRule> rules = wrapper.getSpecialRules();
        this.allSpecialRules = Collections.unmodifiableList(new ArrayList<>(rules));
        this.specialRulesById = Collections.unmodifiableMap(
                rules.stream().collect(Collectors.toMap(
                        SpecialRule::getUid,
                        rule -> rule
                ))
        );
    }

    private Map<String, TeamStaff> loadStaff() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:reference/staff_en.json");

        // Lire le fichier JSON
        JsonNode root = refDataObjectMapper.readTree(resource.getInputStream());
        JsonNode staffNode = root.get("staff");

        List<TeamStaff> teamStaffList = new ArrayList<>();
        if (staffNode != null && staffNode.isArray()) {
            for (JsonNode node : staffNode) {
                TeamStaff staff = refDataObjectMapper.treeToValue(node, TeamStaff.class);
                teamStaffList.add(staff);
            }
        }

        this.allStaff = Collections.unmodifiableList(teamStaffList);

        Map<String, TeamStaff> staffMap = teamStaffList.stream().collect(Collectors.toMap(
                TeamStaff::getId,
                s -> s
        ));
        this.staffById = Collections.unmodifiableMap(staffMap);

        return staffMap;
    }

    // ===========================
    // Méthodes d'accès - Rosters
    // ===========================

    /**
     * Récupère un roster par son ID.
     * @param rosterId ID du roster (ex: "HUMAN", "CHAOS_CHOSEN")
     * @return Optional contenant le roster si trouvé
     */
    public Optional<Roster> getRosterById(String rosterId) {
        return Optional.ofNullable(rostersById.get(rosterId));
    }

    /**
     * Récupère tous les rosters.
     * @return Liste immuable de tous les rosters
     */
    public List<Roster> getAllRosters() {
        return allRosters;
    }

    /**
     * Vérifie si un roster existe.
     * @param rosterId ID du roster
     * @return true si le roster existe
     */
    public boolean rosterExists(String rosterId) {
        return rostersById.containsKey(rosterId);
    }

    /**
     * Compte le nombre total de rosters chargés.
     * @return nombre de rosters
     */
    public int getRosterCount() {
        return allRosters.size();
    }

    // ===========================
    // Méthodes d'accès - Players
    // ===========================

    /**
     * Récupère un joueur par son ID (tous rosters confondus).
     * @param playerId ID du joueur (ex: "HUMAN__LINEMAN")
     * @return Optional contenant le joueur si trouvé
     */
    public Optional<PlayerDefinition> getPlayerById(String playerId) {
        return Optional.ofNullable(playersById.get(playerId));
    }

    /**
     * Récupère tous les joueurs d'un roster.
     * @param rosterId ID du roster
     * @return Liste des joueurs du roster (liste vide si roster non trouvé)
     */
    public List<PlayerDefinition> getPlayersByRoster(String rosterId) {
        return getRosterById(rosterId)
                .map(Roster::getPlayerDefinitions)
                .orElse(Collections.emptyList());
    }

    /**
     * Compte le nombre total de joueurs chargés (tous rosters confondus).
     * @return nombre de joueurs
     */
    public int getPlayerCount() {
        return playersById.size();
    }

    // ===================================
    // Méthodes d'accès - Special Rules
    // ===================================

    /**
     * Récupère une règle spéciale par son UID.
     * @param uid UID de la règle (ex: "MASTERS_OF_UNDEATH")
     * @return Optional contenant la règle si trouvée
     */
    public Optional<SpecialRule> getSpecialRuleByUid(String uid) {
        return Optional.ofNullable(specialRulesById.get(uid));
    }

    /**
     * Récupère toutes les règles spéciales.
     * @return Liste immuable de toutes les règles spéciales
     */
    public List<SpecialRule> getAllSpecialRules() {
        return allSpecialRules;
    }

    // ===========================
    // Méthodes d'accès - Staff
    // ===========================

    /**
     * Récupère un staff par son UID.
     * @param uid UID du staff (ex: "APOTHECARY", "CHEERLEADERS")
     * @return Optional contenant le staff si trouvé
     */
    public Optional<TeamStaff> getStaffByUid(String uid) {
        return Optional.ofNullable(staffById.get(uid));
    }

    /**
     * Récupère tous les staff.
     * @return Liste immuable de tous les staff
     */
    public List<TeamStaff> getAllStaff() {
        return allStaff;
    }

    // ===========================
    // Classes internes - DTOs
    // ===========================

    /**
     * Wrapper pour le fichier special_rules.json
     */
    private static class SpecialRulesWrapper {
        @com.fasterxml.jackson.annotation.JsonProperty("special_rules")
        private List<SpecialRule> specialRules;

        public List<SpecialRule> getSpecialRules() {
            return specialRules;
        }

        public void setSpecialRules(List<SpecialRule> specialRules) {
            this.specialRules = specialRules;
        }
    }

    /**
     * DTO pour une règle spéciale
     */
    public static class SpecialRule {
        private String uid;
        private String label;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

}
