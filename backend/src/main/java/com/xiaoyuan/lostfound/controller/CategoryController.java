package com.xiaoyuan.lostfound.controller;

import com.xiaoyuan.lostfound.security.RoleGuard;
import com.xiaoyuan.lostfound.support.ApiException;
import com.xiaoyuan.lostfound.support.ApiResponse;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
  private final JdbcTemplate jdbcTemplate;

  public CategoryController(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @GetMapping
  public ApiResponse<List<Map<String, Object>>> list() {
    return ApiResponse.ok(jdbcTemplate.queryForList("SELECT * FROM categories ORDER BY id ASC"));
  }

  @PostMapping
  public ApiResponse<Void> create(@RequestBody Map<String, Object> body) {
    RoleGuard.requireRole("admin");
    if (blank(body.get("name"))) {
      throw new ApiException(400, "分类名称不能为空");
    }
    jdbcTemplate.update(
        "INSERT INTO categories (name, color, description) VALUES (?, ?, ?)",
        body.get("name"),
        valueOrDefault(body.get("color"), "#1f9d88"),
        valueOrDefault(body.get("description"), ""));
    return ApiResponse.ok(null, "分类新增成功");
  }

  @PutMapping("/{id}")
  public ApiResponse<Void> update(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
    RoleGuard.requireRole("admin");
    if (blank(body.get("name"))) {
      throw new ApiException(400, "分类名称不能为空");
    }
    jdbcTemplate.update(
        "UPDATE categories SET name = ?, color = ?, description = ? WHERE id = ?",
        body.get("name"),
        valueOrDefault(body.get("color"), "#1f9d88"),
        valueOrDefault(body.get("description"), ""),
        id);
    return ApiResponse.ok(null, "分类修改成功");
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Integer id) {
    RoleGuard.requireRole("admin");
    jdbcTemplate.update("DELETE FROM categories WHERE id = ?", id);
    return ApiResponse.ok(null, "分类删除成功");
  }

  private boolean blank(Object value) {
    return value == null || String.valueOf(value).trim().isEmpty();
  }

  private String valueOrDefault(Object value, String fallback) {
    return blank(value) ? fallback : String.valueOf(value);
  }
}
