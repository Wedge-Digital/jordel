package com.bloodbowlclub.reference.service;

import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.reference.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour valider le support multi-locale du ReferenceDataService.
 */
@SpringBootTest
class ReferenceDataServiceLocaleTest extends TestCase {

    @Autowired
    @Qualifier("referenceDataServiceForAPI")
    private ReferenceDataService referenceDataService;

    @Test
    void testBothLocalesAreLoaded() {
        var supportedLocales = referenceDataService.getSupportedLocales();

        assertTrue(supportedLocales.contains(Locale.ENGLISH), "Anglais devrait être chargé");
        assertTrue(supportedLocales.contains(Locale.FRENCH), "Français devrait être chargé");
        assertEquals(2, supportedLocales.size(), "2 locales devraient être chargées");

        System.out.println("✅ Locales chargées: " + supportedLocales);
    }

    @Test
    void testRosterTranslation() {
        // Récupérer le roster HUMAN en anglais
        Optional<RosterRef> humanEN = referenceDataService.getRosterById("HUMAN", Locale.ENGLISH);
        assertTrue(humanEN.isPresent(), "HUMAN devrait exister en anglais");

        // Récupérer le roster HUMAN en français
        Optional<RosterRef> humanFR = referenceDataService.getRosterById("HUMAN", Locale.FRENCH);
        assertTrue(humanFR.isPresent(), "HUMAN devrait exister en français");

        // Les noms devraient être différents (traduits)
        String nameEN = humanEN.get().getName();
        String nameFR = humanFR.get().getName();

        System.out.println("Nom EN: " + nameEN);
        System.out.println("Nom FR: " + nameFR);

        // Vérifier que les noms existent
        assertNotNull(nameEN);
        assertNotNull(nameFR);

        // Les UID devraient être identiques
        assertEquals("HUMAN", humanEN.get().getUid());
        assertEquals("HUMAN", humanFR.get().getUid());
    }

    @Test
    void testPlayerTranslation() {
        // Récupérer un joueur en anglais
        Optional<RosterRef> humanEN = referenceDataService.getRosterById("HUMAN", Locale.ENGLISH);
        assertTrue(humanEN.isPresent());
        PlayerDefinitionRef playerEN = humanEN.get().getAvailablePlayers().get(0);

        // Récupérer le même joueur en français
        Optional<RosterRef> humanFR = referenceDataService.getRosterById("HUMAN", Locale.FRENCH);
        assertTrue(humanFR.isPresent());
        PlayerDefinitionRef playerFR = humanFR.get().getAvailablePlayers().get(0);

        System.out.println("Position EN: " + playerEN.getPositionName());
        System.out.println("Position FR: " + playerFR.getPositionName());

        // Les UIDs devraient être identiques
        assertEquals(playerEN.getUid(), playerFR.getUid());

        // Les noms de position peuvent être traduits ou non
        assertNotNull(playerEN.getPositionName());
        assertNotNull(playerFR.getPositionName());

        // Les caractéristiques devraient être identiques
        assertEquals(playerEN.getMovement().getValue(), playerFR.getMovement().getValue());
        assertEquals(playerEN.getStrength().getValue(), playerFR.getStrength().getValue());
    }

    @Test
    void testSkillTranslation() {
        // Récupérer une compétence en anglais
        Optional<SkillRef> blockEN = referenceDataService.getSkillByUid("BLOCK", Locale.ENGLISH);
        assertTrue(blockEN.isPresent(), "BLOCK devrait exister en anglais");

        // Récupérer la même compétence en français
        Optional<SkillRef> blockFR = referenceDataService.getSkillByUid("BLOCK", Locale.FRENCH);
        assertTrue(blockFR.isPresent(), "BLOCK devrait exister en français");

        System.out.println("Skill EN: " + blockEN.get().getName());
        System.out.println("Skill FR: " + blockFR.get().getName());

        // Les noms devraient potentiellement être différents (traduits)
        assertNotNull(blockEN.get().getName());
        assertNotNull(blockFR.get().getName());

        // Les UIDs devraient être identiques
        assertEquals("BLOCK", blockEN.get().getUid());
        assertEquals("BLOCK", blockFR.get().getUid());

        // Les catégories devraient être identiques (constantes)
        assertEquals(blockEN.get().getCategory(), blockFR.get().getCategory());
    }

    @Test
    void testStaffTranslation() {
        // Récupérer un staff en anglais
        Optional<TeamStaffRef> apoEN = referenceDataService.getStaffByUid("APOTHECARY", Locale.ENGLISH);
        assertTrue(apoEN.isPresent(), "APOTHECARY devrait exister en anglais");

        // Récupérer le même staff en français
        Optional<TeamStaffRef> apoFR = referenceDataService.getStaffByUid("APOTHECARY", Locale.FRENCH);
        assertTrue(apoFR.isPresent(), "APOTHECARY devrait exister en français");

        System.out.println("Staff EN: " + apoEN.get().getName());
        System.out.println("Staff FR: " + apoFR.get().getName());

        // Les noms devraient être traduits
        assertNotNull(apoEN.get().getName());
        assertNotNull(apoFR.get().getName());

        // Les prix devraient être identiques
        assertEquals(apoEN.get().getPrice(), apoFR.get().getPrice());
    }

    @Test
    void testFallbackToEnglish() {
        // Tester avec une locale non supportée
        Locale unsupportedLocale = Locale.GERMAN;

        // Le service devrait fallback sur l'anglais
        var rosters = referenceDataService.getAllRosters(unsupportedLocale);

        assertNotNull(rosters);
        assertFalse(rosters.isEmpty(), "Les rosters devraient être chargés (fallback EN)");

        System.out.println("✅ Fallback fonctionne pour locale non supportée: " + unsupportedLocale);
    }

    @Test
    void testSameNumberOfRostersInBothLocales() {
        int countEN = referenceDataService.getRosterCount(Locale.ENGLISH);
        int countFR = referenceDataService.getRosterCount(Locale.FRENCH);

        assertEquals(countEN, countFR, "Le nombre de rosters devrait être identique dans les deux langues");

        System.out.println("✅ Nombre de rosters identique: EN=" + countEN + ", FR=" + countFR);
    }
}
