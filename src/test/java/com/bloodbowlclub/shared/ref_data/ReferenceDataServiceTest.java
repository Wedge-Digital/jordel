package com.bloodbowlclub.shared.ref_data;

import com.bloodbowlclub.team_building.domain.roster.PlayerDefinition;
import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.bloodbowlclub.team_building.domain.team_staff.TeamStaff;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ReferenceDataService
 * Vérifie le chargement et l'accès aux données de référence
 * Tests sans dépendance au contexte Spring complet
 */
class ReferenceDataServiceTest {

    private ReferenceDataService referenceDataService;

    @BeforeEach
    void setUp() {
        // Créer le service manuellement pour éviter les problèmes de contexte Spring
        ResourceLoader resourceLoader = new DefaultResourceLoader();

        referenceDataService = new ReferenceDataService(resourceLoader);

        // Charger les données
        referenceDataService.loadReferenceData();
    }

    // ===========================
    // Tests - Chargement des données
    // ===========================

    @Test
    @DisplayName("Les rosters doivent être chargés au démarrage")
    void testRostersAreLoaded() {
        List<Roster> rosters = referenceDataService.getAllRosters();

        assertNotNull(rosters, "La liste des rosters ne doit pas être null");
        assertFalse(rosters.isEmpty(), "La liste des rosters ne doit pas être vide");
        assertTrue(rosters.size() > 0, "Au moins un roster doit être chargé");
        assertEquals(30, rosters.size(), "Au moins un roster doit être chargé");

        rosters.stream().forEach(roster -> {
            Assertions.assertTrue(roster.isValid());
        });
    }

    @Test
    @DisplayName("Les players doivent être chargés au démarrage")
    void testPlayersAreLoaded() {
        int playerCount = referenceDataService.getPlayerCount();

        assertTrue(playerCount > 0, "Au moins un joueur doit être chargé");

        System.out.println("✓ Nombre de joueurs chargés: " + playerCount);
    }

    @Test
    @DisplayName("Les special rules doivent être chargées au démarrage")
    void testSpecialRulesAreLoaded() {
        List<ReferenceDataService.SpecialRule> rules = referenceDataService.getAllSpecialRules();

        assertNotNull(rules, "La liste des special rules ne doit pas être null");
        assertFalse(rules.isEmpty(), "La liste des special rules ne doit pas être vide");

        System.out.println("✓ Nombre de special rules chargées: " + rules.size());
    }

    @Test
    @DisplayName("Les staff doivent être chargés au démarrage")
    void testStaffAreLoaded() {
        List<TeamStaff> staff = referenceDataService.getAllStaff();

        assertNotNull(staff, "La liste des staff ne doit pas être null");
        assertFalse(staff.isEmpty(), "La liste des staff ne doit pas être vide");
        assertEquals(3, staff.size(), "Il doit y avoir 3 types de staff");

        System.out.println("✓ Nombre de staff chargés: " + staff.size());
    }

    // ===========================
    // Tests - Accès aux Rosters
    // ===========================

    @Test
    @DisplayName("Doit récupérer un roster par son ID")
    void testGetRosterById() {
        // Test avec HUMAN qui devrait exister
        Optional<Roster> humanRoster = referenceDataService.getRosterById("HUMAN");

        assertTrue(humanRoster.isPresent(), "Le roster HUMAN doit exister");
        assertEquals("HUMAN", humanRoster.get().getId(), "L'ID du roster doit être HUMAN");
        assertEquals("Human", humanRoster.get().getName().toString(), "Le nom du roster doit être Human");

        // Le roster doit contenir des joueurs
        assertNotNull(humanRoster.get().getPlayerDefinitions(), "Le roster doit avoir des joueurs");
        assertFalse(humanRoster.get().getPlayerDefinitions().isEmpty(), "Le roster doit avoir au moins un joueur");

        System.out.println("✓ Roster HUMAN trouvé avec " + humanRoster.get().getPlayerDefinitions().size() + " joueurs");
    }

