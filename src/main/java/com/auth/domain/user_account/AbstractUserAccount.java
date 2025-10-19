package com.auth.domain.user_account;

import com.auth.domain.user_account.commands.ValidateEmailCommand;
import com.auth.domain.user_account.events.AccountRegisteredEvent;
import com.auth.domain.user_account.events.EmailValidatedEvent;
import com.lib.domain.AggregateRoot;
import com.lib.domain.events.DomainEvent;
import com.lib.services.Result;

import java.util.Comparator;
import java.util.List;

public abstract class AbstractUserAccount extends AggregateRoot {

    public AbstractUserAccount() {}

    public Result<AbstractUserAccount> applyAll(List<DomainEvent> eventList) {
        eventList.sort(Comparator.comparing(DomainEvent::getTimeStampedAt));
        AbstractUserAccount current = this;
        for (DomainEvent event : eventList) {
            Result<AbstractUserAccount> currentApplication = current.apply(event);
            if (currentApplication.isFailure()) {
                return currentApplication;
            }
            current = currentApplication.getValue();
        }
        return Result.success(current);
    }

    public Result<AbstractUserAccount> apply(DomainEvent event) {
        return switch (event) {
            case AccountRegisteredEvent e1 -> applyAccountRegisteredEvent(e1);
            case EmailValidatedEvent e2 -> applyEmailValidatedEvent(e2);
            default -> Result.failure("Impossible to apply event " + event.getClass().getSimpleName() + " to aggregate AbstractUserAccount");
        };
    }

    private Result<AbstractUserAccount> applyAccountRegisteredEvent(AccountRegisteredEvent event) {
        return Result.success(new DraftUserAccount(event.getAggregateId(),
                event.getUsername(),
                event.getEmail(),
                event.getPassword(),
                event.getCreatedAt()));
    }

    private Result<AbstractUserAccount> applyEmailValidatedEvent(EmailValidatedEvent event) {
        return Result.success(new ActiveUserAccount((DraftUserAccount) this, event.getValidatedAt()));
    }

    public abstract void confirmEmail(ValidateEmailCommand command);

    public abstract boolean isActivated();

}
