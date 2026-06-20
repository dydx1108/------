package com.xiaoyuan.lostfound.support;

public class ApiResponse<T> {
  private boolean success;
  private String message;
  private T data;

  public ApiResponse(boolean success, String message, T data) {
    this.success = success;
    this.message = message;
    this.data = data;
  }

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<T>(true, "操作成功", data);
  }

  public static <T> ApiResponse<T> ok(T data, String message) {
    return new ApiResponse<T>(true, message, data);
  }

  public static <T> ApiResponse<T> fail(String message) {
    return new ApiResponse<T>(false, message, null);
  }

  public boolean isSuccess() {
    return success;
  }

  public String getMessage() {
    return message;
  }

  public T getData() {
    return data;
  }
}
