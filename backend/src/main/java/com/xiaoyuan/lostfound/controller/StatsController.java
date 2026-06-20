package com.xiaoyuan.lostfound.controller;

import com.xiaoyuan.lostfound.support.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
  private final JdbcTemplate jdbcTemplate;

  public StatsController(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @GetMapping
  public ApiResponse<Map<String, Object>> stats() {
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("itemStats", jdbcTemplate.queryForList("SELECT status, COUNT(*) AS total FROM items GROUP BY status"));
    data.put("userStats", jdbcTemplate.queryForList("SELECT role, COUNT(*) AS total FROM users GROUP BY role"));
    data.put("claimStats", jdbcTemplate.queryForList("SELECT status, COUNT(*) AS total FROM claims GROUP BY status"));
    data.put("categoryStats", jdbcTemplate.queryForList(
        "SELECT c.name, COUNT(i.id) AS total FROM categories c LEFT JOIN items i ON i.category_id = c.id GROUP BY c.id ORDER BY total DESC"));
    return ApiResponse.ok(data);
  }
}
