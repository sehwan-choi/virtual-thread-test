package com.test.asyncclient;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DefaultThreadService {

    private final TestClient testClient;

    private final ForkJoinPool customPorkJoinPool;

    /**
     * CompletableFuture의 기본 ThreadPool은 PortJoinPool을 사용한다
     */

    public void test(int mainCount, int threadCount) {
        long start = System.currentTimeMillis();
        System.out.println("메인[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        for (int i = 0 ; i < mainCount ; i++) {
            futures.add(create(threadCount));
        }

        futures.forEach(x -> x.join());

        long end = System.currentTimeMillis();
        System.out.println("메인[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
    }

    private CompletableFuture<List<String>> create(int threadCount) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
            long start = System.currentTimeMillis();
            List<String> result = new ArrayList<>();
            // 약 2초 걸림
            for (int i = 0 ; i < threadCount ; i++) {
                testClient.test();
            }
            long end = System.currentTimeMillis();
            System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
            return result;
        });
    }
}
