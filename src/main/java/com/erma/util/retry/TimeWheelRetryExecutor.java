package com.erma.util.retry;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @Date 2022/7/22 16:56
 * @Created by erma66
 */
@Slf4j
public class TimeWheelRetryExecutor implements RetryExecutor {
    private final ConcurrentMap<String, Timeout> taskMap = PlatformDependent.newConcurrentHashMap();
    private final HashedWheelTimer wheelTimer;

    public TimeWheelRetryExecutor() {
        wheelTimer = new HashedWheelTimer();
    }

    public TimeWheelRetryExecutor(ThreadFactory threadFactory) {
        wheelTimer = new HashedWheelTimer(threadFactory);
    }

    @Override
    public void submit(String taskId, Consumer<Integer> consumer, int count, long delay, long inteval, TimeUnit unit, Consumer<String> onEnd) {
        AtomicInteger current = new AtomicInteger(1);
        Consumer<Integer> onError = current1 -> log.error("retry error,taskId:{},current:{}", taskId, current1);
        Runnable onFinish = new Runnable() {
            @Override
            public void run() {
                if (current.incrementAndGet() <= count) {
                    Timeout timeout = getTimeout(consumer, inteval, unit, current.get(), this, onError);
                    taskMap.put(taskId, timeout);
                } else {
                    taskMap.remove(taskId);
                    onEnd.accept(taskId);
                }
            }
        };
        Timeout timeout = getTimeout(consumer, delay, unit, current.get(), onFinish, onError);
        taskMap.put(taskId, timeout);
    }

    private Timeout getTimeout(Consumer<Integer> taskRun, long delay, TimeUnit unit, final int current, Runnable onFinish, Consumer<Integer> onError) {
        return wheelTimer.newTimeout(timeout -> {
            try {
                taskRun.accept(current);
            } catch (Exception ex) {
                onError.accept(current);
            } finally {
                onFinish.run();
            }
        }, delay, unit);
    }

    @Override
    public boolean cancle(String taskId) {
        log.info("retry task,taskId:{}", taskId);
        if (taskMap.containsKey(taskId)) {
            return taskMap.get(taskId).cancel();
        }
        return false;
    }
}
