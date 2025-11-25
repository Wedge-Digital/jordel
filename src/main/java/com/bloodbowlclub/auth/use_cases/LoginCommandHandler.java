package com.bloodbowlclub.auth.use_cases;

import com.bloodbowlclub.auth.domain.user_account.DraftUserAccount;
import com.bloodbowlclub.auth.domain.user_account.commands.LoginCommand;
import com.bloodbowlclub.lib.Command;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;


@Component("loginCommandHandler")
public class LoginCommandHandler extends CommandHandler {

    public LoginCommandHandler(@Qualifier("EventStore") EventStore eventStore,
                               AbstractEventDispatcher businessDispatcher,
                               MessageSource messageSource) {
        super(eventStore, businessDispatcher, messageSource);
    }

    private String getAccountNotExistMessage(String username){
        return  messageSource.getMessage("user_account.not_existing", new String[]{username}, LocaleContextHolder.getLocale());
    }

    private String getBadPasswordMessage(String username){
        return  messageSource.getMessage("user_account.bad_credentials", new String[]{username}, LocaleContextHolder.getLocale());
    }


    @Override
    public ResultMap<Void> handle(Command userCommand) {
        LoginCommand cmd = (LoginCommand) userCommand;
        String username = cmd.getUsername();

        Result<AggregateRoot> agregateSearch = eventStore.findUser(username);
        DraftUserAccount userAccount = (DraftUserAccount) agregateSearch.getValue();
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
