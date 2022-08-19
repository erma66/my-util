package com.erma.util.str;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Date 2022/6/21 14:19
 * @Created by erma66
 */
public class IdGenerator {
    private static AtomicInteger order = new AtomicInteger(0);

    /**
     * 生成18位id,低并发可用，高并发会重复
     *
     * @return
     */
    public static String getId19() {
        if (order.get() >= 100) {
            order = new AtomicInteger(0);
        }
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + order.incrementAndGet() % 100;
    }
}
