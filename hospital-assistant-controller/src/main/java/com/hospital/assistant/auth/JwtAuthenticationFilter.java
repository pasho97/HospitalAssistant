package com.hospital.assistant.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.assistant.model.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private static final ObjectMapper mapper = new ObjectMapper();
  private final AuthenticationManager authenticationManager;

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;

    setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
                                              HttpServletResponse response) throws AuthenticationException {
    String username = "";
    log.info("Attempting auth {}", request.getRequestURI());
    String password = "";
    Role role;
    try {
      String body = request.getReader().lines().collect(Collectors.joining("\n"));
      JsonNode node = mapper.readTree(body);
      username = node.get(SecurityConstants.USERNAME_KEY).asText();
      password = node.get(SecurityConstants.PASSWORD_KEY).asText();
      role = Role.valueOf(node.get(SecurityConstants.ROLE_KEY).asText());
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return null;
    }
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                                                                                                      password,
                                                                                                      Collections.singletonList(
                                                                                                          new SimpleGrantedAuthority(
                                                                                                              role.name())));

    return authenticationManager.authenticate(authenticationToken);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                          FilterChain filterChain, Authentication authentication) {
    User user = ((User) authentication.getPrincipal());

    List<String> roles = user.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();

    String token = Jwts.builder()
        .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
        .setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
        .setIssuer(SecurityConstants.TOKEN_ISSUER)
        .setAudience(SecurityConstants.TOKEN_AUDIENCE)
        .setSubject(user.getUsername())
        .setExpiration(new Date(System.currentTimeMillis() + 864000000))
        .claim("rol", roles)
        .compact();

    response.addHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token);
  }
}