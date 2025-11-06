package com.bloodbowlclub.auth.use_cases.policies;


import com.bloodbowlclub.WebApplication;
import com.bloodbowlclub.auth.domain.user_account.ActiveUserAccount;
import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.auth.domain.user_account.values.Email;
import com.bloodbowlclub.auth.domain.user_account.values.Password;
import com.bloodbowlclub.auth.domain.user_account.values.Username;
import com.bloodbowlclub.lib.persistance.event_store.EventEntityFactory;
import com.bloodbowlclub.lib.persistance.event_store.fake.FakeEventStore;
import com.bloodbowlclub.lib.persistance.read_cache.ReadEntity;
import com.bloodbowlclub.lib.persistance.read_cache.ReadRepository;
import com.bloodbowlclub.lib.services.ResultMap;
import com.bloodbowlclub.lib.tests.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

public class AgregateShallNotExistPolicyTest extends TestCase {

    private FakeEventStore fakeEventStore =  new FakeEventStore();
    private AgregateShallNotExistPolicy agregateShallNotExistPolicy = new AgregateShallNotExistPolicy(messageSource, fakeEventStore);

    void loadPredefinedData(String username) {
        AccountRegisteredEvent evt = new AccountRegisteredEvent(
                username,
                new Email("toto"),
                new Password("no_pwd")
        );
        fakeEventStore.save(EventEntityFactory.AnonymousEventEntity(evt));
        Assertions.assertEquals(1, fakeEventStore.findAll().size());
    }

    @Test
    void TestuserIdShallNotExistPolicy_fails_when_id_already_exists() {
        loadPredefinedData("Bagzz");
        ResultMap<?> emailCheck = agregateShallNotExistPolicy.check("Bagzz");
        Assertions.assertTrue(emailCheck.isFailure());
    }

    @Test
    void TestuserIdShallNotExistPolicy_succeed_when_email_does_not_exists() {
        ResultMap<?> emailCheck = agregateShallNotExistPolicy.check("Bagzz");
        Assertions.assertTrue(emailCheck.isSuccess());
    }

    @Test
    void Test_email_shall_not_exist_policy_succeed_when_other_email_is_present() {
        loadPredefinedData("Bagouze");
        ResultMap<?> emailCheck = agregateShallNotExistPolicy.check("Castor");
        Assertions.assertTrue(emailCheck.isSuccess());
    }

}
