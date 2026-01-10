package com.bloodbowlclub.reference.service;

import com.bloodbowlclub.reference.domain.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Service de cache en RAM pour les données de référence Blood Bowl.
 * Les données sont chargées au démarrage depuis les fichiers JSON et stockées dans des collections immuables.
 * Thread-safe en lecture seule.
 *
 * ISOLÉ de team_building.domain - utilise ses propres modèles (RosterRef, PlayerDefinitionRef, etc.)
 *
 * Usage:
 * - Récupérer un roster: referenceDataService.getRosterById("HUMAN")
 * - Récupérer un joueur: referenceDataService.getPlayerById("HUMAN__LINEMAN")
 * - Lister tous les rosters: referenceDataService.getAllRosters()
 */
@Service("referenceDataServiceForAPI")
public class ReferenceDataService {

    private static final Logger log = LoggerFactory.getLogger(ReferenceDataService.class);

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    // Caches immuables
    private Map<String, RosterRef> rostersById;
    private List<RosterRef> allRosters;
    private Map<String, PlayerDefinitionRef> playersById;
    private Map<String, SpecialRuleRef> specialRulesById;
    private List<SpecialRuleRef> allSpecialRules;
    private Map<String, TeamStaffRef> staffById;
    private List<TeamStaffRef> allStaff;
    private Map<String, SkillRef> skillsById;
    private List<SkillRef> allSkills;
    private Map<String, SkillCategoryRef> skillCategoriesById;
    private List<SkillCategoryRef> allSkillCategories;

    public ReferenceDataService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void loadReferenceData() {
        log.info("[ReferenceAPI] Loading reference data into memory...");
        try {
            loadStaff();
            loadSpecialRules();
            loadSkills();
            loadSkillCategories();
            loadRosters();

            log.info("[ReferenceAPI] Reference data loaded: {} rosters, {} players, {} special rules, {} staff, {} skills, {} skill categories",
                    allRosters.size(), playersById.size(), allSpecialRules.size(), allStaff.size(), allSkills.size(), allSkillCategories.size());
        } catch (Exception e) {
            log.error("[ReferenceAPI] Failed to load reference data", e);
            throw new RuntimeException("Cannot start application: reference data loading failed", e);
        }
    }

    private void loadRosters() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:reference/teams_en.json");
        JsonNode root = objectMapper.readTree(resource.getInputStream());
        JsonNode teamsNode = root.get("teams");

        List<RosterRef> rosters = new ArrayList<>();
        if (teamsNode != null && teamsNode.isArray()) {
            for (JsonNode teamNode : teamsNode) {
                RosterRef roster = deserializeRoster(teamNode);
                rosters.add(roster);
            }
        }

        this.allRosters = Collections.unmodifiableList(new ArrayList<>(rosters));
        this.rostersById = Collections.unmodifiableMap(
                rosters.stream().collect(Collectors.toMap(RosterRef::getUid, roster -> roster))
        );

