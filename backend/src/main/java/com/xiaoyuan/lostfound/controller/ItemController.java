package com.xiaoyuan.lostfound.controller;

import com.xiaoyuan.lostfound.dto.ItemRequest;
import com.xiaoyuan.lostfound.model.LoginUser;
import com.xiaoyuan.lostfound.security.RoleGuard;
import com.xiaoyuan.lostfound.support.ApiException;
import com.xiaoyuan.lostfound.support.ApiResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/items")
public class ItemController {
  private static final String SELECT_SQL =
      "SELECT i.*, c.name AS category_name, c.color AS category_color, u.real_name AS publisher_name "
          + "FROM items i JOIN categories c ON c.id = i.category_id JOIN users u ON u.id = i.publisher_id ";
  private static final long MAX_IMAGE_SIZE = 5L * 1024L * 1024L;

  private final JdbcTemplate jdbcTemplate;

  public ItemController(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @GetMapping
  public ApiResponse<List<Map<String, Object>>> list(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) Integer categoryId,
      @RequestParam(required = false) String mine) {
    LoginUser user = RoleGuard.currentUser();
    StringBuilder sql = new StringBuilder(SELECT_SQL);
    List<String> conditions = new ArrayList<String>();
    List<Object> params = new ArrayList<Object>();

    if (keyword != null && !keyword.trim().isEmpty()) {
      conditions.add("(i.title LIKE ? OR i.location LIKE ? OR i.description LIKE ?)");
      String like = "%" + keyword.trim() + "%";
      params.add(like);
      params.add(like);
      params.add(like);
    }
    if (type != null && !type.isEmpty()) {
      conditions.add("i.type = ?");
      params.add(type);
    }
    if (status != null && !status.isEmpty()) {
      conditions.add("i.status = ?");
      params.add(status);
    } else if ("student".equals(user.getRole()) && !"true".equals(mine)) {
      conditions.add("i.status IN ('published', 'claimed')");
    }
    if (categoryId != null) {
      conditions.add("i.category_id = ?");
      params.add(categoryId);
    }
    if ("true".equals(mine)) {
      conditions.add("i.publisher_id = ?");
      params.add(user.getId());
    }

    if (!conditions.isEmpty()) {
      sql.append(" WHERE ").append(String.join(" AND ", conditions));
    }
    sql.append(" ORDER BY i.created_at DESC");
    return ApiResponse.ok(jdbcTemplate.queryForList(sql.toString(), params.toArray()));
  }

  @GetMapping("/{id}")
  public ApiResponse<Map<String, Object>> detail(@PathVariable Integer id) {
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(SELECT_SQL + " WHERE i.id = ?", id);
    if (rows.isEmpty()) {
      throw new ApiException(404, "物品不存在");
    }
    return ApiResponse.ok(rows.get(0));
  }

  @PostMapping
  public ApiResponse<Void> create(@Valid @RequestBody ItemRequest request) {
    LoginUser user = RoleGuard.currentUser();
    String status = "admin".equals(user.getRole()) ? "published" : "pending";
    jdbcTemplate.update(
        "INSERT INTO items (title, type, category_id, location, event_date, description, image_url, contact_name, contact_phone, status, publisher_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        request.getTitle(), request.getType(), request.getCategoryId(), request.getLocation(),
        request.getEventDate(), request.getDescription(), defaultString(request.getImageUrl()),
        request.getContactName(), request.getContactPhone(), status, user.getId());
    return ApiResponse.ok(null, "pending".equals(status) ? "提交成功，等待管理员审核" : "发布成功");
  }

  @PostMapping("/upload")
  public ApiResponse<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new ApiException(400, "请选择要上传的图片");
    }
    if (file.getSize() > MAX_IMAGE_SIZE) {
      throw new ApiException(400, "图片大小不能超过 5MB");
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new ApiException(400, "只能上传图片文件");
    }

    String extension = getExtension(file.getOriginalFilename());
    if (extension.isEmpty()) {
      extension = ".png";
    }

    Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "items").toAbsolutePath().normalize();
    String filename = System.currentTimeMillis() + "-" + UUID.randomUUID().toString().replace("-", "") + extension;
    Path targetPath = uploadDir.resolve(filename).normalize();

    if (!targetPath.startsWith(uploadDir)) {
      throw new ApiException(400, "图片路径无效");
    }

    try {
      Files.createDirectories(uploadDir);
      Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException exception) {
      throw new ApiException(500, "图片上传失败，请稍后再试");
    }

    Map<String, String> data = new HashMap<String, String>();
    data.put("url", "/uploads/items/" + filename);
    return ApiResponse.ok(data, "图片上传成功");
  }

  @PutMapping("/{id}")
  public ApiResponse<Void> update(@PathVariable Integer id, @Valid @RequestBody ItemRequest request) {
    LoginUser user = RoleGuard.currentUser();
    Map<String, Object> current = findItem(id);
    if (!"admin".equals(user.getRole()) && !user.getId().equals(((Number) current.get("publisher_id")).intValue())) {
      throw new ApiException(403, "只能修改自己发布的物品");
    }
    String status = "admin".equals(user.getRole()) && request.getStatus() != null ? request.getStatus() : "pending";
    jdbcTemplate.update(
        "UPDATE items SET title = ?, type = ?, category_id = ?, location = ?, event_date = ?, description = ?, image_url = ?, contact_name = ?, contact_phone = ?, status = ? WHERE id = ?",
        request.getTitle(), request.getType(), request.getCategoryId(), request.getLocation(),
        request.getEventDate(), request.getDescription(), defaultString(request.getImageUrl()),
        request.getContactName(), request.getContactPhone(), status, id);
    return ApiResponse.ok(null, "物品修改成功");
  }

  @PatchMapping("/{id}/status")
  public ApiResponse<Void> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
    RoleGuard.requireRole("admin");
    jdbcTemplate.update("UPDATE items SET status = ? WHERE id = ?", body.get("status"), id);
    return ApiResponse.ok(null, "状态更新成功");
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Integer id) {
    LoginUser user = RoleGuard.currentUser();
    Map<String, Object> current = findItem(id);
    if (!"admin".equals(user.getRole()) && !user.getId().equals(((Number) current.get("publisher_id")).intValue())) {
      throw new ApiException(403, "只能删除自己发布的物品");
    }
    jdbcTemplate.update("DELETE FROM items WHERE id = ?", id);
    return ApiResponse.ok(null, "物品删除成功");
  }

  private Map<String, Object> findItem(Integer id) {
    List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM items WHERE id = ?", id);
    if (rows.isEmpty()) {
      throw new ApiException(404, "物品不存在");
    }
    return rows.get(0);
  }

  private String defaultString(String value) {
    return value == null ? "" : value;
  }

  private String getExtension(String filename) {
    String cleanName = StringUtils.cleanPath(filename == null ? "" : filename);
    int index = cleanName.lastIndexOf('.');
    if (index < 0) {
      return "";
    }
    return cleanName.substring(index);
  }
}
