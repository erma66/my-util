package com.erma.util.async;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 异步任务执行工具
 */
public class AsyncTaskUtils {

    private static final Executor EXECUTOR =
            new ThreadPoolExecutor(
                    2,
                    Math.max(Runtime.getRuntime().availableProcessors(), 10),
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>());

    /**
     * 执行一个任务，该任务没有参数，也没有返回值
     *
     * @param runnable        需要执行的任务
     * @param exceptionHandle 异常处理函数,如果没有发生异常，该函数不会调用
     */
    public static CompletableFuture<Void> execute(
            Runnable runnable, Consumer<? super Throwable> exceptionHandle) {
        return CompletableFuture.runAsync(runnable, EXECUTOR)
                .whenComplete(
                        (t, e) -> {
                            if (e != null) {
                                exceptionHandle.accept(e);
                            }
                        });
    }

    /**
     * 执行一个任务，该任务有参数，没有返回值
     *
     * @param consumer        需要执行的任务
     * @param param           参数
     * @param exceptionHandle 异常处理函数，如果没有发生异常，该函数不会调用
     * @param <T>             参数类型
     */
    public static <T> CompletableFuture<Void> execute(
            Consumer<T> consumer, T param, Consumer<? super Throwable> exceptionHandle) {
        return CompletableFuture.runAsync(() -> consumer.accept(param), EXECUTOR)
                .whenComplete(
                        (result, exception) -> {
                            if (exception != null) {
                                exceptionHandle.accept(exception);
                            }
                        });
    }

    /**
     * 执行一个任务，该任务没有参数，有返回值
     *
     * @param supplier        需要执行的任务
     * @param resultHandle    结果处理函数
     * @param exceptionHandle 异常处理函数，如果没有发生异常，该函数不会调用
     * @param <R>             返回值类型
     */
    public static <R> CompletableFuture<R> execute(
            Supplier<R> supplier,
            Consumer<R> resultHandle,
            Consumer<? super Throwable> exceptionHandle) {
        return CompletableFuture.supplyAsync(supplier, EXECUTOR)
                .whenComplete(
                        (result, exception) -> {
                            if (exception != null) {
                                exceptionHandle.accept(exception);
                            } else {
                                resultHandle.accept(result);
                            }
                        });
    }

    /**
     * 执行一个任务，该任务有参数，有或者没有返回值
     *
     * @param function        执行的任务
     * @param param           参数
     * @param resultHandle    结果处理函数
     * @param exceptionHandle 异常处理函数，如果没有发生异常，该函数不会调用
     * @param <T>             参数类型
     * @param <R>             返回值类型
     */
    public static <T, R> CompletableFuture<R> execute(
            Function<T, R> function,
            T param,
            Consumer<R> resultHandle,
            Consumer<? super Throwable> exceptionHandle) {
        return CompletableFuture.supplyAsync(() -> function.apply(param), EXECUTOR)
                .whenComplete(
                        (result, exception) -> {
                            if (exception != null) {
                                exceptionHandle.accept(exception);
                            } else {
                                resultHandle.accept(result);
                            }
                        });
    }

    public static void main(String[] args) {
        System.out.println(Thread.currentThread());
        // 无参数无返回值-无异常
        execute(
                () -> {
                    System.out.println("hello");
                    System.out.println(Thread.currentThread());
                },
                exception -> System.out.println(exception.getMessage()));
        // 无参数无返回值-有异常
        execute(
                () -> {
                    System.out.println(Thread.currentThread());
                    throw new RuntimeException("exception");
                },
                exception -> System.out.println(exception.getMessage()));

        // 有参数无返回值-无异常
        execute(
                param -> {
                    System.out.println(param);
                    System.out.println(Thread.currentThread());
                },
                "hello",
                exception -> System.out.println(exception.getMessage()));
        // 有参数无返回值-有异常
        execute(
                param -> {
                    System.out.println(Thread.currentThread());
                    throw new RuntimeException("exception");
                },
                "hello",
                exception -> System.out.println(exception.getMessage()));

        // 无参数，有返回值-无异常
        execute(
                () -> {
                    System.out.println("hello");
                    System.out.println(Thread.currentThread());
                    return "world";
                },
                result -> {
                    System.out.println(result);
                },
                exception -> System.out.println(exception.getMessage()));
        // 无参数，有返回值-有异常
        execute(
                () -> {
                    System.out.println(Thread.currentThread());
                    throw new RuntimeException("exception");
                },
                result -> {
                    System.out.println(result);
                },
                exception -> System.out.println(exception.getMessage()));

        // 有参数，有返回值-无异常
        execute(
                a -> {
                    System.out.println(a);
                    System.out.println(Thread.currentThread());
                    return "world";
                },
                "hello",
                result -> {
                    System.out.println(result);
                },
                exception -> System.out.println(exception.getMessage()));
        // 有参数，有返回值-有异常
        execute(
                a -> {
                    System.out.println(Thread.currentThread());
                    throw new RuntimeException("exception");
                },
                "hello",
                result -> {
                    System.out.println(result);
                },
                exception -> System.out.println(exception.getMessage()));
    }
}
