package com.gridu.threadpool;

public interface ThreadPoolService {
    void submit(Runnable runnable, long timeout);

    void submit(Runnable runnable);

    void shutdown();
}