        // Index global des joueurs
        Map<String, PlayerDefinitionRef> playersIndex = new HashMap<>();
        for (RosterRef roster : rosters) {
            if (roster.getAvailablePlayers() != null) {
                for (PlayerDefinitionRef player : roster.getAvailablePlayers()) {
                    playersIndex.put(player.getUid(), player);
                }
            }
        }
        this.playersById = Collections.unmodifiableMap(playersIndex);
    }

    private RosterRef deserializeRoster(JsonNode node) {
        String uid = node.get("uid").asText();
        String name = node.get("name").asText();
        Integer rerollCost = node.get("rerollCost").asInt() / 1000;

        RosterTier tier = node.has("tier") ? new RosterTier(node.get("tier").asText()) : null;

        List<String> specialRuleUids = new ArrayList<>();
        JsonNode specialRulesNode = node.get("specialRules");
        if (specialRulesNode != null && specialRulesNode.isArray()) {
            for (JsonNode ruleNode : specialRulesNode) {
                specialRuleUids.add(ruleNode.asText());
            }
        }
        SpecialRulesList specialRules = new SpecialRulesList(specialRuleUids);

        List<PlayerDefinitionRef> playerDefinitions = new ArrayList<>();
        JsonNode playersNode = node.get("availablePlayers");
        if (playersNode != null && playersNode.isArray()) {
            for (JsonNode playerNode : playersNode) {
                PlayerDefinitionRef player = deserializePlayer(playerNode);
                playerDefinitions.add(player);
            }
        }

        List<CrossLimitRef> crossLimits = new ArrayList<>();
        JsonNode crossLimitsNode = node.get("cross_limit");
        if (crossLimitsNode != null && crossLimitsNode.isArray()) {
            for (JsonNode limitNode : crossLimitsNode) {
                List<String> limitedPlayerIds = new ArrayList<>();
                JsonNode idsNode = limitNode.get("in");
                if (idsNode == null) idsNode = limitNode.get("limitedPlayerIds");
                if (idsNode != null && idsNode.isArray()) {
                    for (JsonNode idNode : idsNode) {
                        limitedPlayerIds.add(idNode.asText());
                    }
                }
                int maxValue;
                JsonNode maxNode = limitNode.get("max");
                if (maxNode != null) {
                    maxValue = maxNode.asInt();
                } else {
                    maxValue = limitNode.get("limit").asInt();
                }
                CrossLimitRef crossLimit = new CrossLimitRef(maxValue, limitedPlayerIds);
                crossLimits.add(crossLimit);
            }
        }

        List<String> allowedStaffUids = new ArrayList<>();
        JsonNode staffNode = node.get("allowedStaff");
        if (staffNode != null && staffNode.isArray()) {
            for (JsonNode staffUidNode : staffNode) {
                allowedStaffUids.add(staffUidNode.asText());
            }
        }

        return RosterRef.builder()
                .uid(uid)
                .name(name)
                .rerollCost(rerollCost)
                .tier(tier)
                .specialRules(specialRules)
                .availablePlayers(playerDefinitions)
                .allowedStaffUids(allowedStaffUids)
                .crossLimits(crossLimits)
                .build();
    }

    private PlayerDefinitionRef deserializePlayer(JsonNode playerNode) {
        List<String> skillUids = new ArrayList<>();
        JsonNode skillsNode = playerNode.get("skills");
        if (skillsNode != null && skillsNode.isArray()) {
            for (JsonNode skillNode : skillsNode) {
                skillUids.add(skillNode.asText());
            }
        }

        List<String> primaryAccessList = new ArrayList<>();
        JsonNode primaryNode = playerNode.get("primaryAccess");
        if (primaryNode != null && primaryNode.isArray()) {
            for (JsonNode catNode : primaryNode) {
                primaryAccessList.add(catNode.asText());
            }
        }

        List<String> secondaryAccessList = new ArrayList<>();
        JsonNode secondaryNode = playerNode.get("secondaryAccess");
        if (secondaryNode != null && secondaryNode.isArray()) {
            for (JsonNode catNode : secondaryNode) {
                secondaryAccessList.add(catNode.asText());
            }
        }

        return PlayerDefinitionRef.builder()
                .uid(playerNode.get("uid").asText())
                .positionName(playerNode.get("positionName").asText())
                .cost(playerNode.get("cost").asInt() / 1000)
                .maxQuantity(playerNode.get("max_quantity").asInt())
                .movement(new PlayerCharacteristic(playerNode.get("MA").asInt()))
                .strength(new PlayerCharacteristic(playerNode.get("ST").asInt()))
                .agility(new PlayerCharacteristic(playerNode.get("AG").asInt()))
                .passing(new PlayerCharacteristic(playerNode.get("PA").asInt()))
                .armourValue(new PlayerCharacteristic(playerNode.get("AV").asInt()))
                .skills(new SkillsList(skillUids))
                .primaryAccess(new SkillAccessCategories(primaryAccessList))
                .secondaryAccess(new SkillAccessCategories(secondaryAccessList))
                .build();
    }

    private void loadSpecialRules() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:reference/special_rules_en.json");
        SpecialRulesWrapper wrapper = objectMapper.readValue(resource.getInputStream(), SpecialRulesWrapper.class);
        List<SpecialRuleRef> rules = wrapper.getSpecialRules();
        this.allSpecialRules = Collections.unmodifiableList(new ArrayList<>(rules));
        this.specialRulesById = Collections.unmodifiableMap(
                rules.stream().collect(Collectors.toMap(SpecialRuleRef::getUid, rule -> rule))
        );
    }

    private void loadStaff() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:reference/staff_en.json");
        JsonNode root = objectMapper.readTree(resource.getInputStream());
        JsonNode staffNode = root.get("staff");

        List<TeamStaffRef> teamStaffList = new ArrayList<>();
        if (staffNode != null && staffNode.isArray()) {
            for (JsonNode node : staffNode) {
                TeamStaffRef staff = TeamStaffRef.builder()
                        .uid(node.get("uid").asText())
                        .name(node.get("name").asText())
                        .price(node.get("price").asInt())
                        .maxQuantity(node.get("maxQuantity").asInt())
                        .description(node.has("description") ? node.get("description").asText() : null)
                        .build();
                teamStaffList.add(staff);
            }
        }

        this.allStaff = Collections.unmodifiableList(teamStaffList);
        this.staffById = Collections.unmodifiableMap(
                teamStaffList.stream().collect(Collectors.toMap(TeamStaffRef::getUid, s -> s))
        );
    }

    private void loadSkills() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:reference/skills_en.json");
        JsonNode root = objectMapper.readTree(resource.getInputStream());
        JsonNode skillsNode = root.get("skills");

        List<SkillRef> skillsList = new ArrayList<>();
        if (skillsNode != null && skillsNode.isArray()) {
            for (JsonNode node : skillsNode) {
                SkillRef skill = new SkillRef();
                skill.setUid(node.get("uid").asText());
                skill.setName(node.get("name").asText());
                skill.setCategory(node.get("category").asText());
                skill.setType(node.get("type").asText());
                skill.setActivation(node.get("activation").asText());
                skill.setDescription(node.has("description") ? node.get("description").asText() : null);
                skillsList.add(skill);
            }
        }

        this.allSkills = Collections.unmodifiableList(skillsList);
        this.skillsById = Collections.unmodifiableMap(
                skillsList.stream().collect(Collectors.toMap(SkillRef::getUid, s -> s))
        );
    }

    private void loadSkillCategories() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:reference/skill_cat_en.json");
        SkillCategoriesWrapper wrapper = objectMapper.readValue(resource.getInputStream(), SkillCategoriesWrapper.class);
        List<SkillCategoryRef> categories = wrapper.getSkillCategories();
        this.allSkillCategories = Collections.unmodifiableList(new ArrayList<>(categories));
        this.skillCategoriesById = Collections.unmodifiableMap(
                categories.stream().collect(Collectors.toMap(SkillCategoryRef::getUid, cat -> cat))
        );
    }

    // ===========================
    // Méthodes d'accès - Rosters
    // ===========================

    public Optional<RosterRef> getRosterById(String rosterId) {
        return Optional.ofNullable(rostersById.get(rosterId));
    }

    public List<RosterRef> getAllRosters() {
        return allRosters;
    }

    public boolean rosterExists(String rosterId) {
        return rostersById.containsKey(rosterId);
    }

    public int getRosterCount() {
        return allRosters.size();
    }

    // ===========================
    // Méthodes d'accès - Players
    // ===========================

    public Optional<PlayerDefinitionRef> getPlayerById(String playerId) {
        return Optional.ofNullable(playersById.get(playerId));
    }

    public List<PlayerDefinitionRef> getPlayersByRoster(String rosterId) {
        return getRosterById(rosterId)
                .map(RosterRef::getAvailablePlayers)
                .orElse(Collections.emptyList());
    }

    public int getPlayerCount() {
        return playersById.size();
    }

    // ===================================
    // Méthodes d'accès - Special Rules
    // ===================================

    public Optional<SpecialRuleRef> getSpecialRuleByUid(String uid) {
        return Optional.ofNullable(specialRulesById.get(uid));
    }

    public List<SpecialRuleRef> getAllSpecialRules() {
        return allSpecialRules;
    }

    // ===========================
    // Méthodes d'accès - Staff
    // ===========================

    public Optional<TeamStaffRef> getStaffByUid(String uid) {
        return Optional.ofNullable(staffById.get(uid));
    }

    public List<TeamStaffRef> getAllStaff() {
        return allStaff;
    }

    public List<TeamStaffRef> getStaffByRoster(String rosterId) {
        return getRosterById(rosterId)
                .map(roster -> roster.getAllowedStaffUids().stream()
                        .map(staffById::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    // ===========================
    // Méthodes d'accès - Skills
    // ===========================

    public Optional<SkillRef> getSkillByUid(String uid) {
        return Optional.ofNullable(skillsById.get(uid));
    }

    public List<SkillRef> getAllSkills() {
        return allSkills;
    }

    public List<SkillRef> getSkillsByCategory(String category) {
        return allSkills.stream()
                .filter(skill -> skill.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    // ========================================
    // Méthodes d'accès - Skill Categories
    // ========================================

    public Optional<SkillCategoryRef> getSkillCategoryByUid(String uid) {
        return Optional.ofNullable(skillCategoriesById.get(uid));
    }

    public List<SkillCategoryRef> getAllSkillCategories() {
        return allSkillCategories;
    }

    // ===========================
    // Classes internes - DTOs
    // ===========================

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    private static class SpecialRulesWrapper {
        @com.fasterxml.jackson.annotation.JsonProperty("special_rules")
        private List<SpecialRuleRef> specialRules;

        public List<SpecialRuleRef> getSpecialRules() { return specialRules; }
        public void setSpecialRules(List<SpecialRuleRef> specialRules) { this.specialRules = specialRules; }
    }

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    private static class SkillCategoriesWrapper {
        @com.fasterxml.jackson.annotation.JsonProperty("skill_categories")
        private List<SkillCategoryRef> skillCategories;

        public List<SkillCategoryRef> getSkillCategories() { return skillCategories; }
        public void setSkillCategories(List<SkillCategoryRef> skillCategories) { this.skillCategories = skillCategories; }
    }
}
