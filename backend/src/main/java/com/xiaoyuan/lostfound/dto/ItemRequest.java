package com.xiaoyuan.lostfound.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ItemRequest {
  @NotBlank(message = "标题不能为空")
  private String title;

  @NotBlank(message = "类型不能为空")
  private String type;

  @NotNull(message = "分类不能为空")
  private Integer categoryId;

  @NotBlank(message = "地点不能为空")
  private String location;

  @NotBlank(message = "日期不能为空")
  private String eventDate;

  @NotBlank(message = "描述不能为空")
  private String description;

  private String imageUrl;

  @NotBlank(message = "联系人不能为空")
  private String contactName;

  @NotBlank(message = "联系电话不能为空")
  private String contactPhone;

  private String status;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Integer categoryId) {
    this.categoryId = categoryId;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getEventDate() {
    return eventDate;
  }

  public void setEventDate(String eventDate) {
    this.eventDate = eventDate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getContactPhone() {
    return contactPhone;
  }

  public void setContactPhone(String contactPhone) {
    this.contactPhone = contactPhone;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
