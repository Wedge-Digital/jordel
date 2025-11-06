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
import com.bloodbowlclub.lib.services.ObjectMapperService;
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
@Import(IdShallNotExistPolicy.class)
public class IdShallNotExistPolicyTest {

    @Autowired
    private ReadRepository readRepository;

    @Autowired
    private IdShallNotExistPolicy idShallNotExistPolicy;

    void loadPredefinedData(String id) {
        DraftUserAccount draftAccount = DraftUserAccount.builder()
                .userId(new UserAccountID(id))
                .username(new Username("my_username"))
                .email(new Email( "bertran@bagouze.net"))
                .password(new Password("my_password"))
                .createdAt(new Date())
                .build();

        ActiveUserAccount account = ActiveUserAccount.builder()
                .userId(new UserAccountID(id))
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
    void TestuserIdShallNotExistPolicy_fails_when_id_already_exists() {
        String userId = "01K7YW65AECNXFPTZKFB3281FJ";
        loadPredefinedData(userId);
        ResultMap<?> emailCheck = idShallNotExistPolicy.check(userId);
        Assertions.assertTrue(emailCheck.isFailure());
    }

    @Test
    void TestuserIdShallNotExistPolicy_succeed_when_email_does_not_exists() {
        String userId = "01K7YW65AECNXFPTZKFB3281FJ";
        ResultMap<?> emailCheck = idShallNotExistPolicy.check(userId);
        Assertions.assertTrue(emailCheck.isSuccess());
    }

    @Test
    void Test_email_shall_not_exist_policy_succeed_when_other_email_is_present() {
        String userId = "01K7YW65AECNXFPTZKFB3281FK";
        loadPredefinedData(userId);
        ResultMap<?> emailCheck = idShallNotExistPolicy.check("01K7YW65AECNXFPTZKFB3281FJ");
        Assertions.assertTrue(emailCheck.isSuccess());
    }

}
