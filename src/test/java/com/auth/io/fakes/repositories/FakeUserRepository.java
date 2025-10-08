package com.auth.io.fakes.repositories;

import com.auth.domain.user_account.ActiveUserAccount;
import com.auth.io.persistance.read.UserJpaEntity;

import java.util.Optional;

public class FakeUserRepository {

    public Optional<UserJpaEntity> findByUsername(String username) {
        return Optional.empty();
    }

    public Optional<ActiveUserAccount> findDomainUserByName(String username) {
        return Optional.empty();
    }
}
