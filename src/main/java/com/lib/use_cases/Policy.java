package com.lib.use_cases;

import com.lib.services.ResultMap;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;


@Service
public abstract class Policy {

    protected final MessageSource msgSource;

    public Policy(MessageSource msgSource) {
        this.msgSource = msgSource;
    }

    public abstract String getErrorMsg(String predicate);

    public abstract ResultMap<String> check(String predicate);
}
