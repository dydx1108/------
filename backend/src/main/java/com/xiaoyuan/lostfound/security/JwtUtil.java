package com.xiaoyuan.lostfound.security;

import com.xiaoyuan.lostfound.model.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
  private final SecretKey key;
  private final long expireMillis;

  public JwtUtil(
      @Value("${app.jwt-secret}") String secret,
      @Value("${app.jwt-expire-hours}") long expireHours) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expireMillis = expireHours * 60 * 60 * 1000;
  }

  public String createToken(LoginUser user) {
    Date now = new Date();
    return Jwts.builder()
        .setSubject(String.valueOf(user.getId()))
        .claim("username", user.getUsername())
        .claim("realName", user.getRealName())
        .claim("role", user.getRole())
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + expireMillis))
        .signWith(key)
        .compact();
  }

  public LoginUser parseToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();

    return new LoginUser(
        Integer.valueOf(claims.getSubject()),
        claims.get("username", String.class),
        claims.get("realName", String.class),
        claims.get("role", String.class));
  }
}
