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
}
