package com.erma.util.retry;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @Date 2022/7/22 16:52
 * @Created by erma66
 */
public interface RetryExecutor {
    void submit(String taskId, Consumer<Integer> onRun, int count, long delay, long inteval, TimeUnit unit, Consumer<String> onEnd);

    boolean cancle(String taskId);
}
