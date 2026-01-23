package com.bloodbowlclub.team_building.use_cases;

import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.persistance.event_store.EventEntity;
import com.bloodbowlclub.lib.persistance.event_store.EventEntityFactory;
import com.bloodbowlclub.lib.persistance.event_store.fake.FakeEventStore;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.team_building.domain.commands.ChooseRulesetCommand;
import com.bloodbowlclub.team_building.domain.events.DraftTeamRegisteredEvent;
import com.bloodbowlclub.team_building.domain.events.RulesetSelectedEvent;
import com.bloodbowlclub.team_building.domain.ruleset.Ruleset;
import com.bloodbowlclub.team_building.domain.team.BaseTeam;
import com.bloodbowlclub.test_utilities.dispatcher.FakeEventDispatcher;
import com.bloodbowlclub.test_utilities.team_creation.RulesetCreator;
import com.bloodbowlclub.test_utilities.team_creation.TeamCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChooseRulesetCommandHandlerTest extends TestCase {

    FakeEventStore fakeEventStore = new FakeEventStore();
    FakeEventDispatcher fakeEventDispatcher = new FakeEventDispatcher();
    ChooseRulesetCommandHandler handler = new ChooseRulesetCommandHandler(fakeEventStore, fakeEventDispatcher);
    EventEntityFactory factory = new EventEntityFactory();
    RulesetCreator rulesetCreator = new RulesetCreator();

    @Test
    @DisplayName("Test successful ruleset selection for a draft team")
    void testSuccessfulRulesetSelection() {
        // Arrange - Créer une équipe draft et la persister dans le FakeEventStore
        BaseTeam baseTeam = TeamCreator.createBaseTeam();
        String teamId = baseTeam.getTeamId().toString();

        // Persister l'événement de création de l'équipe
        DraftTeamRegisteredEvent registeredEvent = (DraftTeamRegisteredEvent) baseTeam.domainEvents().get(0);
        EventEntity eventEntity = factory.build(registeredEvent);
        fakeEventStore.save(eventEntity);

        // Créer un ruleset valide
        Ruleset ruleset = rulesetCreator.createBasicRuleset();

        // Créer la commande
        ChooseRulesetCommand command = ChooseRulesetCommand.builder()
                .teamId(teamId)
                .ruleset(ruleset)
                .build();

        // Act
        ResultMap<Void> result = handler.handle(command);

        // Assert
        Assertions.assertTrue(result.isSuccess());

        // Vérifier que l'événement a été dispatché
        List<DomainEvent> dispatchedEvents = fakeEventDispatcher.getDispatchedEvents();
        Assertions.assertEquals(1, dispatchedEvents.size());
        DomainEvent event = dispatchedEvents.get(0);
        Assertions.assertTrue(event instanceof RulesetSelectedEvent);

        // Vérifier que l'événement a été sauvegardé dans l'EventStore
        List<EventEntity> allEvents = fakeEventStore.findAll();
        Assertions.assertEquals(2, allEvents.size()); // DraftTeamRegisteredEvent + RulesetSelectedEvent
    }

    @Test
    @DisplayName("Test ruleset selection fails when team not found")
    void testRulesetSelectionFailsWhenTeamNotFound() {
        // Arrange
        Ruleset ruleset = rulesetCreator.createBasicRuleset();
        String nonExistentTeamId = "01NONEXISTENT000000000000";

        ChooseRulesetCommand command = ChooseRulesetCommand.builder()
                .teamId(nonExistentTeamId)
                .ruleset(ruleset)
                .build();

        // Act
        ResultMap<Void> result = handler.handle(command);

        // Assert
        Assertions.assertTrue(result.isFailure());

        // Vérifier le message d'erreur
        Map<String, String> translatedErrors = result.getTranslatedErrorMap(messageSource, Locale.getDefault());
        String expectedError = messageSource.getMessage("team_creation.team_not_found", null, LocaleContextHolder.getLocale());
        Assertions.assertTrue(translatedErrors.containsValue(expectedError));

        // Vérifier qu'aucun événement n'a été dispatché
        List<DomainEvent> dispatchedEvents = fakeEventDispatcher.getDispatchedEvents();
        Assertions.assertEquals(0, dispatchedEvents.size());
    }

    @Test
    @DisplayName("Test ruleset selection fails with invalid ruleset")
    void testRulesetSelectionFailsWithInvalidRuleset() {
        // Arrange - Créer une équipe draft
        BaseTeam baseTeam = TeamCreator.createBaseTeam();
        String teamId = baseTeam.getTeamId().toString();

        // Persister l'événement de création de l'équipe
        DraftTeamRegisteredEvent registeredEvent = (DraftTeamRegisteredEvent) baseTeam.domainEvents().get(0);
        EventEntity eventEntity = factory.build(registeredEvent);
        fakeEventStore.save(eventEntity);

        // Créer un ruleset invalide
        Ruleset invalidRuleset = RulesetCreator.createBadTeamCreationRulset();

        // Créer la commande
        ChooseRulesetCommand command = ChooseRulesetCommand.builder()
                .teamId(teamId)
                .ruleset(invalidRuleset)
                .build();

        // Act
        ResultMap<Void> result = handler.handle(command);

        // Assert
        Assertions.assertTrue(result.isFailure());

        // Vérifier qu'aucun événement n'a été dispatché
        List<DomainEvent> dispatchedEvents = fakeEventDispatcher.getDispatchedEvents();
        Assertions.assertEquals(0, dispatchedEvents.size());

        // Vérifier que seul l'événement de registration est présent
        List<EventEntity> allEvents = fakeEventStore.findAll();
        Assertions.assertEquals(1, allEvents.size());
    }

    @Test
    @DisplayName("Test successful ruleset selection saves correct event data")
    void testRulesetSelectionSavesCorrectEventData() {
        // Arrange
        BaseTeam baseTeam = TeamCreator.createBaseTeam();
        String teamId = baseTeam.getTeamId().toString();

        DraftTeamRegisteredEvent registeredEvent = (DraftTeamRegisteredEvent) baseTeam.domainEvents().get(0);
        EventEntity eventEntity = factory.build(registeredEvent);
        fakeEventStore.save(eventEntity);

        Ruleset ruleset = rulesetCreator.createBasicRuleset();

        ChooseRulesetCommand command = ChooseRulesetCommand.builder()
                .teamId(teamId)
                .ruleset(ruleset)
                .build();

        // Act
        ResultMap<Void> result = handler.handle(command);

        // Assert
        Assertions.assertTrue(result.isSuccess());

        // Vérifier que l'événement dispatché contient les bonnes données
        List<DomainEvent> dispatchedEvents = fakeEventDispatcher.getDispatchedEvents();
        RulesetSelectedEvent event = (RulesetSelectedEvent) dispatchedEvents.get(0);
        Assertions.assertEquals(ruleset.getRulesetID(), event.getRuleset().getRulesetID());
        Assertions.assertEquals(ruleset.getName(), event.getRuleset().getName());
    }
}
