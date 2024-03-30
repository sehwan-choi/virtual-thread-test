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

    public void test() {
        long start = System.currentTimeMillis();
        System.out.println("메인[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        for (int i = 0 ; i < 90 ; i++) {
            futures.add(create());
        }

        futures.forEach(x -> x.join());

        long end = System.currentTimeMillis();
        System.out.println("메인[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
    }

    private CompletableFuture<List<String>> create() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
            long start = System.currentTimeMillis();
            List<String> result = new ArrayList<>();
            // 약 2초 걸림
            for (int i = 0 ; i < 13 ; i++) {
                testClient.test();
            }
            long end = System.currentTimeMillis();
            System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
            return result;
        });
    }

    public void test2() {

        Thread.Builder.OfVirtual ofVirtual = Thread.ofVirtual();
        System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
        long start = System.currentTimeMillis();
        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        for (int i = 0 ; i < 90 ; i++) {
            futures.add(create2(customPorkJoinPool));
        }
//        futures.add(create33(executor));

        futures.forEach(CompletableFuture::join);
        long end = System.currentTimeMillis();
        System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
    }

    private CompletableFuture<List<String>> create2(Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
            long start = System.currentTimeMillis();
            List<String> result = new ArrayList<>();
            // 약 2초 걸림

            for (int i = 0 ; i < 13 ; i++) {
                testClient.test();
            }
            long end = System.currentTimeMillis();
            System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
            return result;
        }, executor);
    }
}
