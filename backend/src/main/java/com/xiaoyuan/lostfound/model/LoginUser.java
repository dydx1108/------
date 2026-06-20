package com.xiaoyuan.lostfound.model;

public class LoginUser {
  private Integer id;
  private String username;
  private String realName;
  private String role;

  public LoginUser() {
  }

  public LoginUser(Integer id, String username, String realName, String role) {
    this.id = id;
    this.username = username;
    this.realName = realName;
    this.role = role;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getRealName() {
    return realName;
  }

  public void setRealName(String realName) {
    this.realName = realName;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
