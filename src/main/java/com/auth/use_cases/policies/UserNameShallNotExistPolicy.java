package com.auth.use_cases.policies;
import com.lib.services.ResultMap;
import com.lib.use_cases.Policy;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service("UserNameShallNotExistPolicy")
public class UserNameShallNotExistPolicy extends Policy {

    public UserNameShallNotExistPolicy(MessageSource msgSource) {
        super(msgSource);
    }

    public String getErrorMsg(String username) {
        return msgSource.getMessage("user_registration.username.already_exists", new Object[]{username}, Locale.getDefault());
    }

    public ResultMap<String> check(String username) {
        return ResultMap.failure("username", getErrorMsg(username));
    }
}
