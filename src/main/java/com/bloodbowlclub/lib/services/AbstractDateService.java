package com.bloodbowlclub.lib.services;

import com.bloodbowlclub.lib.services.result.Result;

import java.util.Date;

public interface AbstractDateService {
    public Result<Date> dateTimeFromMysql(String mysqlDate);

    public Date now();
}
