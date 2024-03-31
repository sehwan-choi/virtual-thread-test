package com.test.asyncclient;

import feign.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

@Configuration
public class Config {

    @Bean
    public Client feignClient() {
        return new Client.Default(null,null);
    }

    @Bean
    public ThreadPoolTaskExecutor customExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(30);
        executor.setMaxPoolSize(30);
        executor.initialize();
        executor.setKeepAliveSeconds(5);
        return executor;
    }

    @Bean
    public ForkJoinPool customPorkJoinPool() {
        ForkJoinPool executor = new ForkJoinPool(30);
        return executor;
    }

    @Bean
    public ExecutorService virtualExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

}
