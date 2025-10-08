package com.auth.use_cases.events_receivers;

import com.auth.domain.user_account.DraftUserAccount;
import com.auth.domain.user_account.commands.RegisterCommand;
import com.auth.io.persistance.write.BusinessEventRepository;
import com.auth.use_cases.event_receivers.user_account.UserAccountEventDumper;
import com.auth.domain.user_account.events.AccountRegisteredEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.domain.events.InvalidAggregateRoot;
import com.shared.services.DateService;
import com.test_utilities.dispatcher.FakeEventDispatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserAccountEventDumperTest {

    @Autowired
    private BusinessEventRepository eventRepo;

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
        Assertions.assertTrue(eventRepo.findBySource(userId).isEmpty());
        UserAccountEventDumper eventDumper = new UserAccountEventDumper(eventRepo, fakeEventBus);
        AccountRegisteredEvent event = new AccountRegisteredEvent(draftUserAccount.getId(), "testUsername", "may@me.com", "pwd", ds.dateTimeFromMysql("2020-08-10 00:22:11").getValue());
        eventDumper.receive(event);
        Assertions.assertFalse(eventRepo.findBySource(userId).isEmpty());
        AccountRegisteredEvent found = (AccountRegisteredEvent) eventRepo.findBySource(userId).getFirst().getData();
        Assertions.assertEquals(found.getAggregateId(), userId);
    }

    @Test
    void Test_event_serializer() throws InvalidAggregateRoot, JsonProcessingException {
        initUserAccount();
        Assertions.assertTrue(eventRepo.findBySource(userId).isEmpty());
        AccountRegisteredEvent event = new AccountRegisteredEvent(draftUserAccount.getId(), "testUsername", "may@me.com", "pwd", ds.dateTimeFromMysql("2020-08-10 00:22:11").getValue());
        var mapper = new ObjectMapper();
        var json = mapper.writeValueAsString(event);
        Assertions.assertEquals(json, "{\"@class\":\"com.auth.domain.user_account.events.AccountRegisteredEvent\",\"aggregateId\":\"01E48SD97BMWHAW82D229T0C7K\",\"username\":\"testUsername\",\"email\":\"may@me.com\",\"password\":\"pwd\",\"createdAt\":1597011731000}");
    }

}
