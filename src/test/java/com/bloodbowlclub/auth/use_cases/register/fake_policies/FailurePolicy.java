package com.bloodbowlclub.auth.use_cases.register.fake_policies;

import com.bloodbowlclub.lib.services.TranslatableMessage;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.use_cases.Policy;
import org.springframework.context.MessageSource;

public class FailurePolicy extends Policy {
    private final ErrorCode code;

    public FailurePolicy(ErrorCode code) {
        super(null);
        this.code = code;
    }

    @Override
    public TranslatableMessage getErrorMsg(String username) {
        return new TranslatableMessage("policy.failure");
    }

    public ResultMap<Void> check(String username) {
        return ResultMap.failure("aggregateId", new TranslatableMessage("fake.policy"), code);
    }
}
