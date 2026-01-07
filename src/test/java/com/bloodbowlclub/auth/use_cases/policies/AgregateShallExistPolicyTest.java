package com.bloodbowlclub.auth.use_cases.policies;

import com.bloodbowlclub.auth.io.web.UserTestUtils;
import com.bloodbowlclub.lib.persistance.event_store.fake.FakeEventStore;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.tests.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AgregateShallExistPolicyTest extends TestCase {

    private final FakeEventStore eventStore = new FakeEventStore();
    private final AgregateShallExistPolicy agregateShallExistPolicy = new AgregateShallExistPolicy(eventStore);

    @Test
    void test_userAccountShallExistPolicy_fails_when_no_account_is_present() {
        ResultMap<Void> userAccountCheck = agregateShallExistPolicy.check("Bagouze");
        Assertions.assertTrue(userAccountCheck.isFailure());
    }

    @Test
    void test_userAccountShallExistPolicy_succeed_when_account_is_present() {
        UserTestUtils.createUser("Bagouze",  eventStore);
        ResultMap<Void> userAccountCheck = agregateShallExistPolicy.check("Bagouze");
        Assertions.assertTrue(userAccountCheck.isSuccess());
    }

    @Test
    void test_userAccountShallExistPolicy_fails_when_account_is_not_present_but_other_has_its_id() {
        UserTestUtils.createUser("Bagouze",  eventStore);
        ResultMap<Void> userAccountCheck = agregateShallExistPolicy.check("Castor");
        Assertions.assertTrue(userAccountCheck.isFailure());
    }

}
