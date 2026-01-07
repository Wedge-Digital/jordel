package com.bloodbowlclub.auth.use_cases;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.domain.user_account.commands.LoginCommand;
import com.bloodbowlclub.lib.Command;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.services.TranslatableMessage;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component("loginCommandHandler")
public class LoginCommandHandler extends CommandHandler {

    public LoginCommandHandler(@Qualifier("eventStore") EventStore eventStore,
                               AbstractEventDispatcher businessDispatcher) {
        super(eventStore, businessDispatcher);
    }

    private TranslatableMessage getAccountNotExistMessage(String username){
        return new TranslatableMessage("user_account.not_existing", username);
    }

    private TranslatableMessage getBadPasswordMessage(String username){
        return new TranslatableMessage("user_account.bad_credentials", username);
    }


    @Override
    public ResultMap<Void> handle(Command userCommand) {
        LoginCommand cmd = (LoginCommand) userCommand;
        String username = cmd.getUsername();

        Result<AggregateRoot> agregateSearch = eventStore.findUser(username);
        if (agregateSearch.isFailure()) {
            return ResultMap.failure("username", getAccountNotExistMessage(username), ErrorCode.NOT_FOUND);
        }

        BaseUserAccount userAccount = (BaseUserAccount) agregateSearch.getValue();
        if (userAccount.isNotValid()) {
            return ResultMap.failure("username", getAccountNotExistMessage(username), ErrorCode.UNPROCESSABLE_ENTITY);
        }

        Result<Void> loginResult = userAccount.login(cmd.getPassword());
        if (loginResult.isFailure()) {
            return ResultMap.failure("password", getBadPasswordMessage(username), ErrorCode.BAD_REQUEST);
        }
        saveAndDispatch(userAccount.domainEvents());
        return ResultMap.success(null);
    }
}
