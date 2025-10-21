package com.bloodbowlclub.lib.use_cases;

import com.bloodbowlclub.lib.services.ResultMap;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;


@Service
public abstract class Policy {

    protected final MessageSource msgSource;

    public Policy(MessageSource msgSource) {
        this.msgSource = msgSource;
    }

    public abstract String getErrorMsg(String predicate);

    public abstract ResultMap<Void> check(String predicate);
}
