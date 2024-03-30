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
public class ThreadPoolTaskExecutorTest {

    /**
     * newFixedThreadPool을 이용
     */

    /**
     *
     * create2() * 9 (약 2초)
     * create22() * 1 (약 7.5초)
     * [Thread[#42,ThreadPoolTaskExecutor-4,5,main]]걸린시간 : 4973ms
     * [Thread[#39,ThreadPoolTaskExecutor-1,5,main]]걸린시간 : 5107ms
     * [Thread[#43,ThreadPoolTaskExecutor-5,5,main]]걸린시간 : 6273ms
     * [Thread[#41,ThreadPoolTaskExecutor-3,5,main]]걸린시간 : 6350ms
     * [Thread[#40,ThreadPoolTaskExecutor-2,5,main]]걸린시간 : 6674ms
     * [Thread[#42,ThreadPoolTaskExecutor-4,5,main]]걸린시간 : 4682ms
     * [Thread[#39,ThreadPoolTaskExecutor-1,5,main]]걸린시간 : 4675ms
     * [Thread[#41,ThreadPoolTaskExecutor-3,5,main]]걸린시간 : 5402ms
     * [Thread[#43,ThreadPoolTaskExecutor-5,5,main]]걸린시간 : 5620ms
     * [Thread[#40,ThreadPoolTaskExecutor-2,5,main]]걸린시간 : 5272ms
     * [Thread[#42,ThreadPoolTaskExecutor-4,5,main]]걸린시간 : 8545ms
     * [Thread[#1,main,5,main]]걸린시간 : 18217ms
     *
     * create2() * 9 (약 2초)
     * [Thread[#43,ThreadPoolTaskExecutor-5,5,main]]걸린시간 : 4974ms
     * [Thread[#40,ThreadPoolTaskExecutor-2,5,main]]걸린시간 : 5168ms
     * [Thread[#42,ThreadPoolTaskExecutor-4,5,main]]걸린시간 : 6123ms
     * [Thread[#41,ThreadPoolTaskExecutor-3,5,main]]걸린시간 : 6163ms
     * [Thread[#39,ThreadPoolTaskExecutor-1,5,main]]걸린시간 : 6376ms
     * [Thread[#43,ThreadPoolTaskExecutor-5,5,main]]걸린시간 : 4604ms
     * [Thread[#40,ThreadPoolTaskExecutor-2,5,main]]걸린시간 : 4651ms
     * [Thread[#41,ThreadPoolTaskExecutor-3,5,main]]걸린시간 : 4870ms
     * [Thread[#39,ThreadPoolTaskExecutor-1,5,main]]걸린시간 : 5015ms
     * [Thread[#42,ThreadPoolTaskExecutor-4,5,main]]걸린시간 : 5212ms
     * [Thread[#1,main,5,main]]걸린시간 : 11436ms
     *
     * Process finished with exit code 0
     */
    @Test
    public void test() {
        Executor executor = getExecutor(300, 300);
        System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
        long start = System.currentTimeMillis();
        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        for (int i = 0 ; i < 300 ; i++) {
            futures.add(create(executor));
        }
//        futures.add(create1(executor));

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
            for (int i = 0 ; i < 10000000 ; i++) {
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
    private Executor getExecutor(int core, int max) {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(core);
        executor.setMaxPoolSize(max);
        executor.initialize();
        return executor;
    }
}
