package com.bloodbowlclub.reference.service;

import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.reference.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ReferenceDataServiceStatsTest extends TestCase {

    @Autowired
    @Qualifier("referenceDataServiceForAPI")
    private ReferenceDataService referenceDataService;

    @Test
    void displayLoadedDataStats() {
        System.out.println("\n========================================");
        System.out.println("STATISTIQUES DES DONNÉES DE RÉFÉRENCE");
        System.out.println("========================================\n");

        // Locales supportées
        System.out.println("🌍 LOCALES SUPPORTÉES: " + referenceDataService.getSupportedLocales());
        System.out.println();

        // Test pour chaque locale
        for (java.util.Locale locale : referenceDataService.getSupportedLocales()) {
            System.out.println("--- " + locale.getDisplayLanguage() + " (" + locale.getLanguage() + ") ---");

            // Rosters
            List<RosterRef> rosters = referenceDataService.getAllRosters(locale);
            System.out.println("📚 ROSTERS: " + rosters.size());
            if (!rosters.isEmpty()) {
                RosterRef firstRoster = rosters.get(0);
                System.out.println("   Exemple: " + firstRoster.getName() + " (" + firstRoster.getUid() + ")");
                System.out.println("   - Tier: " + (firstRoster.getTier() != null ? firstRoster.getTier().getValue() : "N/A"));
                System.out.println("   - Joueurs disponibles: " + firstRoster.getAvailablePlayers().size());
                System.out.println("   - Staff autorisé: " + firstRoster.getAllowedStaffUids().size());

                if (!firstRoster.getAvailablePlayers().isEmpty()) {
                    PlayerDefinitionRef player = firstRoster.getAvailablePlayers().get(0);
                    System.out.println("   - Exemple joueur: " + player.getPositionName());
                    System.out.println("     MA:" + player.getMovement().getValue() +
                        " ST:" + player.getStrength().getValue() +
                        " AG:" + player.getAgility().getValue() +
                        " PA:" + player.getPassing().getValue() +
                        " AV:" + player.getArmourValue().getValue());
                    System.out.println("     Compétences: " + player.getSkills().getSkillUids().size());
                }
            }

            // Players
            int totalPlayers = referenceDataService.getPlayerCount(locale);
            System.out.println("\n⚽ JOUEURS (toutes équipes): " + totalPlayers);

            // Skills
            List<SkillRef> skills = referenceDataService.getAllSkills(locale);
            System.out.println("\n💪 COMPÉTENCES: " + skills.size());
            if (!skills.isEmpty()) {
                SkillRef firstSkill = skills.get(0);
                System.out.println("   Exemple: " + firstSkill.getName() + " (" + firstSkill.getUid() + ")");
                System.out.println("   - Catégorie: " + firstSkill.getCategory());
                System.out.println("   - Type: " + firstSkill.getType());
            }

            // Skill Categories
            List<SkillCategoryRef> categories = referenceDataService.getAllSkillCategories(locale);
            System.out.println("\n🏷️  CATÉGORIES DE COMPÉTENCES: " + categories.size());
            categories.forEach(cat -> System.out.println("   - " + cat.getLabel() + " (" + cat.getUid() + ")"));

            // Staff
            List<TeamStaffRef> staff = referenceDataService.getAllStaff(locale);
            System.out.println("\n👥 STAFF: " + staff.size());
            if (!staff.isEmpty()) {
                TeamStaffRef firstStaff = staff.get(0);
                System.out.println("   Exemple: " + firstStaff.getName() + " (" + firstStaff.getUid() + ")");
                System.out.println("   - Prix: " + firstStaff.getPriceInGold() + " gold");
                System.out.println("   - Max: " + firstStaff.getMaxQuantity());
            }

            // Special Rules
            List<SpecialRuleRef> rules = referenceDataService.getAllSpecialRules(locale);
            System.out.println("\n📋 RÈGLES SPÉCIALES: " + rules.size());
            System.out.println();
        }

        System.out.println("========================================");
        System.out.println("✅ TOUTES LES DONNÉES SONT CHARGÉES");
        System.out.println("========================================\n");
    }
}
