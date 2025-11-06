package com.bloodbowlclub.auth.use_cases;

import com.bloodbowlclub.auth.domain.user_account.commands.LoginUserCommand;
import com.bloodbowlclub.lib.Command;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.Result;
import com.bloodbowlclub.lib.services.ResultMap;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import com.bloodbowlclub.lib.use_cases.UserCommand;
import com.bloodbowlclub.shared.use_cases.CommandResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;


@Component
public class LoginCommandHandler extends CommandHandler {

    protected LoginCommandHandler(@Qualifier("EventStore") EventStore eventStore,
                                  AbstractEventDispatcher businessDispatcher,
                                  MessageSource messageSource) {
        super(eventStore, businessDispatcher, messageSource);
    }

    @Override
    public ResultMap<Void> handle(Command userCommand) {
        return null;
    }
}
