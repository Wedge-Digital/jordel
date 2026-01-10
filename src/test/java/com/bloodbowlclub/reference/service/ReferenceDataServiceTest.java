package com.bloodbowlclub.reference.service;

import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.reference.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReferenceDataServiceTest extends TestCase {

    @Autowired
    @Qualifier("referenceDataServiceForAPI")
    private ReferenceDataService referenceDataService;

    @Test
    void testLoadRosters() {
        // Vérifier que des rosters sont chargés
        List<RosterRef> rosters = referenceDataService.getAllRosters();
        assertNotNull(rosters);
        assertFalse(rosters.isEmpty(), "Les rosters devraient être chargés");

        System.out.println("✅ Nombre de rosters chargés: " + rosters.size());
    }

    @Test
    void testGetRosterById() {
        // Vérifier qu'on peut récupérer un roster spécifique (HUMAN devrait exister)
        Optional<RosterRef> humanRoster = referenceDataService.getRosterById("HUMAN");
        assertTrue(humanRoster.isPresent(), "Le roster HUMAN devrait exister");

        RosterRef roster = humanRoster.get();
        assertEquals("HUMAN", roster.getUid());
        assertNotNull(roster.getName());
        assertNotNull(roster.getRerollCost());

        System.out.println("✅ Roster HUMAN chargé: " + roster.getName());
    }

    @Test
    void testPlayerDefinitionsWithCharacteristics() {
        // Vérifier que les joueurs ont toutes leurs caractéristiques
        Optional<RosterRef> humanRoster = referenceDataService.getRosterById("HUMAN");
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
        List<SkillRef> skills = referenceDataService.getAllSkills();
        assertNotNull(skills);
        assertFalse(skills.isEmpty(), "Les skills devraient être chargées");

        System.out.println("✅ Nombre de skills chargées: " + skills.size());
    }

    @Test
    void testLoadSkillCategories() {
        List<SkillCategoryRef> categories = referenceDataService.getAllSkillCategories();
        assertNotNull(categories);
        assertFalse(categories.isEmpty(), "Les catégories de skills devraient être chargées");

        // Vérifier que GENERAL existe
        Optional<SkillCategoryRef> general = referenceDataService.getSkillCategoryByUid("GENERAL");
        assertTrue(general.isPresent(), "La catégorie GENERAL devrait exister");

        System.out.println("✅ Nombre de catégories de skills: " + categories.size());
    }

    @Test
    void testLoadStaff() {
        List<TeamStaffRef> staff = referenceDataService.getAllStaff();
        assertNotNull(staff);
        assertFalse(staff.isEmpty(), "Les staff devraient être chargés");

        System.out.println("✅ Nombre de staff chargés: " + staff.size());
    }

    @Test
    void testLoadSpecialRules() {
        List<SpecialRuleRef> rules = referenceDataService.getAllSpecialRules();
        assertNotNull(rules);
        assertFalse(rules.isEmpty(), "Les règles spéciales devraient être chargées");

        System.out.println("✅ Nombre de règles spéciales chargées: " + rules.size());
    }
}
