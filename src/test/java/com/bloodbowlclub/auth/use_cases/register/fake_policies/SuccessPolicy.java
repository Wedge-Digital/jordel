package com.bloodbowlclub.auth.use_cases.register.fake_policies;

import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.ResultMap;
import com.bloodbowlclub.lib.use_cases.Policy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class SuccessPolicy extends Policy {

    public SuccessPolicy(MessageSource msgSource) {
        super(msgSource, null);
    }

    @Override
    public String getErrorMsg(String username) {
        return "";
    }

    public ResultMap<Void> check(String username) {
        return ResultMap.success(null);
    }
}
