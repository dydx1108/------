package com.xiaoyuan.lostfound.controller;

import com.xiaoyuan.lostfound.support.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
  @GetMapping("/api/health")
  public ApiResponse<String> health() {
    return ApiResponse.ok("校园失物招领平台 Spring Boot API 正常运行");
  }
}
