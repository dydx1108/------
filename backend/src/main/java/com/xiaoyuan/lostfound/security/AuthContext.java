package com.xiaoyuan.lostfound.security;

import com.xiaoyuan.lostfound.model.LoginUser;

public final class AuthContext {
  private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<LoginUser>();

  private AuthContext() {
  }

  public static void set(LoginUser user) {
    HOLDER.set(user);
  }

  public static LoginUser get() {
    return HOLDER.get();
  }

  public static void clear() {
    HOLDER.remove();
  }
}
