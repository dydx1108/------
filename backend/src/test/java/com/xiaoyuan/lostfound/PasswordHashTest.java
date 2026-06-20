package com.xiaoyuan.lostfound;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class PasswordHashTest {
  @Test
  void seedPasswordMatchesDefaultPassword() {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String seedHash = "$2b$10$5SvK3rleo2da4cVM30UJoe/di/jtTFuk2Yyt43aHsV80hLht46N6m";
    assertTrue(encoder.matches("123456", seedHash));
  }
}
