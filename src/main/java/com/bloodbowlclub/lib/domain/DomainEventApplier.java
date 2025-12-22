package com.bloodbowlclub.lib.domain;

import com.bloodbowlclub.auth.domain.user_account.events.*;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.team_building.domain.events.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.slf4j.LoggerFactory;

import static com.bloodbowlclub.lib.services.result.ErrorCode.INTERNAL_ERROR;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@NoArgsConstructor
@SuperBuilder
public class DomainEventApplier {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DomainEventApplier.class);

    /**
     * Méthodes apply typées pour chaque type d'événement
     * Implémentations par défaut qui loggent un warning si l'événement n'est pas géré
     * Les sous-classes peuvent override ces méthodes pour gérer les événements spécifiques
     */
    //===============================================================================================================
    //
    // Evénement User Acount
    //
    //===============================================================================================================
    public Result<AggregateRoot> apply(AccountRegisteredEvent event) {
        String err = "Event not handled by " + this.getClass().getSimpleName();
        return Result.failure(err, INTERNAL_ERROR);
    }

    public Result<AggregateRoot> apply(EmailValidatedEvent event) {
        String err = "Event not handled by " + this.getClass().getSimpleName();
        return Result.failure(err, INTERNAL_ERROR);
    }

    public Result<AggregateRoot> apply(UserLoggedEvent event) {
        String err = "Event not handled by " + this.getClass().getSimpleName();
        return Result.failure(err, INTERNAL_ERROR);
    }

    public Result<AggregateRoot> apply(PasswordResetStartedEvent event) {
        String err = "Event not handled by " + this.getClass().getSimpleName();
        return Result.failure(err, INTERNAL_ERROR);
    }

    public Result<AggregateRoot> apply(PasswordResetCompletedEvent event) {
        String err = "Event not handled by " + this.getClass().getSimpleName();
        return Result.failure(err, INTERNAL_ERROR);
    }

    //===============================================================================================================
    //
    // Evénements Team Building
    //
    //===============================================================================================================

    public Result<AggregateRoot> apply(DraftTeamRegisteredEvent event) {
        String err = "Event not handled by " + this.getClass().getSimpleName();
        return Result.failure(err, INTERNAL_ERROR);
    }

    public Result<AggregateRoot> apply(RosterChosenEvent event) {
        String err = "Event not handled by " + this.getClass().getSimpleName();
        return Result.failure(err, INTERNAL_ERROR);
    }

    public Result<AggregateRoot> apply(RulesetSelectedEvent event) {
        String err = "Event not handled by " + this.getClass().getSimpleName();
        return Result.failure(err, INTERNAL_ERROR);
    }

    public Result<AggregateRoot> apply(PlayerHiredEvent event) {
        String err = "Event not handled by " + this.getClass().getSimpleName();
        return Result.failure(err, INTERNAL_ERROR);
    }

    public Result<AggregateRoot> apply(PlayerRemovedEvent event) {
        String err = "Event not handled by " + this.getClass().getSimpleName();
        return Result.failure(err, INTERNAL_ERROR);
    }

}
