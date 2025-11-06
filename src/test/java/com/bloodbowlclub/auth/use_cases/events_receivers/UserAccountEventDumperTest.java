package com.bloodbowlclub.auth.use_cases.events_receivers;

import com.bloodbowlclub.WebApplication;
import com.bloodbowlclub.auth.domain.user_account.DraftUserAccount;
import com.bloodbowlclub.auth.domain.user_account.commands.RegisterCommand;
import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.auth.use_cases.event_receivers.UserAccountEventDumper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.DateService;
import com.bloodbowlclub.lib.services.ObjectMapperService;
import com.bloodbowlclub.test_utilities.dispatcher.FakeEventDispatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = WebApplication.class)
public class UserAccountEventDumperTest {

    @Autowired
    @Qualifier("EventStore")
    private EventStore eventRepo;

    private FakeEventDispatcher fakeEventBus = new FakeEventDispatcher();

    private String userId = "01E48SD97BMWHAW82D229T0C7K";

    private DraftUserAccount draftUserAccount;
    private DateService ds = new DateService();

    private void initUserAccount() {
        draftUserAccount = new DraftUserAccount();
        draftUserAccount.register(new RegisterCommand(
                userId,
                "testCoach",
                "bb@gmail.com",
                "mon_password"
        ));
    }

    @Test
    void DumperUserAccountEvent_succeed_and_save_event() {
        initUserAccount();
        Assertions.assertTrue(eventRepo.findBySubject(userId).isEmpty());
        UserAccountEventDumper eventDumper = new UserAccountEventDumper(eventRepo, fakeEventBus);
        AccountRegisteredEvent event = AccountRegisteredEvent.builder()
                .aggregateId(draftUserAccount.getId())
                .username("testUsername")
                .email("may@me.com")
                .password("pwd")
                .createdAt(ds.dateTimeFromMysql("2020-08-10 00:22:11").getValue())
                .build();
        eventDumper.receive(event);
        Assertions.assertFalse(eventRepo.findBySubject(userId).isEmpty());
        AccountRegisteredEvent found = (AccountRegisteredEvent) eventRepo.findBySubject(userId).getFirst().getData();
        Assertions.assertEquals(found.getAggregateId(), userId);
    }

    @Test
    void Test_event_serializer() throws JsonProcessingException {
        initUserAccount();
        Assertions.assertTrue(eventRepo.findBySubject(userId).isEmpty());
        AccountRegisteredEvent event = AccountRegisteredEvent.builder()
                .aggregateId(draftUserAccount.getId())
                .username("testUsername")
                .email("may@me.com")
                .password("pwd")
                .createdAt(ds.dateTimeFromMysql("2020-08-10 00:22:11").getValue())
                .build();

        ObjectMapperService mapperService = new ObjectMapperService();
        ObjectMapper mapper = mapperService.getMapper();
        var rawJson = mapper.writeValueAsString(event);
        HashMap map = mapper.readValue(rawJson, HashMap.class);
        map.remove("createdAt");
        map.remove("timeStampedAt");
        String json = mapper.writeValueAsString(map);
        Assertions.assertEquals("{\"aggregateId\":\"01E48SD97BMWHAW82D229T0C7K\",\"password\":\"pwd\",\"@class\":\"com.auth.domain.user_account.events.AccountRegisteredEvent\",\"createdBy\":\"01E48SD97BMWHAW82D229T0C7K\",\"email\":\"may@me.com\",\"username\":\"testUsername\"}", json);
    }

}
