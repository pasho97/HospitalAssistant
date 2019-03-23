package com.hospital.assistant.api;

import com.hospital.assistant.account.repo.AccountFactory;
import com.hospital.assistant.account.repo.AccountRepository;
import com.hospital.assistant.api.dto.RegisterAccountDto;
import com.hospital.assistant.auth.SecurityConstants;
import com.hospital.assistant.auth.SecurityUtil;
import com.hospital.assistant.model.Account;
import com.hospital.assistant.model.FirebaseData;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping(value = "/account")
public class ServerController {
  private static final String SUCCESSFUL_REGISTRATION_TEMPLATE = "Successfully created account %s";
  private AccountFactory accountFactory;
  private AccountRepository accountRepository;

  @Autowired
  public ServerController(AccountFactory accountFactory,
                          AccountRepository accountRepository) {
    this.accountFactory = accountFactory;
    this.accountRepository = accountRepository;
  }

  @RequestMapping(path = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity registerAccount(@RequestBody RegisterAccountDto registerAccountDto) {
    log.debug("Attempting to create account {} with role {}",
              registerAccountDto.getUsername(),
              registerAccountDto.getRole());
    try {
      accountRepository.registerAccount(
          accountFactory.createInstance(registerAccountDto.getUsername(),
                                        registerAccountDto.getPassword(),
                                        registerAccountDto.getRole()));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return ResponseEntity.badRequest().body(e.getMessage());
    }

    log.info("Successfully created account {} with role {}",
             registerAccountDto.getUsername(),
             registerAccountDto.getRole());


    String msg = String.format(SUCCESSFUL_REGISTRATION_TEMPLATE, registerAccountDto.getUsername());
    return ResponseEntity.ok().body(msg);
  }

  @RequestMapping(path = "/updateToken", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity updateFirebaseToken(@RequestBody FirebaseData firebaseData,
                                            @RequestHeader HttpHeaders httpHeaders) {
    List<String> authorizationTokens = httpHeaders.get(HttpHeaders.AUTHORIZATION);
    //This should never happen as this method requires auth
    if (CollectionUtils.isEmpty(authorizationTokens)) {
      return ResponseEntity.unprocessableEntity().body("Bad Auth");
    }
    String token = authorizationTokens.get(0);
    String username;
    if (token.startsWith(SecurityConstants.JWT_TOKEN_PREFIX)) {
      Jws<Claims> jwsToken = SecurityUtil.decryptJwsToken(token);
      username = jwsToken.getSignature();
    } else {
      try {
        username = SecurityUtil.decryptBasicAuthToken(token).split(":")[0];
      } catch (Base64DecodingException e) {
        return ResponseEntity.unprocessableEntity().body("Cannot decrypt basic auth token");
      }
    }

    Optional<Account> optionalAccount = accountRepository.getAccounts().stream().filter(account -> account.getName()
        .equals(username)).findFirst();
    if (!optionalAccount.isPresent()) {
      return ResponseEntity.badRequest().body("No associated account found to set the token to");
    }

    log.info("Setting firebase data {} to user {}", firebaseData.toString(), username);
    optionalAccount.get().setFirebaseData(firebaseData);
    return ResponseEntity.ok("Successfully updated firebase token of " + username);
  }

}
