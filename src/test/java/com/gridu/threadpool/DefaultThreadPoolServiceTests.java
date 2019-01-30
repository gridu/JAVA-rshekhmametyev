package com.gridu.threadpool;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.StringJoiner;

public class DefaultThreadPoolServiceTests {
    private int numOfThreads;
    private ThreadPoolService service;

    @Before
    public void setUp() {
        this.numOfThreads = 4;
        this.service = new DefaultThreadPoolService(this.numOfThreads);
    }

    @After
    public void tearDown() {
        this.service.shutdown();
    }

    @Test (expected = IllegalArgumentException.class)
    public void throwsExceptionIfNumOfThreadsIsLessThanZero() {
        ThreadPoolService service = new DefaultThreadPoolService(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void throwsExceptionIfNumOfThreadsIsZero() {
        ThreadPoolService service = new DefaultThreadPoolService(0);
    }

    @Test
    public void shouldNotStartAnyThreadsOnConstruction() {
        int currentActiveThreadCount = Thread.activeCount();

        Assert.assertEquals(currentActiveThreadCount, Thread.activeCount());
    }

    @Test
    public void shouldSpawnTheSpecifiedNumberOfThreadsOnFirstSubmit() {
        int currentActiveThreadCount = Thread.activeCount();
        int numOfThreads = 4;

        this.service.submit(() -> {}, 0);

        Assert.assertEquals(currentActiveThreadCount + numOfThreads, Thread.activeCount());
    }

    @Test (expected = IllegalArgumentException.class)
    public void throwsExceptionIfDelayIsLessThanZero() {
        this.service.submit(() -> {}, -10);
    }

    @Test (expected = IllegalArgumentException.class)
    public void throwsExceptionIfRunnableIsNull() {
        this.service.submit(null, 10);
    }

    @Test
    public void executesAllSubmittedTasksWithDelays() throws InterruptedException {
        StringJoiner joiner = new StringJoiner("\n");

        this.service.submit(() -> joiner.add("a"), 0);
        this.service.submit(() -> joiner.add("b"), 10);
        this.service.submit(() -> joiner.add("c"), 20);

        Thread.sleep(30);

        Assert.assertEquals("a\nb\nc", joiner.toString());
    }

    @Test
    public void shouldNotAcceptMoreSubmitsAfterShutdown() {
        int activeThreads = Thread.activeCount();

        this.service.shutdown();
        this.service.submit(() -> {}, 0);

        Assert.assertEquals(activeThreads, Thread.activeCount());
    }
}