    @Test
    @DisplayName("Doit retourner Optional.empty pour un roster inexistant")
    void testGetRosterByIdNotFound() {
        Optional<Roster> roster = referenceDataService.getRosterById("INEXISTANT_ROSTER");

        assertFalse(roster.isPresent(), "Un roster inexistant doit retourner Optional.empty");
    }

    @Test
    @DisplayName("Doit vérifier l'existence d'un roster")
    void testRosterExists() {
        assertTrue(referenceDataService.rosterExists("HUMAN"), "HUMAN doit exister");
        assertFalse(referenceDataService.rosterExists("INEXISTANT"), "INEXISTANT ne doit pas exister");
    }

    @Test
    @DisplayName("Doit compter le nombre de rosters")
    void testGetRosterCount() {
        int count = referenceDataService.getRosterCount();

        assertTrue(count > 0, "Le nombre de rosters doit être supérieur à 0");
        assertEquals(referenceDataService.getAllRosters().size(), count,
                "getRosterCount() doit retourner le même nombre que getAllRosters().size()");
    }

    @Test
    @DisplayName("Doit charger plusieurs rosters connus")
    void testMultipleKnownRosters() {
        String[] expectedRosters = {"HUMAN", "ORC", "DWARF", "DARK_ELF", "WOOD_ELF", "SKAVEN"};
        int foundCount = 0;

        for (String rosterName : expectedRosters) {
            Optional<Roster> roster = referenceDataService.getRosterById(rosterName);
            if (roster.isPresent()) {
                foundCount++;
                System.out.println("✓ Roster trouvé: " + rosterName + " (" + roster.get().getName() + ")");
                assertNotNull(roster.get().getPlayerDefinitions(),
                        "Le roster " + rosterName + " doit avoir des joueurs");
            }
        }

        assertTrue(foundCount > 0, "Au moins un roster connu doit être trouvé");
    }

    // ===========================
    // Tests - Accès aux Players
    // ===========================

    @Test
    @DisplayName("Doit récupérer un joueur par son ID")
    void testGetPlayerById() {
        // Test avec HUMAN__HUMAN_LINEMAN qui devrait exister
        Optional<PlayerDefinition> player = referenceDataService.getPlayerById("HUMAN__HUMAN_LINEMAN");

        assertTrue(player.isPresent(), "Le joueur HUMAN__HUMAN_LINEMAN doit exister");
        assertEquals("HUMAN__HUMAN_LINEMAN", player.get().getId(), "L'ID du joueur doit correspondre");
        assertNotNull(player.get().getName(), "Le joueur doit avoir un nom");
        assertNotNull(player.get().getPrice(), "Le joueur doit avoir un prix");
        assertNotNull(player.get().getMaxQuantity(), "Le joueur doit avoir une quantité max");

        System.out.println("✓ Joueur trouvé: " + player.get().getId() + " (" + player.get().getName() + ")");
    }

    @Test
    @DisplayName("Doit retourner Optional.empty pour un joueur inexistant")
    void testGetPlayerByIdNotFound() {
        Optional<PlayerDefinition> player = referenceDataService.getPlayerById("INEXISTANT__PLAYER");

        assertFalse(player.isPresent(), "Un joueur inexistant doit retourner Optional.empty");
    }

    @Test
    @DisplayName("Doit récupérer tous les joueurs d'un roster")
    void testGetPlayersByRoster() {
        List<PlayerDefinition> humanPlayers = referenceDataService.getPlayersByRoster("HUMAN");

        assertNotNull(humanPlayers, "La liste des joueurs ne doit pas être null");
        assertFalse(humanPlayers.isEmpty(), "Le roster HUMAN doit avoir des joueurs");

        System.out.println("✓ Nombre de joueurs pour HUMAN: " + humanPlayers.size());

        // Vérifier que tous les joueurs ont un ID qui commence par HUMAN__
        for (PlayerDefinition player : humanPlayers) {
            assertTrue(player.getId().startsWith("HUMAN__"),
                    "Tous les joueurs du roster HUMAN doivent avoir un ID qui commence par HUMAN__");
        }
    }

