package com.concurrent.concurrent.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 【使用synchronized锁】
 *  一 sychronized:jvm层次实现的，会自动加锁与解锁,当只有少量竞争者的时候,
 *  sychronized是个很好的锁实现;
 *  二 线程不少,但是线程增长的趋势是可以预估的,这时候ReentrantLock是一个很好的通用的锁实现;
 *  特别注意解锁,防止死锁;
 *
 *使用:
 * synchronized的实现 依赖于jvm，所以不会忘记释放锁，因为，在退出synchronized块的时候，JVM会帮助释放锁。
 * synchronized非公平锁，其优化借鉴ReentrantLock中的CAS，引入了偏量锁轻量级锁，也就是咨询锁后，两者性能差不多，
 * 在两种方法都可用的情况下，官方更建议使用synchronized，写法更容易。
 *
 * ReentrantLock（可重入锁）的实现 依赖于代码，默认不公平锁， 独有功能
 *
 */
@Slf4j
public class SynchronizedLockDemo {

    // 请求总数
    public static int clientTotal = 5000;

    // 同时并发执行的线程数
    public static int threadTotal = 200;

    public static int count = 0;

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(threadTotal);
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
        for (int i = 0; i < clientTotal ; i++) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    add();
                    semaphore.release();
                } catch (Exception e) {
                    log.error("exception", e);
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("count:{}", count);
    }

    private synchronized static void add() {
        count++;
    }
}
