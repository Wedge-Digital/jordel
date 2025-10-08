package com.auth.use_cases.register.fake_policies;

import com.auth.use_cases.policies.UserShallNotExistPolicy;
import com.shared.services.Result;
import com.shared.services.ResultMap;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class UserAlwaysNotExistsPolicy extends UserShallNotExistPolicy {

    public UserAlwaysNotExistsPolicy(MessageSource msgSource) {
        super(msgSource);
    }

    public ResultMap<String> check(String username) {
        return ResultMap.success("Ok, user is not known");
    }
}
