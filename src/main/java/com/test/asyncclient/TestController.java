package com.test.asyncclient;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final ThreadPoolTaskExecutorService threadPoolTaskExecutorService;

    private final DefaultThreadService defaultThreadService;

    private final VirtualThreadExecutorService virtualThreadPoolTaskExecutorService;
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

    @GetMapping("/1")
    public String test1() {
        threadPoolTaskExecutorService.test();
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
    @GetMapping("/v")
    public String vtest1() {
        virtualThreadPoolTaskExecutorService.test();
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

    @GetMapping("/2")
    public String test2() {
        defaultThreadService.test();
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

    @GetMapping("/3")
    public String test21() {
        defaultThreadService.test2();
        return "OK";
    }
}
