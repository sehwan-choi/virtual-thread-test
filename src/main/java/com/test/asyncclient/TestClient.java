package com.test.asyncclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "testClient", url = "localhost:8081")
public interface TestClient {

    @GetMapping
    Integer test();

    @GetMapping("/e")
    Integer teste();
}
