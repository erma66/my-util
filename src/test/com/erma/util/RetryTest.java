package com.erma.util;

import com.erma.util.retry.RetryExecutor;
import com.erma.util.retry.TimeWheelRetryExecutor;

import java.util.concurrent.TimeUnit;

/**
 * @Date 2023/2/1 15:54
 * @Created by yzfeng
 */
public class RetryTest {

    public static void main(String[] args) {
        RetryExecutor retryExecutor = new TimeWheelRetryExecutor();
        retryExecutor.submit("123", current -> System.out.println("current:" + current), 6, 2, 5, TimeUnit.SECONDS, msg -> System.out.printf("end task:" + msg));
    }
}
