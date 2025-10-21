package com.bloodbowlclub.lib.services;

import java.util.Date;

public interface AbstractDateService {
    public Result<Date> dateTimeFromMysql(String mysqlDate);

    public Date now();
}
