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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service de cache en RAM pour les données de référence Blood Bowl.
 * Les données sont chargées au démarrage depuis les fichiers JSON et stockées dans des collections immuables.
 * Thread-safe en lecture seule.
 * Support multi-locale (FR, EN).
 *
 * ISOLÉ de team_building.domain - utilise ses propres modèles (RosterRef, PlayerDefinitionRef, etc.)
 *
 * Usage:
 * - Récupérer un roster: referenceDataService.getRosterById("HUMAN", Locale.ENGLISH)
 * - Récupérer un joueur: referenceDataService.getPlayerById("HUMAN__LINEMAN", Locale.FRENCH)
 * - Lister tous les rosters: referenceDataService.getAllRosters(Locale.ENGLISH)
 */
@Service("referenceDataServiceForAPI")
public class ReferenceDataService {

    private static final Logger log = LoggerFactory.getLogger(ReferenceDataService.class);

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    // Cache par locale - Thread-safe
    private final Map<Locale, LocalizedReferenceData> dataByLocale = new ConcurrentHashMap<>();

    // Locales supportées
    private static final Set<Locale> SUPPORTED_LOCALES = Set.of(
            Locale.ENGLISH,
            Locale.FRENCH
    );

    public ReferenceDataService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Charge les données de référence pour toutes les locales supportées au démarrage.
     */
    @PostConstruct
    public void loadAllReferenceData() {
        log.info("[ReferenceAPI] Loading reference data for all supported locales...");
        for (Locale locale : SUPPORTED_LOCALES) {
            try {
                loadReferenceData(locale);
            } catch (Exception e) {
                log.error("[ReferenceAPI] Failed to load reference data for locale: {}", locale, e);
            }
        }
        log.info("[ReferenceAPI] Reference data loaded for {} locales", dataByLocale.size());
    }

    /**
     * Charge les données de référence pour une locale spécifique.
     *
     * @param locale la locale pour laquelle charger les données (Locale.ENGLISH, Locale.FRENCH)
     */
    public void loadReferenceData(Locale locale) {
        log.info("[ReferenceAPI] Loading reference data for locale: {}", locale);
        try {
            String languageCode = getLanguageCode(locale);

            LocalizedReferenceData data = new LocalizedReferenceData();

            data.staff = loadStaff(languageCode);
            data.specialRules = loadSpecialRules(languageCode);
            data.skills = loadSkills(languageCode);
            data.skillCategories = loadSkillCategories(languageCode);
            data.rosters = loadRosters(languageCode);

            // Créer l'index global des joueurs
            data.playersById = createPlayersIndex(data.rosters);

            // Stocker dans le cache
            dataByLocale.put(locale, data);

            log.info("[ReferenceAPI] Reference data loaded for {}: {} rosters, {} players, {} special rules, {} staff, {} skills, {} skill categories",
                    locale, data.rosters.size(), data.playersById.size(), data.specialRules.size(),
                    data.staff.size(), data.skills.size(), data.skillCategories.size());
        } catch (Exception e) {
            log.error("[ReferenceAPI] Failed to load reference data for locale: {}", locale, e);
            throw new RuntimeException("Cannot load reference data for locale: " + locale, e);
        }
    }

    /**
     * Convertit une Locale en code langue pour les fichiers (en, fr).
     */
    private String getLanguageCode(Locale locale) {
        if (locale.getLanguage().equals("fr")) {
            return "fr";
        }
        return "en"; // Par défaut anglais
    }

    /**
     * Récupère les données pour une locale, avec fallback sur l'anglais.
     */
    private LocalizedReferenceData getData(Locale locale) {
        LocalizedReferenceData data = dataByLocale.get(locale);
        if (data == null) {
            log.warn("[ReferenceAPI] No data found for locale {}, falling back to English", locale);
            data = dataByLocale.get(Locale.ENGLISH);
        }
        if (data == null) {
            throw new IllegalStateException("No reference data loaded. Service not initialized properly.");
        }
        return data;
    }

    private List<RosterRef> loadRosters(String languageCode) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:reference/teams_" + languageCode + ".json");
        JsonNode root = objectMapper.readTree(resource.getInputStream());
        JsonNode teamsNode = root.get("teams");

        List<RosterRef> rosters = new ArrayList<>();
        if (teamsNode != null && teamsNode.isArray()) {
            for (JsonNode teamNode : teamsNode) {
                RosterRef roster = deserializeRoster(teamNode);
                rosters.add(roster);
            }
        }

        return Collections.unmodifiableList(rosters);
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

    private Map<String, PlayerDefinitionRef> createPlayersIndex(List<RosterRef> rosters) {
        Map<String, PlayerDefinitionRef> playersIndex = new HashMap<>();
        for (RosterRef roster : rosters) {
            if (roster.getAvailablePlayers() != null) {
                for (PlayerDefinitionRef player : roster.getAvailablePlayers()) {
                    playersIndex.put(player.getUid(), player);
                }
            }
        }
        return Collections.unmodifiableMap(playersIndex);
    }

