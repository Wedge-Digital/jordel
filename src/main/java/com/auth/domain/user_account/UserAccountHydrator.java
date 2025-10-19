package com.auth.domain.user_account;

import com.lib.persistance.event_log.EventLogEntity;
import com.lib.persistance.event_log.EventLogRepository;
import com.lib.services.Result;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAccountHydrator {

    private final EventLogRepository eventRepo;

    public UserAccountHydrator(EventLogRepository eventRepo) {
        this.eventRepo = eventRepo;
    }

    public Result<AbstractUserAccount> hydrate(String accountId) {
        List<EventLogEntity> eventEntityList = eventRepo.findBySubject(accountId);
        DraftUserAccount account = new DraftUserAccount();
        Result<AbstractUserAccount> agregateHydratation = account.applyAll(eventEntityList.stream().map(EventLogEntity::getData).toList());

        if (agregateHydratation.isFailure()) {
            return Result.failure(agregateHydratation.getError());
        }
        return agregateHydratation;
    }
}
