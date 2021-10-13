package com.chat.server.handler;

/**
 * @Program: chatRoom
 * @Description: 重试策略接口
 * @Author: LHB
 * @Version: v0.0.1
 * @Time: 2021-10-13 13:08
 **/
public interface RetryPolicy {
    boolean allowRetry(int retryCount);

    long getSleepTimeMs(int retryCount);
}
