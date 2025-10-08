package com.auth.use_cases.policies;
import com.shared.services.ResultMap;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UserShallNotExistPolicy implements Policy {
    private final MessageSource msgSource;

    public UserShallNotExistPolicy(MessageSource msgSource) {
        this.msgSource = msgSource;
    }

    public String getErrorMsg(String username) {
        return msgSource.getMessage("user_registration.username.already_exists", new Object[]{username}, Locale.getDefault());
    }

    public ResultMap<String> check(String username) {
        return ResultMap.failure("username", getErrorMsg(username));
    }
}
