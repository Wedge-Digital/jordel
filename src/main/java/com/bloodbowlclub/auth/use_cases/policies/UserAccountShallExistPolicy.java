package com.bloodbowlclub.auth.use_cases.policies;
import com.bloodbowlclub.lib.persistance.read_cache.ReadEntity;
import com.bloodbowlclub.lib.persistance.read_cache.ReadRepository;
import com.bloodbowlclub.lib.services.ResultMap;
import com.bloodbowlclub.lib.use_cases.Policy;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service("UserAccountShallExistPolicy")
public class UserAccountShallExistPolicy extends Policy {

    private final ReadRepository readRepository;

    public UserAccountShallExistPolicy(MessageSource msgSource, ReadRepository readRepository) {
        super(msgSource);
        this.readRepository = readRepository;
    }

    public String getErrorMsg(String userAccountId) {
        return msgSource.getMessage("user_account.not_existing", new Object[]{userAccountId}, Locale.getDefault());
    }

    public ResultMap<Void> check(String userAccountId) {
        Optional<ReadEntity> result = this.readRepository.findUserAccountByUserId("USER_ACCOUNT", userAccountId);
        if (result.isPresent()) {
            return ResultMap.success(null);
        }
        return ResultMap.failure("UserAccount", getErrorMsg(userAccountId));
    }
}
