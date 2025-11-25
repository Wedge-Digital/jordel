package com.bloodbowlclub.lib.tests;

import com.bloodbowlclub.lib.Command;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.use_cases.CommandHandler;

public class SucceedCommandHandler extends CommandHandler {
    public SucceedCommandHandler() {
        super(null, null, null);
    }

    @Override
    public ResultMap<Void> handle(Command userCommand) {
        return ResultMap.success(null);
    }
}
