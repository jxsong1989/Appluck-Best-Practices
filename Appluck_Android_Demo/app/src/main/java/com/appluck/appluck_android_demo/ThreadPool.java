package com.appluck.appluck_android_demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {

    private final static ExecutorService THREAD_POOL;

    static {
        THREAD_POOL = new ThreadPoolExecutor(
                4,
                8,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(16),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    private ThreadPool() {
    }

    public static void execute(Runnable r) {
        THREAD_POOL.execute(r);
    }
}
