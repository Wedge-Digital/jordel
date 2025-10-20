package com.auth.use_cases.register.fake_policies;

import com.lib.use_cases.Policy;
import com.lib.services.ResultMap;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class SuccessPolicy extends Policy {

    public SuccessPolicy(MessageSource msgSource) {
        super(msgSource);
    }

    @Override
    public String getErrorMsg(String username) {
        return "";
    }

    public ResultMap<Void> check(String username) {
        return ResultMap.success(null);
    }
}