    @Test
    @DisplayName("Doit retourner une liste vide pour un roster inexistant")
    void testGetPlayersByRosterNotFound() {
        List<PlayerDefinition> players = referenceDataService.getPlayersByRoster("INEXISTANT");

        assertNotNull(players, "La liste ne doit pas être null");
        assertTrue(players.isEmpty(), "La liste doit être vide pour un roster inexistant");
    }

    @Test
    @DisplayName("Doit compter le nombre total de joueurs")
    void testGetPlayerCount() {
        int count = referenceDataService.getPlayerCount();

        assertTrue(count > 0, "Le nombre de joueurs doit être supérieur à 0");

        // Le nombre total de joueurs doit être la somme de tous les joueurs de tous les rosters
        int totalPlayersInRosters = referenceDataService.getAllRosters().stream()
                .mapToInt(roster -> roster.getPlayerDefinitions().size())
                .sum();

        assertEquals(totalPlayersInRosters, count,
                "Le nombre total de joueurs doit correspondre à la somme des joueurs de tous les rosters");
    }

    // ===========================
    // Tests - Accès aux Special Rules
    // ===========================

    @Test
    @DisplayName("Doit récupérer une special rule par son UID")
    void testGetSpecialRuleByUid() {
        // On va tester avec la première règle disponible
        List<ReferenceDataService.SpecialRule> allRules = referenceDataService.getAllSpecialRules();

        if (!allRules.isEmpty()) {
            ReferenceDataService.SpecialRule firstRule = allRules.get(0);
            String uid = firstRule.getUid();

            Optional<ReferenceDataService.SpecialRule> rule = referenceDataService.getSpecialRuleByUid(uid);

            assertTrue(rule.isPresent(), "La règle doit être trouvée");
            assertEquals(uid, rule.get().getUid(), "L'UID doit correspondre");
            assertNotNull(rule.get().getLabel(), "La règle doit avoir un label");

            System.out.println("✓ Special rule trouvée: " + uid + " = " + rule.get().getLabel());
        }
    }

    @Test
    @DisplayName("Doit retourner Optional.empty pour une special rule inexistante")
    void testGetSpecialRuleByUidNotFound() {
        Optional<ReferenceDataService.SpecialRule> rule =
                referenceDataService.getSpecialRuleByUid("INEXISTANT_RULE");

        assertFalse(rule.isPresent(), "Une règle inexistante doit retourner Optional.empty");
    }

    // ===========================
    // Tests - Accès aux Staff
    // ===========================

    @Test
    @DisplayName("Doit récupérer un staff par son UID")
    void testGetStaffByUid() {
        Optional<TeamStaff> apothecary = referenceDataService.getStaffByUid("APOTHECARY");

        assertTrue(apothecary.isPresent(), "Le staff APOTHECARY doit exister");
        assertEquals("APOTHECARY", apothecary.get().getId(), "L'UID doit correspondre");
        assertEquals("Apothecary", apothecary.get().getName().toString(), "Le nom doit être Apothecary");
        assertEquals(50, apothecary.get().getPrice().getValue(), "Le prix doit être 50");
        assertEquals(1, apothecary.get().getMaxQuantity().getValue(), "La quantité max doit être 1");

        System.out.println("✓ Staff trouvé: " + apothecary.get().getId() + " (" + apothecary.get().getName() + ")");
    }

