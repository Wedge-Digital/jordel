package com.auth.use_cases.policies;
import com.lib.services.ResultMap;
import com.lib.use_cases.Policy;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service("UserAccountShallExistPolicy")
public class UserAccountShallExistPolicy extends Policy {

    public UserAccountShallExistPolicy(MessageSource msgSource) {
        super(msgSource);
    }

    public String getErrorMsg(String userAccountId) {
        return msgSource.getMessage("user_account.not_existing", new Object[]{userAccountId}, Locale.getDefault());
    }

    public ResultMap<String> check(String userAccountId) {
        return ResultMap.failure("UserAccount", getErrorMsg(userAccountId));
    }
}
