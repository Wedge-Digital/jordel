package com.shared.services;

import org.springframework.stereotype.Service;

import java.util.Date;

public interface AbstractDateService {
    public Result<Date> dateTimeFromMysql(String mysqlDate);

    public Date now();
}
