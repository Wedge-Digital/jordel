package com.auth.use_cases.policies;
import com.lib.persistance.read_cache.ReadEntity;
import com.lib.persistance.read_cache.ReadRepository;
import com.lib.services.ResultMap;
import com.lib.use_cases.Policy;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service("UsernameShallNotExistPolicy")
public class UsernameShallNotExistPolicy extends Policy {

    private final ReadRepository readRepository;

    public UsernameShallNotExistPolicy(MessageSource msgSource, ReadRepository readRepository) {
        super(msgSource);
        this.readRepository = readRepository;
    }

    public String getErrorMsg(String username) {
        return msgSource.getMessage("user_registration.username.already_exists", new Object[]{username}, Locale.getDefault());
    }

    public ResultMap<Void> check(String username) {
        Optional<ReadEntity> result = this.readRepository.findUserAccountByUsername("USER_ACCOUNT", username);
        if (result.isPresent()) {
            return ResultMap.failure("username", getErrorMsg(username));
        }
        return ResultMap.success(null);
    }
}
