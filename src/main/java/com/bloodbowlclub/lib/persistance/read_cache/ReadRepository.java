package com.bloodbowlclub.lib.persistance.read_cache;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReadRepository extends JpaRepository<ReadEntity, String> {

    @Query(value = "SELECT * FROM read_cache " +
            "WHERE type = :type " +
            "AND data::jsonb ->> 'email' = :email", nativeQuery = true)
    Optional<ReadEntity> findUserAccountByEmail(@Param("type") String type, @Param("email") String email);

    @Query(value = "SELECT * FROM read_cache " +
            "WHERE type = :type " +
            "AND data::jsonb ->> 'username' = :username", nativeQuery = true)
    Optional<ReadEntity> findUserAccountByUsername(@Param("type") String type, @Param("username") String username);

    @Query(value = "SELECT * FROM read_cache " +
            "WHERE type = :type " +
            "AND data::jsonb ->> 'id' = :userId", nativeQuery = true)
    Optional<ReadEntity> findUserAccountByUserId(@Param("type") String type, @Param("userId") String userId);

}