    @Test
    @DisplayName("Doit récupérer tous les staff de base")
    void testGetAllBaseStaff() {
        List<TeamStaff> allStaff = referenceDataService.getAllStaff();

        assertEquals(3, allStaff.size(), "Il doit y avoir 3 types de staff");

        // Vérifier que les 3 staff de base sont présents
        assertTrue(referenceDataService.getStaffByUid("APOTHECARY").isPresent(), "APOTHECARY doit être présent");
        assertTrue(referenceDataService.getStaffByUid("CHEERLEADERS").isPresent(), "CHEERLEADERS doit être présent");
        assertTrue(referenceDataService.getStaffByUid("COACH_ASSISTANTS").isPresent(), "COACH_ASSISTANTS doit être présent");

        System.out.println("✓ Tous les staff de base sont présents");
    }

    @Test
    @DisplayName("Doit retourner Optional.empty pour un staff inexistant")
    void testGetStaffByUidNotFound() {
        Optional<TeamStaff> staff = referenceDataService.getStaffByUid("INEXISTANT_STAFF");

        assertFalse(staff.isPresent(), "Un staff inexistant doit retourner Optional.empty");
    }

    @Test
    @DisplayName("Doit vérifier les prix des staff")
    void testStaffPrices() {
        Optional<TeamStaff> apothecary = referenceDataService.getStaffByUid("APOTHECARY");
        Optional<TeamStaff> cheerleaders = referenceDataService.getStaffByUid("CHEERLEADERS");
        Optional<TeamStaff> coaches = referenceDataService.getStaffByUid("COACH_ASSISTANTS");

        assertTrue(apothecary.isPresent() && apothecary.get().getPrice().getValue() == 50, "Apothecary doit coûter 50k");
        assertTrue(cheerleaders.isPresent() && cheerleaders.get().getPrice().getValue() == 10, "Cheerleaders doivent coûter 10k");
        assertTrue(coaches.isPresent() && coaches.get().getPrice().getValue() == 10, "Coach Assistants doivent coûter 10k");

        System.out.println("✓ Tous les prix sont corrects");
    }

    // ===========================
    // Tests - Immutabilité
    // ===========================

    @Test
    @DisplayName("La liste des rosters doit être immuable")
    void testRosterListIsImmutable() {
        List<Roster> rosters = referenceDataService.getAllRosters();

        assertThrows(UnsupportedOperationException.class, () -> {
            rosters.add(null);
        }, "La liste des rosters doit être immuable");

        System.out.println("✓ Liste des rosters confirmée immuable");
    }

    @Test
    @DisplayName("La liste des special rules doit être immuable")
    void testSpecialRulesListIsImmutable() {
        List<ReferenceDataService.SpecialRule> rules = referenceDataService.getAllSpecialRules();

        assertThrows(UnsupportedOperationException.class, () -> {
            rules.add(null);
        }, "La liste des special rules doit être immuable");

        System.out.println("✓ Liste des special rules confirmée immuable");
    }

    @Test
    @DisplayName("La liste des staff doit être immuable")
    void testStaffListIsImmutable() {
        List<TeamStaff> staff = referenceDataService.getAllStaff();

        assertThrows(UnsupportedOperationException.class, () -> {
            staff.add(null);
        }, "La liste des staff doit être immuable");

        System.out.println("✓ Liste des staff confirmée immuable");
    }

    // ===========================
    // Tests - Validation des données
    // ===========================

    @Test
    @DisplayName("Tous les rosters doivent avoir un ID valide")
    void testAllRostersHaveValidId() {
        List<Roster> rosters = referenceDataService.getAllRosters();

        for (Roster roster : rosters) {
            assertNotNull(roster.getId(), "Chaque roster doit avoir un ID");
            assertFalse(roster.getId().isEmpty(), "L'ID du roster ne doit pas être vide");
            assertTrue(roster.getId().matches("^[A-Z][A-Z0-9_ ]*$"),
                    "L'ID du roster doit être en majuscules: " + roster.getId());
        }

        System.out.println("✓ Tous les " + rosters.size() + " rosters ont un ID valide");
    }

