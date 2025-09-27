package com.shared.services;

import com.auth.services.Result;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateService {

    DateService() {}

    public static Result<Date> dateTimeFromMysql(String mysqlDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date computed = null;
        try {
            computed = sdf.parse(mysqlDate);
        } catch (Exception e) {
            return Result.failure("Invalid date format");
        }
        return Result.success(computed);
    }

    public static Date now() {
        return new Date();
    }
}
