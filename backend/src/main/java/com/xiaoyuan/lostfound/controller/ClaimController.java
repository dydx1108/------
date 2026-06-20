package com.xiaoyuan.lostfound.controller;

import com.xiaoyuan.lostfound.model.LoginUser;
import com.xiaoyuan.lostfound.security.RoleGuard;
import com.xiaoyuan.lostfound.support.ApiException;
import com.xiaoyuan.lostfound.support.ApiResponse;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {
  private static final String SELECT_SQL =
      "SELECT cl.*, i.title AS item_title, i.image_url, i.type, u.real_name AS claimant_name, u.phone AS claimant_phone "
          + "FROM claims cl JOIN items i ON i.id = cl.item_id JOIN users u ON u.id = cl.user_id ";

  private final JdbcTemplate jdbcTemplate;

  public ClaimController(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @GetMapping
  public ApiResponse<List<Map<String, Object>>> list() {
    LoginUser user = RoleGuard.currentUser();
    if ("student".equals(user.getRole())) {
      return ApiResponse.ok(jdbcTemplate.queryForList(SELECT_SQL + " WHERE cl.user_id = ? ORDER BY cl.created_at DESC", user.getId()));
    }
    return ApiResponse.ok(jdbcTemplate.queryForList(SELECT_SQL + " ORDER BY cl.created_at DESC"));
  }

  @PostMapping
  public ApiResponse<Void> create(@RequestBody Map<String, Object> body) {
    LoginUser user = RoleGuard.requireRole("student");
    if (body.get("itemId") == null || blank(body.get("evidence"))) {
      throw new ApiException(400, "请填写认领说明");
    }
    jdbcTemplate.update(
        "INSERT INTO claims (item_id, user_id, evidence) VALUES (?, ?, ?)",
        body.get("itemId"), user.getId(), body.get("evidence"));
    return ApiResponse.ok(null, "认领申请已提交");
  }

  @PatchMapping("/{id}/review")
  public ApiResponse<Void> review(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
    RoleGuard.requireRole("admin");
    String status = String.valueOf(body.get("status"));
    jdbcTemplate.update(
        "UPDATE claims SET status = ?, reply = ? WHERE id = ?",
        status,
        body.get("reply") == null ? "" : body.get("reply"),
        id);
    if ("approved".equals(status)) {
      List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT item_id FROM claims WHERE id = ?", id);
      if (!rows.isEmpty()) {
        jdbcTemplate.update("UPDATE items SET status = 'claimed' WHERE id = ?", rows.get(0).get("item_id"));
      }
    }
    return ApiResponse.ok(null, "认领审核完成");
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Integer id) {
    LoginUser user = RoleGuard.currentUser();
    List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM claims WHERE id = ?", id);
    if (rows.isEmpty()) {
      throw new ApiException(404, "认领记录不存在");
    }
    if (!"admin".equals(user.getRole()) && !user.getId().equals(((Number) rows.get(0).get("user_id")).intValue())) {
      throw new ApiException(403, "只能删除自己的认领记录");
    }
    jdbcTemplate.update("DELETE FROM claims WHERE id = ?", id);
    return ApiResponse.ok(null, "认领记录已删除");
  }

  private boolean blank(Object value) {
    return value == null || String.valueOf(value).trim().isEmpty();
  }
}
