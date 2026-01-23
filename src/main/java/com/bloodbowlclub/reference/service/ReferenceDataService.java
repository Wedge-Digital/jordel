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
        // Configuration pour un format JSON cohérent
        this.objectMapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
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
            data.leagues = loadLeagues(languageCode);
            data.skills = loadSkills(languageCode);
            data.skillCategories = loadSkillCategories(languageCode);
            data.rosters = loadRosters(languageCode, data.specialRules, data.leagues, data.skills);
            data.starPlayers = loadStarPlayers(languageCode, data.leagues, data.skills);

            // Créer l'index global des joueurs
            data.playersById = createPlayersIndex(data.rosters);

            // Stocker dans le cache
            dataByLocale.put(locale, data);

            log.info("[ReferenceAPI] Reference data loaded for {}: {} rosters, {} players, {} special rules, {} leagues, {} staff, {} skills, {} skill categories, {} star players",
                    locale, data.rosters.size(), data.playersById.size(), data.specialRules.size(),
                    data.leagues.size(), data.staff.size(), data.skills.size(), data.skillCategories.size(), data.starPlayers.size());
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

    private List<RosterRef> loadRosters(String languageCode, List<SpecialRuleRef> specialRuleRefs, List<LeagueRef> leagueRefs, List<SkillRef> skillRefs) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:reference/teams_" + languageCode + ".json");
        JsonNode root = objectMapper.readTree(resource.getInputStream());
        JsonNode teamsNode = root.get("teams");

        List<RosterRef> rosters = new ArrayList<>();
        if (teamsNode != null && teamsNode.isArray()) {
            for (JsonNode teamNode : teamsNode) {
                RosterRef roster = deserializeRoster(teamNode, specialRuleRefs, leagueRefs, skillRefs);
                rosters.add(roster);
            }
        }

        return Collections.unmodifiableList(rosters);
    }

    private RosterRef deserializeRoster(JsonNode node, List<SpecialRuleRef> specialRuleRefs, List<LeagueRef> leagueRefs, List<SkillRef> skillRefs) throws IOException {
        String uid = node.get("uid").asText();
        String name = node.get("name").asText();
        Integer rerollCost = node.get("rerollCost").asInt() / 1000;

        RosterTier tier = node.has("tier") ? new RosterTier(node.get("tier").asText()) : null;

        // Résolution des special rules: conversion des UIDs en objets complets
        List<SpecialRuleRef> resolvedSpecialRules = new ArrayList<>();
        JsonNode specialRulesNode = node.get("specialRules");
        if (specialRulesNode != null && specialRulesNode.isArray()) {
            for (JsonNode ruleNode : specialRulesNode) {
                String ruleUid = ruleNode.asText();
                Optional<SpecialRuleRef> foundRule = specialRuleRefs.stream()
                        .filter(rule -> Objects.equals(rule.getUid(), ruleUid))
                        .findFirst();

                if (foundRule.isPresent()) {
                    resolvedSpecialRules.add(foundRule.get());
                } else {
                    log.warn("[ReferenceAPI] Special rule '{}' not found for roster '{}'", ruleUid, uid);
                }
            }
        }

        // Résolution des leagues: conversion des UIDs en objets complets
        List<LeagueRef> resolvedLeagues = new ArrayList<>();
        JsonNode leaguesNode = node.get("leagues");
        if (leaguesNode != null && leaguesNode.isArray()) {
            for (JsonNode leagueNode : leaguesNode) {
                String leagueUid = leagueNode.asText();
                Optional<LeagueRef> foundLeague = leagueRefs.stream()
                        .filter(league -> Objects.equals(league.getUid(), leagueUid))
                        .findFirst();

                if (foundLeague.isPresent()) {
                    resolvedLeagues.add(foundLeague.get());
                } else {
                    log.warn("[ReferenceAPI] League '{}' not found for roster '{}'", leagueUid, uid);
                }
            }
        }

        List<PlayerDefinitionRef> playerDefinitions = new ArrayList<>();
        JsonNode playersNode = node.get("availablePlayers");
        if (playersNode != null && playersNode.isArray()) {
            for (JsonNode playerNode : playersNode) {
                PlayerDefinitionRef player = deserializePlayer(playerNode, skillRefs);
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
                .specialRules(resolvedSpecialRules)
                .leagues(resolvedLeagues)
                .availablePlayers(playerDefinitions)
                .allowedStaffUids(allowedStaffUids)
                .crossLimits(crossLimits)
                .build();
    }

    private PlayerDefinitionRef deserializePlayer(JsonNode playerNode, List<SkillRef> skillRefs) {
        // Résolution des skills: conversion des UIDs en objets allégés (uid + name uniquement)
        List<SkillRefLight> resolvedSkills = new ArrayList<>();
        JsonNode skillsNode = playerNode.get("skills");
        if (skillsNode != null && skillsNode.isArray()) {
            for (JsonNode skillNode : skillsNode) {
                String skillUid = skillNode.asText();
                Optional<SkillRef> foundSkill = skillRefs.stream()
                        .filter(skill -> Objects.equals(skill.getUid(), skillUid))
                        .findFirst();

                if (foundSkill.isPresent()) {
                    SkillRef skill = foundSkill.get();
                    resolvedSkills.add(new SkillRefLight(skill.getUid(), skill.getName()));
                } else {
                    log.warn("[ReferenceAPI] Skill '{}' not found in reference data", skillUid);
                }
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
                .skills(resolvedSkills)
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

    private List<LeagueRef> loadLeagues(String languageCode) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:reference/leagues_" + languageCode + ".json");
        LeaguesWrapper wrapper = objectMapper.readValue(resource.getInputStream(), LeaguesWrapper.class);
        return Collections.unmodifiableList(new ArrayList<>(wrapper.getLeagues()));
    }

    private List<StarPlayerRef> loadStarPlayers(String languageCode, List<LeagueRef> leagueRefs, List<SkillRef> skillRefs) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:reference/star_players_" + languageCode + ".json");
        JsonNode root = objectMapper.readTree(resource.getInputStream());
        JsonNode starPlayersNode = root.get("star_players");

        List<StarPlayerRef> starPlayers = new ArrayList<>();
        if (starPlayersNode != null && starPlayersNode.isArray()) {
            for (JsonNode starPlayerNode : starPlayersNode) {
                StarPlayerRef starPlayer = deserializeStarPlayer(starPlayerNode, leagueRefs, skillRefs);
                starPlayers.add(starPlayer);
            }
        }

        return Collections.unmodifiableList(starPlayers);
    }

    private StarPlayerRef deserializeStarPlayer(JsonNode node, List<LeagueRef> leagueRefs, List<SkillRef> skillRefs) {
        String uid = node.get("uid").asText();
        String name = node.get("name").asText();
        Integer cost = node.get("cost").asInt() / 1000;

        // Caractéristiques
        int ma = node.get("MA").asInt();
        int st = node.get("ST").asInt();
        int ag = parseCharacteristic(node.get("AG"));
        int pa = parseCharacteristic(node.get("PA"));
        int av = parseCharacteristic(node.get("AV"));

        String playerType = node.has("playerType") ? node.get("playerType").asText() : null;

        // Résolution des skills
        List<SkillRefLight> resolvedSkills = new ArrayList<>();
        JsonNode skillsNode = node.get("skills");
        if (skillsNode != null && skillsNode.isArray()) {
            for (JsonNode skillNode : skillsNode) {
                String skillUid = skillNode.asText();
                Optional<SkillRef> foundSkill = skillRefs.stream()
                        .filter(skill -> Objects.equals(skill.getUid(), skillUid))
                        .findFirst();

                if (foundSkill.isPresent()) {
                    SkillRef skill = foundSkill.get();
                    resolvedSkills.add(new SkillRefLight(skill.getUid(), skill.getName()));
                } else {
                    // Pour les skills spéciaux comme LONER_3, LONER_4, etc., on crée une référence légère
                    resolvedSkills.add(new SkillRefLight(skillUid, formatSkillName(skillUid)));
                }
            }
        }

        // Capacité spéciale
        String specialAbilityName = node.get("specialAbilityName").asText();
        String specialAbilityDescription = node.get("specialAbilityDescription").asText();

        // Résolution des leagues (playsFor)
        List<LeagueRef> resolvedLeagues = new ArrayList<>();
        JsonNode playsForNode = node.get("playsFor");
        if (playsForNode != null && playsForNode.isArray()) {
            for (JsonNode leagueNode : playsForNode) {
                String leagueUid = leagueNode.asText();
                Optional<LeagueRef> foundLeague = leagueRefs.stream()
                        .filter(league -> Objects.equals(league.getUid(), leagueUid))
                        .findFirst();

                if (foundLeague.isPresent()) {
                    resolvedLeagues.add(foundLeague.get());
                } else {
                    log.warn("[ReferenceAPI] League '{}' not found for star player '{}'", leagueUid, uid);
                }
            }
        }

        // Équipes disponibles
        List<String> availableForRosters = new ArrayList<>();
        JsonNode rostersNode = node.get("availableForRosters");
        if (rostersNode != null && rostersNode.isArray()) {
            for (JsonNode rosterNode : rostersNode) {
                availableForRosters.add(rosterNode.asText());
            }
        }

        return StarPlayerRef.builder()
                .uid(uid)
                .name(name)
                .cost(cost)
                .movement(new PlayerCharacteristic(ma))
                .strength(new PlayerCharacteristic(st))
                .agility(new PlayerCharacteristic(ag))
                .passing(new PlayerCharacteristic(pa))
                .armourValue(new PlayerCharacteristic(av))
                .playerType(playerType)
                .skills(resolvedSkills)
                .specialAbilityName(specialAbilityName)
                .specialAbilityDescription(specialAbilityDescription)
                .playsFor(resolvedLeagues)
                .availableForRosters(availableForRosters)
                .build();
    }

    /**
     * Formate un UID de skill en nom lisible (ex: LONER_4 -> Loner (4+))
     */
    private String formatSkillName(String skillUid) {
        if (skillUid.startsWith("LONER_")) {
            String value = skillUid.substring(6);
            return "Loner (" + value + "+)";
        }
        // Conversion générique: SNAKE_CASE -> Title Case
        return Arrays.stream(skillUid.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    /**
     * Parse une caractéristique depuis un JsonNode.
     * Supporte les formats: entier (6), string avec "+" ("2+", "9+"), ou "-" pour absence de caractéristique.
     *
     * @param node le noeud JSON contenant la valeur
     * @return la valeur entière (0 si "-" ou invalide)
     */
    private int parseCharacteristic(JsonNode node) {
        if (node == null) {
            return 0;
        }
        if (node.isInt()) {
            return node.asInt();
        }
        String text = node.asText();
        if (text == null || text.equals("-") || text.isEmpty()) {
            return 0;
        }
        // Retirer le "+" final si présent (ex: "2+" -> "2")
        String cleanValue = text.replace("+", "").trim();
        try {
            return Integer.parseInt(cleanValue);
        } catch (NumberFormatException e) {
            log.warn("[ReferenceAPI] Cannot parse characteristic value: {}", text);
            return 0;
        }
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
    // Méthodes d'accès - Leagues
    // ===========================

    public Optional<LeagueRef> getLeagueByUid(String uid, Locale locale) {
        return getData(locale).leagues.stream()
                .filter(l -> l.getUid().equals(uid))
                .findFirst();
    }

    public List<LeagueRef> getAllLeagues(Locale locale) {
        return getData(locale).leagues;
    }

    // ================================
    // Méthodes d'accès - Star Players
    // ================================

    public Optional<StarPlayerRef> getStarPlayerByUid(String uid, Locale locale) {
        return getData(locale).starPlayers.stream()
                .filter(sp -> sp.getUid().equals(uid))
                .findFirst();
    }

    public List<StarPlayerRef> getAllStarPlayers(Locale locale) {
        return getData(locale).starPlayers;
    }

    /**
     * Retourne les star players disponibles pour un roster donné.
     */
    public List<StarPlayerRef> getStarPlayersByRoster(String rosterId, Locale locale) {
        return getData(locale).starPlayers.stream()
                .filter(sp -> sp.canPlayFor(rosterId))
                .collect(Collectors.toList());
    }

    /**
     * Retourne les star players disponibles pour une league donnée.
     */
    public List<StarPlayerRef> getStarPlayersByLeague(String leagueUid, Locale locale) {
        return getData(locale).starPlayers.stream()
                .filter(sp -> sp.canPlayInLeague(leagueUid))
                .collect(Collectors.toList());
    }

    public int getStarPlayerCount(Locale locale) {
        return getData(locale).starPlayers.size();
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
        List<LeagueRef> leagues;
        List<TeamStaffRef> staff;
        List<SkillRef> skills;
        List<SkillCategoryRef> skillCategories;
        List<StarPlayerRef> starPlayers;
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

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    private static class LeaguesWrapper {
        @com.fasterxml.jackson.annotation.JsonProperty("leagues")
        private List<LeagueRef> leagues;

        public List<LeagueRef> getLeagues() { return leagues; }
        public void setLeagues(List<LeagueRef> leagues) { this.leagues = leagues; }
    }
}
