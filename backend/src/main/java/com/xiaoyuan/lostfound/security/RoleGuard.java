package com.xiaoyuan.lostfound.security;

import com.xiaoyuan.lostfound.model.LoginUser;
import com.xiaoyuan.lostfound.support.ApiException;

public final class RoleGuard {
  private RoleGuard() {
  }

  public static LoginUser currentUser() {
    LoginUser user = AuthContext.get();
    if (user == null) {
      throw new ApiException(401, "请先登录");
    }
    return user;
  }

  public static LoginUser requireRole(String role) {
    LoginUser user = currentUser();
    if (!role.equals(user.getRole())) {
      throw new ApiException(403, "当前账号无权访问该资源");
    }
    return user;
  }
}
