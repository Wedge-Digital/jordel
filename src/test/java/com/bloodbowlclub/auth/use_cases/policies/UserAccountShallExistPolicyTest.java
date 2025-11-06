package com.bloodbowlclub.auth.use_cases.policies;

import com.bloodbowlclub.WebApplication;
import com.bloodbowlclub.auth.domain.user_account.ActiveUserAccount;
import com.bloodbowlclub.auth.domain.user_account.DraftUserAccount;
import com.bloodbowlclub.auth.domain.user_account.values.Email;
import com.bloodbowlclub.auth.domain.user_account.values.Password;
import com.bloodbowlclub.auth.domain.user_account.values.UserAccountID;
import com.bloodbowlclub.auth.domain.user_account.values.Username;
import com.bloodbowlclub.lib.persistance.read_cache.ReadEntity;
import com.bloodbowlclub.lib.persistance.read_cache.ReadEntityType;
import com.bloodbowlclub.lib.persistance.read_cache.ReadRepository;
import com.bloodbowlclub.lib.services.ResultMap;
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
        DraftUserAccount draftAccount = DraftUserAccount.builder()
                .userId(new UserAccountID(accountId))
                .username(new Username("my_username"))
                .email(new Email( "bertran@bagouze.net"))
                .password(new Password("my_password"))
                .createdAt(new Date())
                .build();

        ActiveUserAccount account = ActiveUserAccount.builder()
                .userId(new UserAccountID(accountId))
                .username(new Username("my_username"))
                .email(new Email( "bertran@bagouze.net"))
                .password(new Password("my_password"))
                .createdAt(new Date())
                .validatedAt(new Date())
                .build();

        ReadEntity readEntity = new ReadEntity(account);
        readRepository.save(readEntity);
        Assertions.assertEquals(1, readRepository.findAll().size());
    }

    void loadExternalData(String accountId) {
        DraftUserAccount draftAccount = DraftUserAccount.builder()
                .userId(new UserAccountID(accountId))
                .username(new Username("my_username"))
                .email(new Email( "bertran@bagouze.net"))
                .password(new Password("my_password"))
                .createdAt(new Date())
                .build();

        ActiveUserAccount account = ActiveUserAccount.builder()
                .userId(new UserAccountID(accountId))
                .username(new Username("my_username"))
                .email(new Email( "bertran@bagouze.net"))
                .password(new Password("my_password"))
                .createdAt(new Date())
                .validatedAt(new Date())
                .build();
        ReadEntity readEntity = new ReadEntity(account);
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
