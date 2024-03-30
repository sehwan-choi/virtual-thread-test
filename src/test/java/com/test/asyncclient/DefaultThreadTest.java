package com.test.asyncclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@SpringBootTest
@EnableFeignClients
public class DefaultThreadTest {

    /**
     * CompletableFuture의 기본 ThreadPool은 PortJoinPool을 사용한다
     */

    @Test
    public void test() {
        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        for (int i = 0 ; i < 10 ; i++) {
            futures.add(create());
        }

        futures.forEach(x -> x.join());

        Executors.newWorkStealingPool();
    }

    private CompletableFuture<List<String>> create() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
            long start = System.currentTimeMillis();
            List<String> result = new ArrayList<>();
            // 약 2초 걸림
            for (int i = 0 ; i < 50000000 ; i++) {
                Stream.of(i).reduce((x,y) -> x+y);
            }
            long end = System.currentTimeMillis();
            System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
            return result;
        });
    }

    /**
     * 기본 ThreadPool 10개
     *
     * create2() * 9 (약 2초)
     * create22() * 1 (약 7.5초)
     * [Thread[#39,ForkJoinPool.commonPool-worker-1,5,main]]걸린시간 : 7078ms
     * [Thread[#44,ForkJoinPool.commonPool-worker-6,5,main]]걸린시간 : 7210ms
     * [Thread[#42,ForkJoinPool.commonPool-worker-4,5,main]]걸린시간 : 7305ms
     * [Thread[#47,ForkJoinPool.commonPool-worker-9,5,main]]걸린시간 : 7289ms
     * [Thread[#41,ForkJoinPool.commonPool-worker-3,5,main]]걸린시간 : 10869ms
     * [Thread[#46,ForkJoinPool.commonPool-worker-8,5,main]]걸린시간 : 10913ms
     * [Thread[#40,ForkJoinPool.commonPool-worker-2,5,main]]걸린시간 : 11106ms
     * [Thread[#43,ForkJoinPool.commonPool-worker-5,5,main]]걸린시간 : 11120ms
     * [Thread[#45,ForkJoinPool.commonPool-worker-7,5,main]]걸린시간 : 11107ms
     * [Thread[#48,ForkJoinPool.commonPool-worker-10,5,main]]걸린시간 : 16678ms
     * [Thread[#1,main,5,main]]걸린시간 : 16766ms
     *
     * create2() * 9 (약 2초)
     * [Thread[#46,ForkJoinPool.commonPool-worker-8,5,main]]걸린시간 : 5921ms
     * [Thread[#43,ForkJoinPool.commonPool-worker-5,5,main]]걸린시간 : 6136ms
     * [Thread[#40,ForkJoinPool.commonPool-worker-2,5,main]]걸린시간 : 6148ms
     * [Thread[#42,ForkJoinPool.commonPool-worker-4,5,main]]걸린시간 : 9311ms
     * [Thread[#41,ForkJoinPool.commonPool-worker-3,5,main]]걸린시간 : 9393ms
     * [Thread[#39,ForkJoinPool.commonPool-worker-1,5,main]]걸린시간 : 9467ms
     * [Thread[#44,ForkJoinPool.commonPool-worker-6,5,main]]걸린시간 : 9467ms
     * [Thread[#45,ForkJoinPool.commonPool-worker-7,5,main]]걸린시간 : 9469ms
     * [Thread[#47,ForkJoinPool.commonPool-worker-9,5,main]]걸린시간 : 9491ms
     * [Thread[#1,main,5,main]]걸린시간 : 9523ms
     *
     */
    @Test
    public void test2() {
        System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
        long start = System.currentTimeMillis();
        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        for (int i = 0 ; i < 9 ; i++) {
            futures.add(create2());
        }
        futures.add(create22());

        futures.forEach(x -> x.join());
        long end = System.currentTimeMillis();
        System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
    }

    private CompletableFuture<List<String>> create2() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
            long start = System.currentTimeMillis();
            List<String> result = new ArrayList<>();

            // 약 2초 걸림
            for (int i = 0 ; i < 50000000 ; i++) {
                Stream.of(i).reduce((x,y) -> x+y);
            }
            long end = System.currentTimeMillis();
            System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
            return result;
        });
    }

    private CompletableFuture<List<String>> create22() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
            long start = System.currentTimeMillis();

            // 약 7.6초 걸림
            List<String> result = new ArrayList<>();
            for (int i = 0 ; i < 200000000 ; i++) {
                Stream.of(i).reduce((x,y) -> x+y);
            }
            long end = System.currentTimeMillis();
            System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
            return result;
        });
    }

    /**
     * 기본 ThreadPool 5개
     *
     * create2() * 9 (약 2초)
     * create22() * 1 (약 7.5초)
     * [Thread[#41,ThreadPoolTaskExecutor-3,5,main]]걸린시간 : 4741ms
     * [Thread[#43,ThreadPoolTaskExecutor-5,5,main]]걸린시간 : 5726ms
     * [Thread[#42,ThreadPoolTaskExecutor-4,5,main]]걸린시간 : 5752ms
     * [Thread[#40,ThreadPoolTaskExecutor-2,5,main]]걸린시간 : 6039ms
     * [Thread[#39,ThreadPoolTaskExecutor-1,5,main]]걸린시간 : 6219ms
     * [Thread[#41,ThreadPoolTaskExecutor-3,5,main]]걸린시간 : 4568ms
     * [Thread[#42,ThreadPoolTaskExecutor-4,5,main]]걸린시간 : 5174ms
     * [Thread[#43,ThreadPoolTaskExecutor-5,5,main]]걸린시간 : 5214ms
     * [Thread[#40,ThreadPoolTaskExecutor-2,5,main]]걸린시간 : 5050ms
     * [Thread[#39,ThreadPoolTaskExecutor-1,5,main]]걸린시간 : 10429ms
     * [Thread[#1,main,5,main]]걸린시간 : 16656ms
     *
     * create2() * 9 (약 2초)
     * [Thread[#41,ThreadPoolTaskExecutor-3,5,main]]걸린시간 : 4884ms
     * [Thread[#42,ThreadPoolTaskExecutor-4,5,main]]걸린시간 : 5573ms
     * [Thread[#39,ThreadPoolTaskExecutor-1,5,main]]걸린시간 : 5677ms
     * [Thread[#43,ThreadPoolTaskExecutor-5,5,main]]걸린시간 : 5719ms
     * [Thread[#40,ThreadPoolTaskExecutor-2,5,main]]걸린시간 : 5866ms
     * [Thread[#41,ThreadPoolTaskExecutor-3,5,main]]걸린시간 : 4908ms
     * [Thread[#39,ThreadPoolTaskExecutor-1,5,main]]걸린시간 : 4447ms
     * [Thread[#43,ThreadPoolTaskExecutor-5,5,main]]걸린시간 : 4471ms
     * [Thread[#42,ThreadPoolTaskExecutor-4,5,main]]걸린시간 : 4625ms
     * [Thread[#1,main,5,main]]걸린시간 : 10204ms
     *
     * Process finished with exit code 0
     *
     */
    @Test
    public void test3() {

        Executor executor = getExecutor();

        System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
        long start = System.currentTimeMillis();
        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        for (int i = 0 ; i < 9 ; i++) {
            futures.add(create3(executor));
        }
//        futures.add(create33(executor));

        futures.forEach(CompletableFuture::join);
        long end = System.currentTimeMillis();
        System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
    }

    private CompletableFuture<List<String>> create3(Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
            long start = System.currentTimeMillis();
            List<String> result = new ArrayList<>();

            // 약 2초 걸림
            for (int i = 0 ; i < 50000000 ; i++) {
                Stream.of(i).reduce((x,y) -> x+y);
            }
            long end = System.currentTimeMillis();
            System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
            return result;
        }, executor);
    }

    private CompletableFuture<List<String>> create33(Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
            long start = System.currentTimeMillis();

            // 약 7.6초 걸림
            List<String> result = new ArrayList<>();
            for (int i = 0 ; i < 200000000 ; i++) {
                Stream.of(i).reduce((x,y) -> x+y);
            }
            long end = System.currentTimeMillis();
            System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
            return result;
        }, executor);
    }

    private Executor getExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.initialize();
        return executor;
    }
}
