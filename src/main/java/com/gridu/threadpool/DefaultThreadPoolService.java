package com.gridu.threadpool;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class DefaultThreadPoolService implements ThreadPoolService {
    private final Queue<ThreadInfo> infoQueue = new ArrayDeque<>();

    private final List<Thread> threads;
    private final int maxThreads;

    private final Object queueLock = new Object();

    private boolean isShutdownInitiated;

    public DefaultThreadPoolService(int maxThreads) {

        if (maxThreads <= 0) {
            throw new IllegalArgumentException("Number of threads must be greater than zero");
        }

        this.threads = new ArrayList<>(maxThreads);
        this.maxThreads = maxThreads;
        initializeWorkerThreads();
    }

    @Override
    public void submit(Runnable runnable, long delay) {
        if (this.isShutdownInitiated) {
            return;
        }

        if (this.threads.stream().anyMatch(thread -> !thread.isAlive())) {
            startWorkers();
        }


        this.infoQueue.add(new ThreadInfo(runnable, delay));
    }

    @Override
    public void submit(Runnable runnable) {
        this.submit(runnable, 0);
    }

    private void initializeWorkerThreads() {
        for (int i = 0; i < this.maxThreads; i++) {
            Thread t = new Thread(() -> {
                while (true) {
                    ThreadInfo info;

                    synchronized (queueLock) {
                        if (this.isShutdownInitiated && this.infoQueue.isEmpty()) {
                            return;
                        }

                        if (this.infoQueue.isEmpty()) {
                            continue;
                        }

                        info = this.infoQueue.poll();
                    }

                    try {
                        if (info.getDelay() != 0) {
                            Thread.sleep(info.getDelay());
                        }

                        info.getRunnable().run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            this.threads.add(t);
        }
    }

    private synchronized void startWorkers() {
        if (this.threads.stream().allMatch(thread -> thread.isAlive())) {
            return;
        }

        this.threads.forEach(Thread::start);
    }

    @Override
    public void shutdown() {
        this.isShutdownInitiated = true;
    }
}
