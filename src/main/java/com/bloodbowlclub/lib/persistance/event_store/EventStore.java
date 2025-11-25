package com.bloodbowlclub.lib.persistance.event_store;

import com.bloodbowlclub.auth.domain.user_account.DraftUserAccount;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.result.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("EventStore")
public interface EventStore extends JpaRepository<EventEntity, String> {

    List<EventEntity> findBySubject(String agregateId);

    List<EventEntity> findBySource(String userId);

    List<EventEntity> findBySourceAndSubject(String source, String subject);

    default Result<AggregateRoot> findUser(String username) {
        DraftUserAccount userAccount = new DraftUserAccount(username);
        List<EventEntity> eventList = findBySubject(username);
        List<DomainEvent> domainEvents = eventList.stream().map(EventEntity::getData).toList();
        return userAccount.hydrate(domainEvents);
    }

}