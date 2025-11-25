package com.bloodbowlclub.auth.io.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface LostLoginTokenRepository extends JpaRepository<LostLoginTokenEntity, String> {
    Optional<LostLoginTokenEntity> findByUsername(String username);
}
