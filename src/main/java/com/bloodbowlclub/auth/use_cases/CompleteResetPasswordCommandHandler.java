package com.bloodbowlclub.auth.use_cases;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.domain.user_account.commands.CompleteResetPasswordCommand;
import com.bloodbowlclub.auth.domain.user_account.values.Password;
import com.bloodbowlclub.auth.domain.user_account.values.PasswordResetToken;
import com.bloodbowlclub.lib.Command;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component("completeResetPasswordCommandHandler")
public class CompleteResetPasswordCommandHandler extends CommandHandler {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CompleteResetPasswordCommandHandler.class);

    public CompleteResetPasswordCommandHandler(@Qualifier("eventStore") EventStore eventStore,
                                               @Qualifier("eventDispatcher") AbstractEventDispatcher businessDispatcher,
                                               MessageSource messageSource
    ) {
        super(eventStore, businessDispatcher, messageSource);
    }

    @Override
    public ResultMap<Void> handle(Command command) {
        CompleteResetPasswordCommand cmd = (CompleteResetPasswordCommand) command;
        Result<AggregateRoot> userSearch = eventStore.findUser(cmd.username());

        if (userSearch.isFailure()) {
            String errorMessage = messageSource.getMessage("user_account.not_existing", new String[]{cmd.username()}, Locale.getDefault());
            return ResultMap.failure("username", errorMessage, ErrorCode.NOT_FOUND);
        }

        BaseUserAccount userAccount = (BaseUserAccount) userSearch.getValue();
        ResultMap<Void> passwordChange = userAccount.completeResetPassword(
                new PasswordResetToken(cmd.token()),
                new Password(cmd.newPassword()));

        if (passwordChange.isFailure()) {
            String errorMessage = messageSource.getMessage(
                    "complete_password_reset.impossible_to_complete",
                    new String[]{cmd.username()},
                    Locale.getDefault());
            return ResultMap.failure("UserAccount", errorMessage, ErrorCode.BAD_REQUEST);
        }

        this.saveAndDispatch(userAccount.domainEvents());

        return ResultMap.success(null);
    }
}
