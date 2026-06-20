package com.xiaoyuan.lostfound.controller;

import com.xiaoyuan.lostfound.dto.LoginRequest;
import com.xiaoyuan.lostfound.model.LoginUser;
import com.xiaoyuan.lostfound.security.JwtUtil;
import com.xiaoyuan.lostfound.support.ApiException;
import com.xiaoyuan.lostfound.support.ApiResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final JdbcTemplate jdbcTemplate;
  private final JwtUtil jwtUtil;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public AuthController(JdbcTemplate jdbcTemplate, JwtUtil jwtUtil) {
    this.jdbcTemplate = jdbcTemplate;
    this.jwtUtil = jwtUtil;
  }

  @PostMapping("/login")
  public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
    List<Map<String, Object>> users = jdbcTemplate.queryForList(
        "SELECT * FROM users WHERE username = ? LIMIT 1",
        request.getUsername());

    if (users.isEmpty() || !"active".equals(users.get(0).get("status"))) {
      throw new ApiException(401, "账号不存在或已被禁用");
    }

    Map<String, Object> row = users.get(0);
    if (!passwordEncoder.matches(request.getPassword(), String.valueOf(row.get("password")))) {
      throw new ApiException(401, "用户名或密码错误");
    }

    LoginUser loginUser = new LoginUser(
        ((Number) row.get("id")).intValue(),
        String.valueOf(row.get("username")),
        String.valueOf(row.get("real_name")),
        String.valueOf(row.get("role")));

    Map<String, Object> safeUser = new HashMap<String, Object>();
    safeUser.put("id", loginUser.getId());
    safeUser.put("username", loginUser.getUsername());
    safeUser.put("realName", loginUser.getRealName());
    safeUser.put("role", loginUser.getRole());
    safeUser.put("phone", row.get("phone"));
    safeUser.put("college", row.get("college"));

    Map<String, Object> data = new HashMap<String, Object>();
    data.put("token", jwtUtil.createToken(loginUser));
    data.put("user", safeUser);
    return ApiResponse.ok(data, "登录成功");
  }

  @PostMapping("/register")
  public ApiResponse<Void> register(@RequestBody Map<String, Object> body) {
    String username = valueOrDefault(body.get("username"), "");
    String password = valueOrDefault(body.get("password"), "");
    String realName = valueOrDefault(body.get("realName"), "");

    if (username.length() < 3 || password.length() < 6 || realName.isEmpty()) {
      throw new ApiException(400, "用户名至少 3 位，密码至少 6 位，并请填写姓名");
    }

    Integer count = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM users WHERE username = ?",
        Integer.class,
        username);

    if (count != null && count > 0) {
      throw new ApiException(400, "用户名已存在");
    }

    jdbcTemplate.update(
        "INSERT INTO users (username, password, real_name, role, phone, college, status) VALUES (?, ?, ?, ?, ?, ?, ?)",
        username,
        passwordEncoder.encode(password),
        realName,
        "student",
        valueOrDefault(body.get("phone"), ""),
        valueOrDefault(body.get("college"), ""),
        "active");

    return ApiResponse.ok(null, "注册成功");
  }

  @GetMapping("/me")
  public ApiResponse<String> me() {
    return ApiResponse.ok("登录状态正常");
  }

  private String valueOrDefault(Object value, String fallback) {
    if (value == null) {
      return fallback;
    }
    String text = String.valueOf(value).trim();
    return text.isEmpty() ? fallback : text;
  }
}
