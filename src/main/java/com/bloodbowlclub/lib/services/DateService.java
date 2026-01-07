package com.bloodbowlclub.lib.services;

import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
            return Result.failure(new TranslatableMessage("date.invalid_format", mysqlDate), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        return Result.success(computed);
    }

    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
