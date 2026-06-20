package com.xiaoyuan.lostfound.controller;

import com.xiaoyuan.lostfound.model.LoginUser;
import com.xiaoyuan.lostfound.security.RoleGuard;
import com.xiaoyuan.lostfound.support.ApiException;
import com.xiaoyuan.lostfound.support.ApiResponse;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final JdbcTemplate jdbcTemplate;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public UserController(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @GetMapping
  public ApiResponse<List<Map<String, Object>>> list(@RequestParam(required = false) String keyword) {
    RoleGuard.requireRole("admin");
    if (keyword == null || keyword.trim().isEmpty()) {
      return ApiResponse.ok(jdbcTemplate.queryForList(
          "SELECT id, username, real_name AS realName, role, phone, college, status, created_at FROM users ORDER BY id DESC"));
    }
    String like = "%" + keyword.trim() + "%";
    return ApiResponse.ok(jdbcTemplate.queryForList(
        "SELECT id, username, real_name AS realName, role, phone, college, status, created_at FROM users WHERE username LIKE ? OR real_name LIKE ? OR college LIKE ? ORDER BY id DESC",
        like, like, like));
  }

  @PostMapping
  public ApiResponse<Void> create(@RequestBody Map<String, Object> body) {
    RoleGuard.requireRole("admin");
    if (blank(body.get("username")) || blank(body.get("password")) || blank(body.get("realName")) || blank(body.get("role"))) {
      throw new ApiException(400, "请完整填写用户信息");
    }
    jdbcTemplate.update(
        "INSERT INTO users (username, password, real_name, role, phone, college, status) VALUES (?, ?, ?, ?, ?, ?, ?)",
        body.get("username"),
        passwordEncoder.encode(String.valueOf(body.get("password"))),
        body.get("realName"),
        body.get("role"),
        valueOrDefault(body.get("phone"), ""),
        valueOrDefault(body.get("college"), ""),
        valueOrDefault(body.get("status"), "active"));
    return ApiResponse.ok(null, "用户新增成功");
  }

  @PutMapping("/{id}")
  public ApiResponse<Void> update(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
    RoleGuard.requireRole("admin");
    if (blank(body.get("realName")) || blank(body.get("role"))) {
      throw new ApiException(400, "姓名和角色不能为空");
    }
    jdbcTemplate.update(
        "UPDATE users SET real_name = ?, role = ?, phone = ?, college = ?, status = ? WHERE id = ?",
        body.get("realName"),
        body.get("role"),
        valueOrDefault(body.get("phone"), ""),
        valueOrDefault(body.get("college"), ""),
        valueOrDefault(body.get("status"), "active"),
        id);
    return ApiResponse.ok(null, "用户修改成功");
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Integer id) {
    LoginUser user = RoleGuard.requireRole("admin");
    if (user.getId().equals(id)) {
      throw new ApiException(400, "不能删除当前登录账号");
    }
    jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    return ApiResponse.ok(null, "用户删除成功");
  }

  private boolean blank(Object value) {
    return value == null || String.valueOf(value).trim().isEmpty();
  }

  private String valueOrDefault(Object value, String fallback) {
    return blank(value) ? fallback : String.valueOf(value);
  }
}
