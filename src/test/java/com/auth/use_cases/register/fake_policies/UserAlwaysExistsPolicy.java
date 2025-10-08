package com.auth.use_cases.register.fake_policies;

import com.auth.use_cases.policies.UserShallNotExistPolicy;
import com.shared.services.Result;
import com.shared.services.ResultMap;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class UserAlwaysExistsPolicy extends UserShallNotExistPolicy {

    public UserAlwaysExistsPolicy(MessageSource msgSource) {
        super(msgSource);
    }

    public ResultMap<String> check(String username) {
        return ResultMap.failure("username", getErrorMsg(username));
    }
}
