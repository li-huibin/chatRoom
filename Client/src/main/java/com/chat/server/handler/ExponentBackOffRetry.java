package com.chat.server.handler;

import java.util.Random;

/**
 * @Program: chatRoom
 * @Description: 重试策略
 * @Author: LHB
 * @Version: v0.0.1
 * @Time: 2021-10-13 13:08
 **/
public class ExponentBackOffRetry implements RetryPolicy{
    private static final int MAX_RETRIES_LIMIT = 29;
    private static final int DEFAULT_MAX_SLEEP_MS = Integer.MAX_VALUE;

    private Random random = new Random();
    private final long baseSleepTimeMs;
    private final int maxRetries;
    private final int maxSleepMs;

    public ExponentBackOffRetry(long baseSleepTimeMs, int maxRetries) {
        this(baseSleepTimeMs,maxRetries,DEFAULT_MAX_SLEEP_MS);
    }

    public ExponentBackOffRetry(long baseSleepTimeMs, int maxRetries, int maxSleepMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
        this.maxRetries = maxRetries;
        this.maxSleepMs = maxSleepMs;
    }

    @Override
    public boolean allowRetry(int retryCount) {
        if (retryCount < maxRetries) {
            return true;
        }
        return false;
    }

    @Override
    public long getSleepTimeMs(int retryCount) {
        if (retryCount < 0) {
            throw new IllegalArgumentException("retries count must greater than 0.");
        }
        if (retryCount > MAX_RETRIES_LIMIT) {
            System.out.println(String.format("maxRetries too large (%d). Pinning to %d", maxRetries, MAX_RETRIES_LIMIT));
            retryCount = MAX_RETRIES_LIMIT;
        }
        long sleepMs = baseSleepTimeMs *  Math.max(1, random.nextInt(1 << retryCount));
        System.out.println("retryCount = " + retryCount);
        System.out.println("1<<retryCount = " + (1<<retryCount));
        if (sleepMs > maxSleepMs) {
            System.out.println(String.format("Sleep extension too large (%d). Pinning to %d", sleepMs, maxSleepMs));
            sleepMs = maxSleepMs;
        }
        return sleepMs;
    }
}
