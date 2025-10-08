package com.shared.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

class DateServiceTests {

    @Test
    void TestSuccessParse() {
        String tobeParsed = "2023-10-01 11:12:00";
        DateService ds = new DateService();
        Result<Date> result = ds.dateTimeFromMysql(tobeParsed);
        Assertions.assertTrue(result.isSuccess());
        Date expectedDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            expectedDate = sdf.parse(tobeParsed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(result.getValue(), expectedDate);
    }

    @Test
    void TestFailureParse() {
        String tobeParsed = "2023-10-01:11:12:00";
        DateService ds = new DateService();
        Result<Date> result = ds.dateTimeFromMysql(tobeParsed);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Invalid date format", result.getErrorMessage());
    }
}
