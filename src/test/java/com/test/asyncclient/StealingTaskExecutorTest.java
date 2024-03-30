package com.test.asyncclient;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@SpringBootTest
@EnableFeignClients
public class StealingTaskExecutorTest {

    /**
     * newWorkStealingPool 이용
     */

    /**
     *
     * create() * 10 (약 2초)
     * create1() * 1 (약 7.5초)
     *[Thread[#39,ForkJoinPool-1-worker-1,5,main]]걸린시간 : 6605ms
     * [Thread[#45,ForkJoinPool-1-worker-7,5,main]]걸린시간 : 6689ms
     * [Thread[#42,ForkJoinPool-1-worker-4,5,main]]걸린시간 : 7065ms
     * [Thread[#44,ForkJoinPool-1-worker-6,5,main]]걸린시간 : 10850ms
     * [Thread[#41,ForkJoinPool-1-worker-3,5,main]]걸린시간 : 10874ms
     * [Thread[#48,ForkJoinPool-1-worker-10,5,main]]걸린시간 : 10866ms
     * [Thread[#43,ForkJoinPool-1-worker-5,5,main]]걸린시간 : 11074ms
     * [Thread[#46,ForkJoinPool-1-worker-8,5,main]]걸린시간 : 11088ms
     * [Thread[#40,ForkJoinPool-1-worker-2,5,main]]걸린시간 : 11149ms
     * [Thread[#47,ForkJoinPool-1-worker-9,5,main]]걸린시간 : 11124ms
     * [Thread[#49,ForkJoinPool-1-worker-11,5,main]]걸린시간 : 15933ms
     * [Thread[#1,main,5,main]]걸린시간 : 16026ms
     *
     * create() * 10 (약 2초)
     * [Thread[#40,ForkJoinPool-1-worker-2,5,main]]걸린시간 : 6233ms
     * [Thread[#45,ForkJoinPool-1-worker-7,5,main]]걸린시간 : 6404ms
     * [Thread[#48,ForkJoinPool-1-worker-10,5,main]]걸린시간 : 6317ms
     * [Thread[#43,ForkJoinPool-1-worker-5,5,main]]걸린시간 : 9461ms
     * [Thread[#44,ForkJoinPool-1-worker-6,5,main]]걸린시간 : 10199ms
     * [Thread[#41,ForkJoinPool-1-worker-3,5,main]]걸린시간 : 10235ms
     * [Thread[#46,ForkJoinPool-1-worker-8,5,main]]걸린시간 : 10151ms
     * [Thread[#42,ForkJoinPool-1-worker-4,5,main]]걸린시간 : 10470ms
     * [Thread[#39,ForkJoinPool-1-worker-1,5,main]]걸린시간 : 10496ms
     * [Thread[#47,ForkJoinPool-1-worker-9,5,main]]걸린시간 : 10388ms
     * [Thread[#1,main,5,main]]걸린시간 : 10525ms
     *
     */
    @Test
    public void test() {
        Executor executor = Executors.newWorkStealingPool();
        System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
        long start = System.currentTimeMillis();
        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        for (int i = 0 ; i < 10 ; i++) {
            futures.add(create(executor));
        }
        futures.add(create1(executor));

        futures.forEach(x -> x.join());

        long end = System.currentTimeMillis();
        System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
    }


    private CompletableFuture<List<String>> create(Executor executor) {
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

    private CompletableFuture<List<String>> create1(Executor executor) {
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
}
