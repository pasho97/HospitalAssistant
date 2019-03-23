package com.hospital.assistant.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
  private static final ObjectMapper mapper = new ObjectMapper();

  public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
    super(authenticationManager);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws IOException, ServletException {
    String header = request.getHeader(SecurityConstants.TOKEN_HEADER);
    if (StringUtils.isEmpty(header) || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }
    UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }

  private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
    String token = request.getHeader(SecurityConstants.TOKEN_HEADER);
    if (!StringUtils.isEmpty(token)) {
      try {
        byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();

        Jws<Claims> parsedToken = Jwts.parser()
            .setSigningKey(signingKey)
            .parseClaimsJws(token.replace("Bearer ", ""));

        String username = parsedToken
            .getBody()
            .getSubject();

        List<SimpleGrantedAuthority> authorities = ((List<?>) parsedToken.getBody()
            .get("rol")).stream()
            .map(authority -> new SimpleGrantedAuthority((String) authority))
            .collect(Collectors.toList());

        if (!StringUtils.isEmpty(username)) {
          return new UsernamePasswordAuthenticationToken(username, null, authorities);
        }
      } catch (ExpiredJwtException exception) {
        log.warn("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
      } catch (UnsupportedJwtException exception) {
        log.warn("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
      } catch (MalformedJwtException exception) {
        log.warn("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
      } catch (SignatureException exception) {
        log.warn("Request to parse JWT with invalid signature : {} failed : {}", token, exception.getMessage());
      } catch (IllegalArgumentException exception) {
        log.warn("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
      }
    }

    return null;
  }
}