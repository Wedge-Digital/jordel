package com.auth.use_cases;

import com.auth.domain.user_account.DraftUserAccount;
import com.auth.domain.user_account.commands.RegisterCommand;
import com.lib.use_cases.Policy;
import com.lib.domain.events.AbstractEventDispatcher;
import com.lib.services.ResultMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RegisterCommandHandler {

    @Qualifier("UserNameShallNotExistPolicy")
    private final Policy userNameShallNotExistPolicy;

    @Qualifier("IdShallNotExistPolicy")
    private final Policy idShallNotExistPolicy;

    @Qualifier("EmailShallNotExistPolicy")
    private final Policy emailShallNotExistPolicy;

    private final AbstractEventDispatcher businessDispatcher;

    public RegisterCommandHandler(@Qualifier("UserNameShallNotExistPolicy") Policy userNameShallNotExistPolicy,
                                  @Qualifier("IdShallNotExistPolicy" ) Policy idShallNotExistPolicy,
                                  @Qualifier("EmailShallNotExistPolicy") Policy emailShallNotExistPolicy,
                                  AbstractEventDispatcher businessDispatcher) {
        this.userNameShallNotExistPolicy = userNameShallNotExistPolicy;
        this.idShallNotExistPolicy = idShallNotExistPolicy;
        this.emailShallNotExistPolicy = emailShallNotExistPolicy;
        this.businessDispatcher = businessDispatcher;
    }

    public ResultMap<String> handle(RegisterCommand command) {
        ResultMap<String> userIdCheck = this.idShallNotExistPolicy.check(command.username());
        ResultMap<String> usernameCheck = this.userNameShallNotExistPolicy.check(command.username());
        ResultMap<String> emailCheck = this.emailShallNotExistPolicy.check(command.email());
        ResultMap<String> allChecks = ResultMap.combine(userIdCheck, usernameCheck, emailCheck);

        if (allChecks.isFailure()) {
            return allChecks;
        }

        DraftUserAccount newAccount = new DraftUserAccount();
        ResultMap<String> registerResult = newAccount.register(command);

        if (registerResult.isFailure()) {
            return registerResult;
        }

        businessDispatcher.asyncDispatchList(newAccount.domainEvents());

        return registerResult;
    }
}
