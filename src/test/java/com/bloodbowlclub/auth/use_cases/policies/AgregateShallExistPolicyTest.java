package com.bloodbowlclub.auth.use_cases.policies;

import com.bloodbowlclub.WebApplication;
import com.bloodbowlclub.auth.domain.user_account.ActiveUserAccount;
import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.auth.domain.user_account.values.Email;
import com.bloodbowlclub.auth.domain.user_account.values.Password;
import com.bloodbowlclub.auth.domain.user_account.values.Username;
import com.bloodbowlclub.lib.persistance.event_store.EventEntity;
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


public class AgregateShallExistPolicyTest extends TestCase {

    private FakeEventStore eventStore = new FakeEventStore();
    private AgregateShallExistPolicy agregateShallExistPolicy = new AgregateShallExistPolicy(messageSource, eventStore);


    void loadPredefinedData(String username) {
        AccountRegisteredEvent account = new AccountRegisteredEvent(
                username,
                new Email( "bertran@bagouze.net"),
                new Password("my_password")
        );
        eventStore.save(EventEntityFactory.AnonymousEventEntity(account));
        Assertions.assertEquals(1, eventStore.findAll().size());
    }

    @Test
    void test_userAccountShallExistPolicy_fails_when_no_account_is_present() {
        ResultMap<Void> userAccountCheck = agregateShallExistPolicy.check("Bagouze");
        Assertions.assertTrue(userAccountCheck.isFailure());
    }

    @Test
    void test_userAccountShallExistPolicy_succeed_when_account_is_present() {
        loadPredefinedData("Bagouze");
        ResultMap<Void> userAccountCheck = agregateShallExistPolicy.check("Bagouze");
        Assertions.assertTrue(userAccountCheck.isSuccess());
    }

    @Test
    void test_userAccountShallExistPolicy_fails_when_account_is_not_present_but_other_has_its_id() {
        loadPredefinedData("Bagouze");
        ResultMap<Void> userAccountCheck = agregateShallExistPolicy.check("Castor");
        Assertions.assertTrue(userAccountCheck.isFailure());
    }

}
