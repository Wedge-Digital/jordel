package com.auth.use_cases.register.fake_policies;

import com.lib.use_cases.Policy;
import com.lib.services.ResultMap;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class FailurePolicy extends Policy {

    public FailurePolicy(MessageSource msgSource) {
        super(msgSource);
    }

    @Override
    public String getErrorMsg(String username) {
        return "Failure policy";
    }

    public ResultMap<Void> check(String username) {
        return ResultMap.failure("fake policy", "failure policy");
    }
}
