package com.auth.use_cases.policies;

import com.shared.services.ResultMap;


public interface Policy {

    String getErrorMsg(String username);

    ResultMap<String> check(String username);
}
