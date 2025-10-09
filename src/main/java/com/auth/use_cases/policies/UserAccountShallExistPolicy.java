package com.auth.use_cases.policies;
import com.shared.services.ResultMap;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UserAccountShallExistPolicy implements Policy {
    private final MessageSource msgSource;

    public UserAccountShallExistPolicy(MessageSource msgSource) {
        this.msgSource = msgSource;
    }

    public String getErrorMsg(String userAccountId) {
        return msgSource.getMessage("user_account.not_existing", new Object[]{userAccountId}, Locale.getDefault());
    }

    public ResultMap<String> check(String userAccountId) {
        return ResultMap.failure("UserAccount", getErrorMsg(userAccountId));
    }
}
