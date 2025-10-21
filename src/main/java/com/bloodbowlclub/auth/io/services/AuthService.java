package com.bloodbowlclub.auth.io.services;

import com.bloodbowlclub.auth.domain.user_account.ActiveUserAccount;
import com.bloodbowlclub.lib.services.Result;
import org.springframework.stereotype.Service;

@Service
public class AuthService extends AbstractAuthService {

//    private final UserRepository userRepository;
//    private final MessageSource messageSource;
//
//    public AuthService(UserRepository userRepo, MessageSource messageSource) {
//        this.userRepository = userRepo;
//        this.messageSource = messageSource;
//    }

    public Result<ActiveUserAccount> isUserIsKnownAndActive(String username) {
        return null;
//        Optional<ActiveUserAccount> requestResult = userRepository.findDomainUserByName(username);
//
//        if (requestResult.isEmpty()) {
//            String errorMessage = messageSource.getMessage("user_account.not_existing", new String[]{username}, Locale.getDefault());
//            return Result.failure(errorMessage);
//        }
//
//        ActiveUserAccount foundUser = requestResult.get();
//
//        if (foundUser.isActive()) {
//            String errorMessage = messageSource.getMessage("user_account.inactive", new String[]{username}, Locale.getDefault());
//            return Result.failure(errorMessage);
//        }
//
//        return Result.success(foundUser);
    }
}
