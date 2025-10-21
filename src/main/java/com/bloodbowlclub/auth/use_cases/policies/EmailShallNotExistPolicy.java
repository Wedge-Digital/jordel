package com.bloodbowlclub.auth.use_cases.policies;

import com.bloodbowlclub.lib.persistance.read_cache.ReadEntity;
import com.bloodbowlclub.lib.persistance.read_cache.ReadRepository;
import com.bloodbowlclub.lib.services.ResultMap;
import com.bloodbowlclub.lib.use_cases.Policy;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service("EmailShallNotExistPolicy")
public class EmailShallNotExistPolicy extends Policy {

    private final ReadRepository readRepository;

    public EmailShallNotExistPolicy(MessageSource msgSource, ReadRepository readRepository) {
        super(msgSource);
        this.readRepository = readRepository;
    }

    public String getErrorMsg(String email) {
        return this.msgSource.getMessage("user_registration.email.already_exists", new Object[]{email}, Locale.getDefault());
    }

    public ResultMap<Void> check(String email) {
        Optional<ReadEntity> result = this.readRepository.findUserAccountByEmail("USER_ACCOUNT", email);
        if (result.isPresent()) {
            return ResultMap.failure("email", getErrorMsg(email));
        }
        return ResultMap.success(null);
    }
}
