package com.bloodbowlclub.lib.use_cases;

import com.bloodbowlclub.lib.services.ResultMap;

public abstract class CommandHandler {
    public abstract ResultMap<Void> handle(Command command);

}
