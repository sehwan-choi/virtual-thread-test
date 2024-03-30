package com.test.asyncclient;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ThreadPoolTaskExecutorService {

    private final TestClient client;

    private final ThreadPoolTaskExecutor customExecutor;
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
    public void test() {
        System.out.println("메인[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
        long start = System.currentTimeMillis();
        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        for (int i = 0 ; i < 90 ; i++) {
            futures.add(create(customExecutor));
        }
//        futures.add(create1(executor));

        futures.forEach(x -> x.join());

        long end = System.currentTimeMillis();
        System.out.println("메인[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
    }


    private CompletableFuture<List<String>> create(Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
            long start = System.currentTimeMillis();
            List<String> result = new ArrayList<>();

            // 약 2초 걸림
            for (int i = 0 ; i < 13 ; i++) {
                client.test();
            }
            long end = System.currentTimeMillis();
            System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
            return result;
        }, executor);
    }
}
