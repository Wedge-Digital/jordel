package com.auth.services;

import com.auth.services.errors.FormatValidationError;

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
            return Result.failure(new FormatValidationError(e.getMessage()));
        }
        return Result.success(computed);
    }
}
