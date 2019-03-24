package com.hospital.assistant.api;

import com.hospital.assistant.auth.SecurityConstants;
import com.hospital.assistant.auth.SecurityUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;

public class ServerUtils {
  public static String extractUsernameFromHeaders(HttpHeaders httpHeaders) {
    List<String> authorizationTokens = httpHeaders.get(HttpHeaders.AUTHORIZATION);
    //This should never happen as this method requires auth
    if (CollectionUtils.isEmpty(authorizationTokens)) {
      return null;
    }
    String token = authorizationTokens.get(0);
    String username;
    if (token.startsWith(SecurityConstants.JWT_TOKEN_PREFIX)) {
      Jws<Claims> jwsToken = SecurityUtil.decryptJwsToken(token);
      username = jwsToken.getSignature();
    } else {
      username = SecurityUtil.decryptBasicAuthToken(token).split(":")[0];
    }

    return username;
  }
}
