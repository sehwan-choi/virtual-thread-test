package com.test.asyncclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final ThreadPoolTaskExecutorService threadPoolTaskExecutorService;

    private final DefaultThreadService defaultThreadService;

    private final VirtualThreadExecutorService virtualThreadPoolTaskExecutorService;

    private final ExceptionCheckService checkService;
    static int count = 0;

    /**
     *  CustomTaskPool 사용
     *  [thread 30]
     *      count 30 * 50
     *      no virtual : 15814ms
     *      virtual : 15779ms
     *
     *      count 30 * 10
     *      no virtual : 3180ms
     *      virtual : 3197ms
     *
     *      count 60 * 25
     *      no virtual : 15926ms
     *      virtual : 15650ms
     *
     *      count 90 * 13
     *      no virtual : 12200ms
     *      virtual : 12368ms
     *
     *  [thread 10]
     *      count 30 * 50
     *      no virtual :
     *      virtual :
     *
     *      count 90 * 13
     *      no virtual : 36775ms
     *      virtual :
     */

    @GetMapping("/t/{mainCount}/{threadCount}")
    public String test1(@PathVariable("mainCount") int mainCount, @PathVariable("threadCount") int threadCount) {
        threadPoolTaskExecutorService.test(mainCount, threadCount);
        return "OK";
    }

    /**
     *  virtualThread 사용
     *      count 30 * 50
     *      no virtual : 15835ms
     *
     *      count 60 * 25
     *      no virtual : 7965ms
     *
     *      count 90 * 13
     *      virtual : 4257ms
     *
     *      count 90 * 50
     *      virtual : 15784ms
     *
     *      count 200 * 30
     *      virtual : 9646ms
     */
    @GetMapping("/v/{mainCount}/{threadCount}")
    public String vtest1(@PathVariable("mainCount") int mainCount, @PathVariable("threadCount") int threadCount) {
        virtualThreadPoolTaskExecutorService.test(mainCount, threadCount);
        return "OK";
    }

    /**
     *  기본 ForkJoinPool 사용 (기본 thread 10개)
     *  [thread 10]
     *      count 30 * 50
     *      no virtual : 46429ms (thread 10)
     *      virtual : 46637ms (thread 10)
     *
     *      count 30 * 10
     *      no virtual : 9221ms (thread 10)
     *      virtual : 9334ms (thread 10)
     *
     *      count 60 * 25
     *      no virtual : 46434ms
     *      virtual : 46375ms
     *
     *      count 90 * 13
     *      no virtual : 36240ms
     *      virtual : 36436ms
     */

    @GetMapping("/d/{mainCount}/{threadCount}")
    public String test2(@PathVariable("mainCount") int mainCount, @PathVariable("threadCount") int threadCount) {
        defaultThreadService.test(mainCount, threadCount);
        return "OK";
    }

    /**
     * Custom ForkJoinPool 사용
     *  [thread 30]
     *      count 30 * 50
     *      no virtual : 15639ms
     *
     *      count 30 * 10
     *      no virtual : 3215ms
     *
     *      count 60 * 25
     *      no virtual : 15761ms
     *
     *      count 90 * 13
     *      no virtual : 12262ms
     *
     *  [thread 10]
     *      count 30 * 50
     *
     *      count 90 * 13
     *      no virtual : 36553ms
     */

    @GetMapping("/e/{mainCount}/{threadCount}")
    public String teste(@PathVariable("mainCount") int mainCount, @PathVariable("threadCount") int threadCount) {
        List<Integer> test = checkService.test(mainCount,threadCount);
        String collect = test.stream().map(String::valueOf).collect(Collectors.joining(","));
        System.out.println("collect = " + collect);
        return "OK";
    }

    /**
     * count 10 (1개 2초씩)
     * 18228ms
     *
     * count 20
     * 35578ms
     *
     * count 30
     * 55986ms
     *
     * count 40
     * 77195ms
     *
     * count 50
     * 84960ms
     *
     * count 60
     *
     *
     * count 70
     */
    @GetMapping("/cpu/v/{mainCount}")
    public String cpuV(@PathVariable("mainCount") int mainCount) {
        virtualThreadPoolTaskExecutorService.cpu(mainCount);
        return "OK";
    }

    /**
     * count 10 (1개 2초씩)
     * 19385ms
     *
     * count 20
     * 40404ms
     *
     * count 30
     * 50942ms
     *
     * count 40
     * 71511ms
     *
     * count 50
     * 84784ms
     *
     * count 60
     *
     *
     * count 70
     *
     */
    @GetMapping("/cpu/{mainCount}")
    public String cpu(@PathVariable("mainCount") int mainCount) {
        threadPoolTaskExecutorService.cpu(mainCount);
        return "OK";
    }
}
