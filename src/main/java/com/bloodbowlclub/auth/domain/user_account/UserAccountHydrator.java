package com.bloodbowlclub.auth.domain.user_account;

import com.bloodbowlclub.lib.persistance.event_store.EventEntity;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.Result;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAccountHydrator {

    private final EventStore eventRepo;

    public UserAccountHydrator(EventStore eventRepo) {
        this.eventRepo = eventRepo;
    }

    public Result<AbstractUserAccount> hydrate(String accountId) {
        List<EventEntity> eventEntityList = eventRepo.findBySubject(accountId);
        DraftUserAccount account = new DraftUserAccount();
        Result<AbstractUserAccount> agregateHydratation = account.applyAll(eventEntityList.stream().map(EventEntity::getData).toList());

        if (agregateHydratation.isFailure()) {
            return Result.failure(agregateHydratation.getError());
        }
        return agregateHydratation;
    }
}
