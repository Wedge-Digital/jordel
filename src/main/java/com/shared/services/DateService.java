package com.shared.services;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class DateService implements AbstractDateService {

    public DateService() {}

    public Result<Date> dateTimeFromMysql(String mysqlDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date computed = null;
        try {
            computed = sdf.parse(mysqlDate);
        } catch (Exception e) {
            return Result.failure("Invalid date format");
        }
        return Result.success(computed);
    }

    public Date now() {
        return new Date();
    }
}
