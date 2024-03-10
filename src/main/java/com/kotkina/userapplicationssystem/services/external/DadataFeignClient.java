package com.kotkina.userapplicationssystem.services.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "phone-service", url = "${dadata.client.url}")
public interface DadataFeignClient {

    @RequestMapping(method = RequestMethod.POST, value = "/phone")
    String checkPhone(@RequestHeader(HttpHeaders.AUTHORIZATION) Map<String, String> headerMap, @RequestBody String request);
}
