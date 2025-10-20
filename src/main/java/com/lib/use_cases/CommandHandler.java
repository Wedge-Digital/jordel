package com.lib.use_cases;

import com.lib.services.ResultMap;

public abstract class CommandHandler {
    public abstract ResultMap<Void> handle(Command command);

}
