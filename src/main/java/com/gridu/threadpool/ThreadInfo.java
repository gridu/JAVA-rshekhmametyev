package com.gridu.threadpool;

class ThreadInfo {
    private Runnable runnable;

    private long delay;

    Runnable getRunnable() {
        return this.runnable;
    }

    long getDelay() {
        return this.delay;
    }

    ThreadInfo(Runnable runnable, long delay) {
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable cannot be null");
        }

        if (delay < 0) {
            throw new IllegalArgumentException("Delay cannot be less than zero");
        }

        this.runnable = runnable;
        this.delay = delay;
    }
}
