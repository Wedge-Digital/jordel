package com.auth.use_cases.register.fake_policies;

import com.auth.use_cases.policies.EmailShallNotExistPolicy;
import com.shared.services.Result;
import com.shared.services.ResultMap;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class EmailAlwaysExistsPolicy extends EmailShallNotExistPolicy {

    public EmailAlwaysExistsPolicy(MessageSource msgSource) {
        super(msgSource);
    }

    public ResultMap<String> check(String email)  {
        return ResultMap.failure("email", getErrorMsg(email));
    }
}
