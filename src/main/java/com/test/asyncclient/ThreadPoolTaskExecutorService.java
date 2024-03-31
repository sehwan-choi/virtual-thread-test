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

    public void test(int mainCount, int threadCount) {
        System.out.println("메인[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
        long start = System.currentTimeMillis();
        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        for (int i = 0 ; i < mainCount ; i++) {
            futures.add(create(customExecutor, threadCount));
        }
//        futures.add(create1(executor));

        futures.forEach(x -> x.join());

        long end = System.currentTimeMillis();
        System.out.println("메인[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
    }


    private CompletableFuture<List<String>> create(Executor executor, int threadCount) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
            long start = System.currentTimeMillis();
            List<String> result = new ArrayList<>();

            // 약 2초 걸림
            for (int i = 0 ; i < threadCount ; i++) {
                client.test();
            }
            long end = System.currentTimeMillis();
            System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
            return result;
        }, executor);
    }

    public void cpu(int mainCount) {
        System.out.println("Available processors (cores): " +
                Runtime.getRuntime().availableProcessors());
        System.out.println("메인[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
        long start = System.currentTimeMillis();
        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        for (int i = 0 ; i < mainCount ; i++) {
            futures.add(create2(customExecutor));
        }
//        futures.add(create1(executor));

        futures.forEach(x -> x.join());

        long end = System.currentTimeMillis();
        System.out.println("메인[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
    }

    private CompletableFuture<List<String>> create2(Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
            long start = System.currentTimeMillis();
            List<String> result = new ArrayList<>();

            // 약 3초 걸림
            for (int i = 0 ; i < 20000000 ; i++) {
                Stream.of(i).reduce((x,y) -> x+y);
            }
            long end = System.currentTimeMillis();
            System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
            return result;
        }, executor);
    }
}