    @Test
    @DisplayName("Tous les joueurs doivent avoir un ID valide au format TEAM__POSITION")
    void testAllPlayersHaveValidId() {
        List<Roster> rosters = referenceDataService.getAllRosters();
        int totalPlayers = 0;

        for (Roster roster : rosters) {
            for (PlayerDefinition player : roster.getPlayerDefinitions()) {
                totalPlayers++;
                assertNotNull(player.getId(), "Chaque joueur doit avoir un ID");
                assertTrue(player.getId().contains("__"),
                        "L'ID du joueur doit contenir '__': " + player.getId());
                assertTrue(player.getId().matches("^[A-Z'][A-Z0-9_' -]*__[A-Z'][A-Z0-9_' -]*$"),
                        "L'ID du joueur doit être au format TEAM__POSITION: " + player.getId());
            }
        }

        System.out.println("✓ Tous les " + totalPlayers + " joueurs ont un ID valide");
    }

    @Test
    @DisplayName("Tous les rosters doivent avoir au moins un joueur")
    void testAllRostersHaveAtLeastOnePlayer() {
        List<Roster> rosters = referenceDataService.getAllRosters();

        for (Roster roster : rosters) {
            assertNotNull(roster.getPlayerDefinitions(),
                    "Le roster " + roster.getId() + " doit avoir une liste de joueurs");
            assertFalse(roster.getPlayerDefinitions().isEmpty(),
                    "Le roster " + roster.getId() + " doit avoir au moins un joueur");
        }

        System.out.println("✓ Tous les rosters ont au moins un joueur");
    }

    @Test
    @DisplayName("Tous les joueurs doivent avoir un prix et une quantité max valides")
    void testAllPlayersHaveValidPriceAndQuantity() {
        List<Roster> rosters = referenceDataService.getAllRosters();

        for (Roster roster : rosters) {
            for (PlayerDefinition player : roster.getPlayerDefinitions()) {
                assertNotNull(player.getPrice(),
                        "Le joueur " + player.getId() + " doit avoir un prix");
                assertNotNull(player.getMaxQuantity(),
                        "Le joueur " + player.getId() + " doit avoir une quantité max");
                assertTrue(player.getMaxQuantity().getValue() > 0,
                        "La quantité max du joueur " + player.getId() + " doit être > 0");
            }
        }

        System.out.println("✓ Tous les joueurs ont un prix et une quantité max valides");
    }

    // ===========================
    // Tests - Affichage pour debug
    // ===========================

    @Test
    @DisplayName("Afficher un résumé des données chargées")
    void testDisplayLoadedDataSummary() {
        System.out.println("\n========================================");
        System.out.println("Résumé des données de référence chargées");
        System.out.println("========================================");
        System.out.println("Rosters: " + referenceDataService.getRosterCount());
        System.out.println("Joueurs: " + referenceDataService.getPlayerCount());
        System.out.println("Special Rules: " + referenceDataService.getAllSpecialRules().size());
        System.out.println("Staff: " + referenceDataService.getAllStaff().size());

        System.out.println("\n--- Tous les rosters chargés ---");
        referenceDataService.getAllRosters()
                .forEach(roster -> {
                    System.out.println("  • " + roster.getId() + " (" + roster.getName() + ") - "
                            + roster.getPlayerDefinitions().size() + " joueurs - "
                            + "Reroll: " + roster.getRerollPrice().getValue() + "k");
                });

        System.out.println("\n--- Quelques joueurs du roster HUMAN ---");
        referenceDataService.getPlayersByRoster("HUMAN").stream()
                .limit(5)
                .forEach(player -> {
                    System.out.println("  • " + player.getId() + " (" + player.getName() + ") - "
                            + player.getPrice().getValue() + "k - Max: " + player.getMaxQuantity().getValue());
                });

        System.out.println("\n--- Staff disponibles ---");
        referenceDataService.getAllStaff()
                .forEach(staff -> {
                    System.out.println("  • " + staff.getId() + " (" + staff.getName() + ") - "
                            + staff.getPrice().getValue() + "k - Max: " + staff.getMaxQuantity().getValue());
                });

        System.out.println("========================================\n");
    }
}
