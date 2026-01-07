package com.bloodbowlclub.auth.use_cases;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.domain.user_account.commands.StartResetPasswordCommand;
import com.bloodbowlclub.lib.Command;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("startResetPasswordCommandHandler")
public class StartResetPasswordCommandHandler extends CommandHandler {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(StartResetPasswordCommandHandler.class);

    public StartResetPasswordCommandHandler(@Qualifier("eventStore") EventStore eventStore,
                                            @Qualifier("eventDispatcher") AbstractEventDispatcher businessDispatcher
    ) {
        super(eventStore, businessDispatcher);
    }

    @Override
    public ResultMap<Void> handle(Command command) {
        StartResetPasswordCommand cmd = (StartResetPasswordCommand) command;
        Result<AggregateRoot> foundUserAccount = eventStore.findUser(cmd.getUsername());
        if (foundUserAccount.isFailure()) {
            return ResultMap.success(null);
        }

        BaseUserAccount userAccount = (BaseUserAccount) foundUserAccount.getValue();

        ResultMap<Void> res =  userAccount.startResetPassword();

        if (res.isFailure()) {
            return res;
        }

        this.saveAndDispatch(userAccount.domainEvents());

        return ResultMap.success(null);
    }
}
