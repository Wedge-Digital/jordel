package com.bloodbowlclub.auth.use_cases.register.fake_policies;

import com.bloodbowlclub.lib.services.ResultMap;
import com.bloodbowlclub.lib.use_cases.Policy;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class FailurePolicy extends Policy {

    public FailurePolicy(MessageSource msgSource) {
        super(msgSource, null);
    }

    @Override
    public String getErrorMsg(String username) {
        return "Failure policy";
    }

    public ResultMap<Void> check(String username) {
        return ResultMap.failure("fake policy", "failure policy");
    }
}
