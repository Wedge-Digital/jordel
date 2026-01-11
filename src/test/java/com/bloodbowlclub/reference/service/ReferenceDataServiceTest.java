package com.bloodbowlclub.reference.service;

import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.reference.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReferenceDataServiceTest extends TestCase {

    @Autowired
    @Qualifier("referenceDataServiceForAPI")
    private ReferenceDataService referenceDataService;

    @Test
    void testLoadRostersEnglish() {
        // Vérifier que des rosters sont chargés en anglais
        List<RosterRef> rosters = referenceDataService.getAllRosters(Locale.ENGLISH);
        assertNotNull(rosters);
        assertFalse(rosters.isEmpty(), "Les rosters devraient être chargés");

        System.out.println("✅ Nombre de rosters chargés (EN): " + rosters.size());
    }

    @Test
    void testLoadRostersFrench() {
        // Vérifier que des rosters sont chargés en français
        List<RosterRef> rosters = referenceDataService.getAllRosters(Locale.FRENCH);
        assertNotNull(rosters);
        assertFalse(rosters.isEmpty(), "Les rosters devraient être chargés");

        System.out.println("✅ Nombre de rosters chargés (FR): " + rosters.size());
    }

    @Test
    void testGetRosterByIdEnglish() {
        // Vérifier qu'on peut récupérer un roster spécifique (HUMAN devrait exister)
        Optional<RosterRef> humanRoster = referenceDataService.getRosterById("HUMAN", Locale.ENGLISH);
        assertTrue(humanRoster.isPresent(), "Le roster HUMAN devrait exister");

        RosterRef roster = humanRoster.get();
        assertEquals("HUMAN", roster.getUid());
        assertNotNull(roster.getName());
        assertNotNull(roster.getRerollCost());

        System.out.println("✅ Roster HUMAN chargé (EN): " + roster.getName());
    }

    @Test
    void testGetRosterByIdFrench() {
        // Vérifier qu'on peut récupérer un roster spécifique en français
        Optional<RosterRef> humanRoster = referenceDataService.getRosterById("HUMAN", Locale.FRENCH);
        assertTrue(humanRoster.isPresent(), "Le roster HUMAN devrait exister");

        RosterRef roster = humanRoster.get();
        assertEquals("HUMAN", roster.getUid());
        assertNotNull(roster.getName());
        assertNotNull(roster.getRerollCost());

        System.out.println("✅ Roster HUMAN chargé (FR): " + roster.getName());
    }

    @Test
    void testPlayerDefinitionsWithCharacteristics() {
        // Vérifier que les joueurs ont toutes leurs caractéristiques
        Optional<RosterRef> humanRoster = referenceDataService.getRosterById("HUMAN", Locale.ENGLISH);
        assertTrue(humanRoster.isPresent());

        List<PlayerDefinitionRef> players = humanRoster.get().getAvailablePlayers();
        assertFalse(players.isEmpty(), "Le roster HUMAN devrait avoir des joueurs");

        // Vérifier le premier joueur
        PlayerDefinitionRef player = players.get(0);
        assertNotNull(player.getMovement(), "Le joueur devrait avoir une caractéristique Movement");
        assertNotNull(player.getStrength(), "Le joueur devrait avoir une caractéristique Strength");
        assertNotNull(player.getAgility(), "Le joueur devrait avoir une caractéristique Agility");
        assertNotNull(player.getPassing(), "Le joueur devrait avoir une caractéristique Passing");
        assertNotNull(player.getArmourValue(), "Le joueur devrait avoir une caractéristique Armour Value");

        System.out.println("✅ Joueur: " + player.getPositionName() +
            " - MA:" + player.getMovement().getValue() +
            " ST:" + player.getStrength().getValue() +
            " AG:" + player.getAgility().getValue() +
            " PA:" + player.getPassing().getValue() +
            " AV:" + player.getArmourValue().getValue());
    }

    @Test
    void testLoadSkills() {
        List<SkillRef> skills = referenceDataService.getAllSkills(Locale.ENGLISH);
        assertNotNull(skills);
        assertFalse(skills.isEmpty(), "Les skills devraient être chargées");

        System.out.println("✅ Nombre de skills chargées (EN): " + skills.size());
    }

    @Test
    void testLoadSkillCategories() {
        List<SkillCategoryRef> categories = referenceDataService.getAllSkillCategories(Locale.ENGLISH);
        assertNotNull(categories);
        assertFalse(categories.isEmpty(), "Les catégories de skills devraient être chargées");

        // Vérifier que GENERAL existe
        Optional<SkillCategoryRef> general = referenceDataService.getSkillCategoryByUid("GENERAL", Locale.ENGLISH);
        assertTrue(general.isPresent(), "La catégorie GENERAL devrait exister");

        System.out.println("✅ Nombre de catégories de skills: " + categories.size());
    }

    @Test
    void testLoadStaff() {
        List<TeamStaffRef> staff = referenceDataService.getAllStaff(Locale.ENGLISH);
        assertNotNull(staff);
        assertFalse(staff.isEmpty(), "Les staff devraient être chargés");

        System.out.println("✅ Nombre de staff chargés (EN): " + staff.size());
    }

    @Test
    void testLoadSpecialRules() {
        List<SpecialRuleRef> rules = referenceDataService.getAllSpecialRules(Locale.ENGLISH);
        assertNotNull(rules);
        assertFalse(rules.isEmpty(), "Les règles spéciales devraient être chargées");

        System.out.println("✅ Nombre de règles spéciales chargées (EN): " + rules.size());
    }

    @Test
    void testSupportedLocales() {
        var locales = referenceDataService.getSupportedLocales();
        assertNotNull(locales);
        assertTrue(locales.contains(Locale.ENGLISH), "English devrait être supporté");
        assertTrue(locales.contains(Locale.FRENCH), "French devrait être supporté");

        System.out.println("✅ Locales supportées: " + locales);
    }

    @Test
    void testRosterSpecialRulesAreResolvedAsObjects() {
        // Vérifier qu'un roster avec special rules les a bien en tant qu'objets complets (pas juste des UIDs)
        // TOMB KINGS et VAMPIRE ont la rule MASTERS_OF_UNDEATH
        Optional<RosterRef> tombKingsRoster = referenceDataService.getRosterById("TOMB KINGS", Locale.ENGLISH);
        assertTrue(tombKingsRoster.isPresent(), "Le roster TOMB KINGS devrait exister");

        RosterRef roster = tombKingsRoster.get();
        assertTrue(roster.hasSpecialRules(), "Le roster TOMB KINGS devrait avoir des special rules");

        List<SpecialRuleRef> specialRules = roster.getSpecialRules();
        assertFalse(specialRules.isEmpty(), "Le roster TOMB KINGS devrait avoir au moins une special rule");

        // Vérifier que les special rules sont des objets complets avec uid ET label
        SpecialRuleRef firstRule = specialRules.get(0);
        assertNotNull(firstRule.getUid(), "La special rule devrait avoir un uid");
        assertNotNull(firstRule.getLabel(), "La special rule devrait avoir un label (pas juste un uid)");

        System.out.println("✅ Special rule résolue: " + firstRule.getUid() + " => " + firstRule.getLabel());
    }

    @Test
    void testLoadLeagues() {
        List<LeagueRef> leagues = referenceDataService.getAllLeagues(Locale.ENGLISH);
        assertNotNull(leagues);
        assertFalse(leagues.isEmpty(), "Les leagues devraient être chargées");

        // Vérifier qu'il y a bien 10 leagues
        assertEquals(10, leagues.size(), "Il devrait y avoir 10 leagues");

        System.out.println("✅ Nombre de leagues chargées (EN): " + leagues.size());
    }

    @Test
    void testGetLeagueByUid() {
        // Vérifier qu'on peut récupérer une league spécifique
        Optional<LeagueRef> oldWorldClassic = referenceDataService.getLeagueByUid("OLD_WORLD_CLASSIC", Locale.ENGLISH);
        assertTrue(oldWorldClassic.isPresent(), "La league OLD_WORLD_CLASSIC devrait exister");

        LeagueRef league = oldWorldClassic.get();
        assertEquals("OLD_WORLD_CLASSIC", league.getUid());
        assertEquals("Old World Classic", league.getLabel());

        System.out.println("✅ League récupérée: " + league.getUid() + " => " + league.getLabel());
    }

    @Test
    void testRosterLeaguesAreResolvedAsObjects() {
        // Vérifier qu'un roster avec leagues les a bien en tant qu'objets complets (pas juste des UIDs)
        // HUMAN appartient à OLD_WORLD_CLASSIC
        Optional<RosterRef> humanRoster = referenceDataService.getRosterById("HUMAN", Locale.ENGLISH);
        assertTrue(humanRoster.isPresent(), "Le roster HUMAN devrait exister");

        RosterRef roster = humanRoster.get();
        assertTrue(roster.hasLeagues(), "Le roster HUMAN devrait avoir des leagues");

        List<LeagueRef> leagues = roster.getLeagues();
        assertFalse(leagues.isEmpty(), "Le roster HUMAN devrait avoir au moins une league");
        assertEquals(1, leagues.size(), "Le roster HUMAN devrait avoir exactement 1 league");

        // Vérifier que les leagues sont des objets complets avec uid ET label
        LeagueRef league = leagues.get(0);
        assertNotNull(league.getUid(), "La league devrait avoir un uid");
        assertNotNull(league.getLabel(), "La league devrait avoir un label (pas juste un uid)");
        assertEquals("OLD_WORLD_CLASSIC", league.getUid());
        assertEquals("Old World Classic", league.getLabel());

        System.out.println("✅ League résolue: " + league.getUid() + " => " + league.getLabel());
    }

    @Test
    void testRosterWithMultipleLeagues() {
        // Vérifier qu'un roster avec plusieurs leagues les a toutes résolues
        // CHAOS DWARF appartient à BADLANDS_BRAWL et CHAOS_CLASH
        Optional<RosterRef> chaosDwarfRoster = referenceDataService.getRosterById("CHAOS DWARF", Locale.ENGLISH);
        assertTrue(chaosDwarfRoster.isPresent(), "Le roster CHAOS DWARF devrait exister");

        RosterRef roster = chaosDwarfRoster.get();
        assertTrue(roster.hasLeagues(), "Le roster CHAOS DWARF devrait avoir des leagues");

        List<LeagueRef> leagues = roster.getLeagues();
        assertEquals(2, leagues.size(), "Le roster CHAOS DWARF devrait avoir exactement 2 leagues");

        // Vérifier que les deux leagues sont bien résolues
        boolean hasBadlandsBrawl = leagues.stream().anyMatch(l -> l.getUid().equals("BADLANDS_BRAWL"));
        boolean hasChaosClash = leagues.stream().anyMatch(l -> l.getUid().equals("CHAOS_CLASH"));

        assertTrue(hasBadlandsBrawl, "Le roster CHAOS DWARF devrait avoir la league BADLANDS_BRAWL");
        assertTrue(hasChaosClash, "Le roster CHAOS DWARF devrait avoir la league CHAOS_CLASH");

        System.out.println("✅ Leagues multiples résolues: " + leagues.stream().map(LeagueRef::getLabel).toList());
    }
}
