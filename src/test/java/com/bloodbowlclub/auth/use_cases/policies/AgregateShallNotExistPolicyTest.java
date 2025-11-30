package com.bloodbowlclub.auth.use_cases.policies;


import com.bloodbowlclub.auth.domain.user_account.events.AccountRegisteredEvent;
import com.bloodbowlclub.auth.domain.user_account.values.Email;
import com.bloodbowlclub.auth.domain.user_account.values.Password;
import com.bloodbowlclub.auth.io.web.UserTestUtils;
import com.bloodbowlclub.lib.persistance.event_store.EventEntityFactory;
import com.bloodbowlclub.lib.persistance.event_store.fake.FakeEventStore;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.tests.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AgregateShallNotExistPolicyTest extends TestCase {

    private final FakeEventStore fakeEventStore =  new FakeEventStore();
    private final AgregateShallNotExistPolicy agregateShallNotExistPolicy = new AgregateShallNotExistPolicy(messageSource, fakeEventStore);

    @Test
    void TestuserIdShallNotExistPolicy_fails_when_id_already_exists() {
        UserTestUtils.createUser("Bagzz", fakeEventStore);
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
        UserTestUtils.createUser("Bagzz", fakeEventStore);
        ResultMap<?> emailCheck = agregateShallNotExistPolicy.check("Castor");
        Assertions.assertTrue(emailCheck.isSuccess());
    }

}
