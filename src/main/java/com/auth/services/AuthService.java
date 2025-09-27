package com.auth.services;

import com.td.aion.io.repositories.user.UserEntity;
import com.td.aion.io.repositories.user.UserRepository;
import com.td.aion.io.web.auth.controller.UserEntityAcountError;
import com.td.aion.utils.Result;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService extends AbstractAuthService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepo) {
        this.userRepository = userRepo;
    }

    public Result<UserEntity> isUserIsKnownAndActive(String userId) {
        Optional<UserEntity> requestResult = userRepository.findByUsername(userId);

        if (requestResult.isEmpty()) {
            logger.info("User {} is not present", userId);
            logger.info(requestResult.toString());
            return Result.failure(UserEntityAcountError.UNEXISTING_ACCOUNT.getMessage());
        }

        UserEntity cg = requestResult.get();

        if (Boolean.FALSE.equals(cg.isActive())) {
            return Result.failure(UserEntityAcountError.INACTIVE_ACCOUNT.getMessage());
        }

        return Result.success(cg);
    }
}
