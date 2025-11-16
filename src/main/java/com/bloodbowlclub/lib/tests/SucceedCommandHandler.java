package com.bloodbowlclub.lib.tests;

import com.bloodbowlclub.lib.Command;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.ResultMap;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import org.springframework.context.MessageSource;

public class SucceedCommandHandler extends CommandHandler {
    public SucceedCommandHandler() {
        super(null, null, null);
    }

    @Override
    public ResultMap<Void> handle(Command userCommand) {
        return ResultMap.success(null);
    }
}
