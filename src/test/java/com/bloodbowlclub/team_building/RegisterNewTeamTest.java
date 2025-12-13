package com.bloodbowlclub.team_building;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.team_building.domain.BaseTeam;
import com.bloodbowlclub.team_building.domain.DraftTeam;
import com.bloodbowlclub.team_building.domain.commands.RegisterNewTeamCommand;
import com.bloodbowlclub.team_building.domain.events.DraftTeamRegisteredEvent;
import com.bloodbowlclub.test_utilities.cloudinary.CloudinaryUrlBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterNewTeamTest {

    private BaseTeam createValidTeam() {
        BaseTeam baseTeam = new BaseTeam();
        Assertions.assertEquals(0, baseTeam.domainEvents().size());
        RegisterNewTeamCommand newTeamCommand = new RegisterNewTeamCommand("01KCAA6DBY2B3M8TKEV7GH5JNN", "Team Name", CloudinaryUrlBuilder.validUrl);
        ResultMap<Void> teamRegistration = baseTeam.registerNewTeam(newTeamCommand);
        Assertions.assertTrue(teamRegistration.isSuccess());
        Assertions.assertEquals(1, baseTeam.domainEvents().size());
        return baseTeam;
    }

    @Test
    @DisplayName("When creating a new team, I should be able to choose a name, and a Logo")
    public void testTeamCreation() {
        DomainEvent domainEvent = createValidTeam().domainEvents().get(0);
        Assertions.assertEquals("DraftTeamRegisteredEvent", domainEvent.getClass().getSimpleName());
    }

    @Test
    @DisplayName("When hydrating a BaseTeam from a single DraftTeamRegisteredEvent, shall have a draft team")
    public void TestDraftTeamHydratation() {
        DraftTeamRegisteredEvent domainEvent = (DraftTeamRegisteredEvent) createValidTeam().domainEvents().get(0);
        BaseTeam freshteam = new BaseTeam();
        Result<AggregateRoot> hydratation = freshteam.hydrate(List.of(domainEvent));
        BaseTeam hydrated = (BaseTeam) hydratation.getValue();
        Assertions.assertTrue(hydrated.isDraftTeam());
        Assertions.assertEquals(hydrated.getTeamId(), domainEvent.getTeam().getTeamId());
        Assertions.assertEquals(hydrated.getName(), domainEvent.getTeam().getName());
        Assertions.assertEquals(hydrated.getLogoUrl(), domainEvent.getTeam().getLogoUrl());
    }


    @Test
    @DisplayName("When creating a new team, I should not be able to choose a shitty name, a no Id, and a dumb Logo")
    public void testTeamCreationShouldFails() {
        BaseTeam baseTeam = new BaseTeam();
        RegisterNewTeamCommand newTeamCommand = new RegisterNewTeamCommand("coin", "sh", CloudinaryUrlBuilder.invalidUrl);
        ResultMap<Void> teamRegistration = baseTeam.registerNewTeam(newTeamCommand);
        Assertions.assertFalse(teamRegistration.isSuccess());
        ResultMap<Void> errors = baseTeam.validationErrors();
        Map<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("teamId", "Identifiant non valide, doit être un ULID");
        expectedErrors.put("name", "must be between 3 and 100 characters");
        expectedErrors.put("logoUrl", "L'Url n'est pas une url Cloudinary");
        Assertions.assertEquals(errors.errorMap(), expectedErrors);
    }
}
