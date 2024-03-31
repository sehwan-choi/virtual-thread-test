package com.test.asyncclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExceptionCheckService {

    private final TestClient client;

    public List<Integer> test(int mainCount, int threadCount) {
        System.out.println("Available processors (cores): " +
                Runtime.getRuntime().availableProcessors());
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        System.out.println("메인[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount());
        long start = System.currentTimeMillis();
        List<CompletableFuture<List<Integer>>> futures = new ArrayList<>();

        for (int i = 0 ; i < mainCount ; i++) {
            futures.add(create(executorService, threadCount));
        }
//        futures.add(create1(executor));

//        List<Integer> collect = futures.stream().flatMap(x -> x.join().stream()).sorted().collect(Collectors.toList());

        List<List<Integer>> results = new ArrayList<>();
        for (CompletableFuture<List<Integer>> future : futures) {
            results.add(future.join());
        }

        List<Integer> collect = results.stream().flatMap(Collection::stream).sorted().collect(Collectors.toList());

        long end = System.currentTimeMillis();
        System.out.println("메인[" + Thread.currentThread() + "]현재 Thread 개수 : " + Thread.activeCount() + " 걸린시간 : " + (end - start) + "ms");
        return collect;
    }


    private CompletableFuture<List<Integer>> create(Executor executor, int threadCount) {
        return CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();
            List<Integer> result = new ArrayList<>();

            // 약 2초 걸림
            for (int i = 0 ; i < threadCount ; i++) {
                try {
                    result.add(client.teste());
                } catch (Exception e) {
                    log.error("ERROR!" + e);
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("[" + Thread.currentThread() + "]걸린시간 : " + (end - start) + "ms");
            return result;
        }, executor);
    }
}
