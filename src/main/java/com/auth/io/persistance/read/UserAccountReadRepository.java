package com.auth.io.persistance.read;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountReadRepository extends JpaRepository<UserJpaEntity, String> {
}