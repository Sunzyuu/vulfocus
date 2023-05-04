package com.sunzy.vulfocus.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class test {
    public static void main(String[] args) {
            // 获得当前时间
            LocalDateTime localDateTime = LocalDateTime.now();
            // 将当前时间转为时间戳
            long second = localDateTime.toInstant(ZoneOffset.ofHours(8)).getEpochSecond();
            // 1580707001
            System.out.println(second);

            long a = 1000;
            double b = 1000.01;
        System.out.println(a < b);
    }
}
