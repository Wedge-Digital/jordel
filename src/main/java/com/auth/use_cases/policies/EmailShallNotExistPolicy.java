package com.auth.use_cases.policies;

import com.shared.services.MessageSourceConfig;
import com.shared.services.Result;
import com.shared.services.ResultMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class EmailShallNotExistPolicy implements Policy {

    private final MessageSource msgSource;

    public EmailShallNotExistPolicy(MessageSource msgSource) {
        this.msgSource = msgSource;
    }

    public String getErrorMsg(String email) {
        return msgSource.getMessage("user_registration.email.already_exists", new Object[]{email}, Locale.getDefault());
    }

    public ResultMap<String> check(String email) {
        return ResultMap.failure("email", getErrorMsg(email));
    }
}
