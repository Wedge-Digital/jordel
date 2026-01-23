package com.bloodbowlclub.lib.persistance.event_store;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.team_building.domain.team.BaseTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("eventStore")
public interface EventStore extends JpaRepository<EventEntity, String> {

    List<EventEntity> findBySubject(String agregateId);

    List<EventEntity> findBySource(String userId);

    List<EventEntity> findBySourceAndSubject(String source, String subject);

    default Result<AggregateRoot> findUser(String username) {
        BaseUserAccount userAccount = new BaseUserAccount(username);
        List<EventEntity> eventList = findBySubject(username);
        List<DomainEvent> domainEvents = eventList.stream().map(EventEntity::getData).toList();
        if (domainEvents.isEmpty()) {
            return Result.failure("not found", ErrorCode.NOT_FOUND);
        }
        return userAccount.hydrate(domainEvents);
    }

    default Result<AggregateRoot> findTeam(String teamId) {
        BaseTeam team = new BaseTeam();
        List<EventEntity> eventList = findBySubject(teamId);
        List<DomainEvent> domainEvents = eventList.stream().map(EventEntity::getData).toList();
        if (domainEvents.isEmpty()) {
            return Result.failure("team not found", ErrorCode.NOT_FOUND);
        }
        return team.hydrate(domainEvents);
    }

}