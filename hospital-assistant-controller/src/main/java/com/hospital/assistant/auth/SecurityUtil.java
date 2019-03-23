package com.hospital.assistant.auth;

import static com.hospital.assistant.auth.SecurityConstants.BASIC_AUTH_TOKEN_PREFIX;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class SecurityUtil {
  public static Jws<Claims> decryptJwsToken(String token) {
    byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();

    Jws<Claims> parsedToken = Jwts.parser()
        .setSigningKey(signingKey)
        .parseClaimsJws(token.replace(SecurityConstants.JWT_TOKEN_PREFIX, ""));

    return parsedToken;
  }

  public static String decryptBasicAuthToken(String basicAuthToken) throws Base64DecodingException {
    return new String(Base64.decode(basicAuthToken.replace(BASIC_AUTH_TOKEN_PREFIX, "")));
  }
}
