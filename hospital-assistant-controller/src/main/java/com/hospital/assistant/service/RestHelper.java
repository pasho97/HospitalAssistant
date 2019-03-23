package com.hospital.assistant.service;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import java.nio.charset.Charset;
import java.util.Collections;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestHelper {
  public RestTemplate instance() {
    return new RestTemplate(Collections.singletonList(new MappingJackson2HttpMessageConverter()));
  }

  public HttpHeaders createBasicAuthHeaders(String username, String password) {
    return new HttpHeaders() {{
      String auth = username + ":" + password;
      byte[] encodedAuth = Base64.encodeBase64(
          auth.getBytes(Charset.forName("US-ASCII")));
      String authHeader = "Basic " + new String(encodedAuth);
      set("Authorization", authHeader);
    }};
  }
}
