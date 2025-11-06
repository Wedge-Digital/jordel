package com.bloodbowlclub.lib.persistance.event_store;

import com.bloodbowlclub.auth.domain.user_account.DraftUserAccount;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.persistance.read_cache.ReadEntity;
import com.bloodbowlclub.lib.services.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("EventStore")
public interface EventStore extends JpaRepository<EventEntity, String> {

    List<EventEntity> findBySubject(String agregateId);

    List<EventEntity> findBySource(String userId);

    List<EventEntity> findBySourceAndSubject(String source, String subject);

    @Query(value = "SELECT * FROM event_log " +
            "WHERE type = AccountRegisteredEvent " +
            "AND data::jsonb ->> 'email' = :email", nativeQuery = true)
    Optional<ReadEntity> findUserAccountByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM event_log " +
            "WHERE type = AccountRegisteredEvent " +
            "AND data::jsonb ->> 'username' = :username", nativeQuery = true)
    Optional<ReadEntity> findUserAccountByUsername(@Param("username") String username);

    default  Result<AggregateRoot> hydrate(String accountId) {
        List<EventEntity> eventEntityList = this.findBySubject(accountId);
        AggregateRoot account = new DraftUserAccount();

        for (EventEntity event : eventEntityList) {
            Result<AggregateRoot> currentApplication = account.apply(event.getData());
            if (currentApplication.isFailure()) {
                return currentApplication;
            }
            account = currentApplication.getValue();
        }
        return Result.success(account);
    }
}