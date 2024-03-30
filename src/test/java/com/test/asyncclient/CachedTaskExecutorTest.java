package com.test.asyncclient;

import org.junit.jupiter.api.Test;
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
public class CachedTaskExecutorTest {

    /**
     * cachedThreadPool을 이용
     */

    /**
     *
     * create() * 10 (약 2초)
     * create1() * 1 (약 7.5초)
     * [Thread[#47,pool-2-thread-9,5,main]]걸린시간 : 6730ms
     * [Thread[#41,pool-2-thread-3,5,main]]걸린시간 : 6798ms
     * [Thread[#44,pool-2-thread-6,5,main]]걸린시간 : 6947ms
     * [Thread[#39,pool-2-thread-1,5,main]]걸린시간 : 10713ms
     * [Thread[#48,pool-2-thread-10,5,main]]걸린시간 : 10938ms
     * [Thread[#43,pool-2-thread-5,5,main]]걸린시간 : 11039ms
     * [Thread[#42,pool-2-thread-4,5,main]]걸린시간 : 11073ms
     * [Thread[#46,pool-2-thread-8,5,main]]걸린시간 : 11088ms
     * [Thread[#40,pool-2-thread-2,5,main]]걸린시간 : 11137ms
     * [Thread[#45,pool-2-thread-7,5,main]]걸린시간 : 11229ms
     * [Thread[#49,pool-2-thread-11,5,main]]걸린시간 : 16188ms
     * [Thread[#1,main,5,main]]걸린시간 : 16200ms
     *
     *
     * create() * 10 (약 2초)
     * [Thread[#42,pool-2-thread-4,5,main]]걸린시간 : 6542ms
     * [Thread[#39,pool-2-thread-1,5,main]]걸린시간 : 6620ms
     * [Thread[#48,pool-2-thread-10,5,main]]걸린시간 : 6667ms
     * [Thread[#45,pool-2-thread-7,5,main]]걸린시간 : 6720ms
     * [Thread[#47,pool-2-thread-9,5,main]]걸린시간 : 9804ms
     * [Thread[#43,pool-2-thread-5,5,main]]걸린시간 : 9954ms
     * [Thread[#41,pool-2-thread-3,5,main]]걸린시간 : 9964ms
     * [Thread[#44,pool-2-thread-6,5,main]]걸린시간 : 9963ms
     * [Thread[#46,pool-2-thread-8,5,main]]걸린시간 : 9966ms
     * [Thread[#40,pool-2-thread-2,5,main]]걸린시간 : 10003ms
     * [Thread[#1,main,5,main]]걸린시간 : 10007ms
     *
     */
    @Test
    public void test() {
        Executor executor = Executors.newCachedThreadPool();
        System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
        long start = System.currentTimeMillis();
        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        for (int i = 0 ; i < 100 ; i++) {
            futures.add(create(executor));
        }
//        futures.add(create1(executor));

        futures.forEach(x -> x.join());

        long end = System.currentTimeMillis();
        System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
    }

    @Test
    public void test2() {
        Executor executor = Executors.newCachedThreadPool();
        System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
        long start = System.currentTimeMillis();
        create(executor).join();
        create(executor).join();
        create(executor).join();
        create(executor).join();
        create(executor).join();
        create(executor).join();
        create(executor).join();
        create(executor).join();

        long end = System.currentTimeMillis();
        System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
    }


    private CompletableFuture<List<String>> create(Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
            long start = System.currentTimeMillis();
            List<String> result = new ArrayList<>();

            // 약 2초 걸림
            for (int i = 0 ; i < 5000000 ; i++) {
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