    private List<SpecialRuleRef> loadSpecialRules(String languageCode) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:reference/special_rules_" + languageCode + ".json");
        SpecialRulesWrapper wrapper = objectMapper.readValue(resource.getInputStream(), SpecialRulesWrapper.class);
        return Collections.unmodifiableList(new ArrayList<>(wrapper.getSpecialRules()));
    }

    private List<TeamStaffRef> loadStaff(String languageCode) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:reference/staff_" + languageCode + ".json");
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

        return Collections.unmodifiableList(teamStaffList);
    }

    private List<SkillRef> loadSkills(String languageCode) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:reference/skills_" + languageCode + ".json");
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

        return Collections.unmodifiableList(skillsList);
    }

    private List<SkillCategoryRef> loadSkillCategories(String languageCode) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:reference/skill_cat_" + languageCode + ".json");
        SkillCategoriesWrapper wrapper = objectMapper.readValue(resource.getInputStream(), SkillCategoriesWrapper.class);
        return Collections.unmodifiableList(new ArrayList<>(wrapper.getSkillCategories()));
    }

    // ===========================
    // Méthodes d'accès - Rosters
    // ===========================

    public Optional<RosterRef> getRosterById(String rosterId, Locale locale) {
        LocalizedReferenceData data = getData(locale);
        return data.rosters.stream()
                .filter(r -> r.getUid().equals(rosterId))
                .findFirst();
    }

    public List<RosterRef> getAllRosters(Locale locale) {
        return getData(locale).rosters;
    }

    public boolean rosterExists(String rosterId, Locale locale) {
        return getRosterById(rosterId, locale).isPresent();
    }

    public int getRosterCount(Locale locale) {
        return getData(locale).rosters.size();
    }

    // ===========================
    // Méthodes d'accès - Players
    // ===========================

    public Optional<PlayerDefinitionRef> getPlayerById(String playerId, Locale locale) {
        return Optional.ofNullable(getData(locale).playersById.get(playerId));
    }

    public List<PlayerDefinitionRef> getPlayersByRoster(String rosterId, Locale locale) {
        return getRosterById(rosterId, locale)
                .map(RosterRef::getAvailablePlayers)
                .orElse(Collections.emptyList());
    }

    public int getPlayerCount(Locale locale) {
        return getData(locale).playersById.size();
    }

    // ===================================
    // Méthodes d'accès - Special Rules
    // ===================================

    public Optional<SpecialRuleRef> getSpecialRuleByUid(String uid, Locale locale) {
        return getData(locale).specialRules.stream()
                .filter(r -> r.getUid().equals(uid))
                .findFirst();
    }

    public List<SpecialRuleRef> getAllSpecialRules(Locale locale) {
        return getData(locale).specialRules;
    }

    // ===========================
    // Méthodes d'accès - Staff
    // ===========================

    public Optional<TeamStaffRef> getStaffByUid(String uid, Locale locale) {
        return getData(locale).staff.stream()
                .filter(s -> s.getUid().equals(uid))
                .findFirst();
    }

    public List<TeamStaffRef> getAllStaff(Locale locale) {
        return getData(locale).staff;
    }

    public List<TeamStaffRef> getStaffByRoster(String rosterId, Locale locale) {
        return getRosterById(rosterId, locale)
                .map(roster -> roster.getAllowedStaffUids().stream()
                        .map(uid -> getStaffByUid(uid, locale))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    // ===========================
    // Méthodes d'accès - Skills
    // ===========================

    public Optional<SkillRef> getSkillByUid(String uid, Locale locale) {
        return getData(locale).skills.stream()
                .filter(s -> s.getUid().equals(uid))
                .findFirst();
    }

    public List<SkillRef> getAllSkills(Locale locale) {
        return getData(locale).skills;
    }

    public List<SkillRef> getSkillsByCategory(String category, Locale locale) {
        return getData(locale).skills.stream()
                .filter(skill -> skill.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    // ========================================
    // Méthodes d'accès - Skill Categories
    // ========================================

    public Optional<SkillCategoryRef> getSkillCategoryByUid(String uid, Locale locale) {
        return getData(locale).skillCategories.stream()
                .filter(c -> c.getUid().equals(uid))
                .findFirst();
    }

    public List<SkillCategoryRef> getAllSkillCategories(Locale locale) {
        return getData(locale).skillCategories;
    }

    // ===========================
    // Méthodes utilitaires
    // ===========================

    /**
     * Retourne les locales supportées et chargées.
     */
    public Set<Locale> getSupportedLocales() {
        return Collections.unmodifiableSet(dataByLocale.keySet());
    }

    // ===========================
    // Classes internes
    // ===========================

    /**
     * Conteneur pour les données d'une locale spécifique.
     */
    private static class LocalizedReferenceData {
        List<RosterRef> rosters;
        Map<String, PlayerDefinitionRef> playersById;
        List<SpecialRuleRef> specialRules;
        List<TeamStaffRef> staff;
        List<SkillRef> skills;
        List<SkillCategoryRef> skillCategories;
    }

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
