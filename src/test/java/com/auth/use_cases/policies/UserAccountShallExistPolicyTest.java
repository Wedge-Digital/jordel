package com.auth.use_cases.policies;

import com.WebApplication;
import com.auth.domain.user_account.ActiveUserAccount;
import com.auth.domain.user_account.DraftUserAccount;
import com.lib.persistance.read_cache.ReadEntity;
import com.lib.persistance.read_cache.ReadEntityType;
import com.lib.persistance.read_cache.ReadRepository;
import com.lib.services.ObjectMapperService;
import com.lib.services.ResultMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = WebApplication.class)
@Import(UserAccountShallExistPolicy.class)
public class UserAccountShallExistPolicyTest {

    @Autowired
    private ReadRepository readRepository;

    @Autowired
    private UserAccountShallExistPolicy userAccountShallExistPolicy;


    void loadPredefinedData(String accountId) {
        DraftUserAccount draftAccount = new DraftUserAccount(
                accountId,
                "my_username",
                "steve@farrel.com",
                "my_password",
                new Date());
        ActiveUserAccount account = new ActiveUserAccount(draftAccount, new Date());
        ReadEntity readEntity = new ReadEntity(ReadEntityType.USER_ACCOUNT, account);
        readRepository.save(readEntity);
        Assertions.assertEquals(1, readRepository.findAll().size());
    }

    void loadExternalData(String accountId) {
        DraftUserAccount draftAccount = new DraftUserAccount(
                accountId,
                "my_username",
                "steve@farrel.com",
                "my_password",
                new Date());
        ActiveUserAccount account = new ActiveUserAccount(draftAccount, new Date());
        ReadEntity readEntity = new ReadEntity(ReadEntityType.TEAM, account);
        readRepository.save(readEntity);
        Assertions.assertEquals(1, readRepository.findAll().size());
    }

    @Test
    void test_userAccountShallExistPolicy_fails_when_no_account_is_present() {
        String userAccountId = "01K7YW65AECNXFPTZKFB3281FJ";
        ResultMap<Void> userAccountCheck = userAccountShallExistPolicy.check(userAccountId);
        Assertions.assertTrue(userAccountCheck.isFailure());
    }

    @Test
    void test_userAccountShallExistPolicy_succeed_when_account_is_present() {
        String userAccountId = "01K7YW65AECNXFPTZKFB3281FJ";
        loadPredefinedData(userAccountId);
        ResultMap<Void> userAccountCheck = userAccountShallExistPolicy.check(userAccountId);
        Assertions.assertTrue(userAccountCheck.isSuccess());
    }

    @Test
    void test_userAccountShallExistPolicy_fails_when_account_is_not_present_but_other_has_its_id() {
        String userAccountId = "01K7YW65AECNXFPTZKFB3281FJ";
        loadExternalData(userAccountId);
        ResultMap<Void> userAccountCheck = userAccountShallExistPolicy.check(userAccountId);
        Assertions.assertTrue(userAccountCheck.isFailure());
    }

    @Test
    void test_userAccountShallExistPolicy_fails_when_account_is_not_present_but_other_account_is_present() {
        String userAccountId = "01K7YW65AECNXFPTZKFB3281FA";
        loadExternalData(userAccountId);
        ResultMap<Void> userAccountCheck = userAccountShallExistPolicy.check("01K7YW65AECNXFPTZKFB3281FJ");
        Assertions.assertTrue(userAccountCheck.isFailure());
    }
}
