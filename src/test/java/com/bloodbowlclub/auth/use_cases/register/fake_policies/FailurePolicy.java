package com.bloodbowlclub.auth.use_cases.register.fake_policies;

import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.use_cases.Policy;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

public class FailurePolicy extends Policy {
    private final ErrorCode code;

    public FailurePolicy(MessageSource msgSource, ErrorCode code) {
        super(msgSource, null);
        this.code = code;
    }

    @Override
    public String getErrorMsg(String username) {
        return "Failure policy";
    }

    public ResultMap<Void> check(String username) {
        return ResultMap.failure("fake policy", "failure policy", code);
    }
}
