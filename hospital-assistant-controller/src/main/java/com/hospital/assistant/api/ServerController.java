package com.hospital.assistant.api;

import com.hospital.assistant.account.repo.AccountFactory;
import com.hospital.assistant.account.repo.AccountRepository;
import com.hospital.assistant.api.dto.RegisterAccountDto;
import com.hospital.assistant.model.Account;
import com.hospital.assistant.model.FirebaseData;
import com.hospital.assistant.model.Location;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
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

    String username = ServerUtils.extractUsernameFromHeaders(httpHeaders);
    if (username == null) {
      return ResponseEntity.unprocessableEntity().body("Error! Cannot resolve user");
    }
    Optional<Account> optionalAccount = accountRepository.getAccounts().stream()
        .filter(account -> account.getName().equals(username))
        .findFirst();
    if (!optionalAccount.isPresent()) {
      return ResponseEntity.badRequest().body("No associated account found to set the token to");
    }
    log.info("Setting firebase data {} to user {}", firebaseData.toString(), username);
    optionalAccount.get().setFirebaseData(firebaseData);
    return ResponseEntity.ok("Successfully updated firebase token of " + username);
  }

  @PostMapping(path = "/updateLocation", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity updateLocation(@RequestBody Location location, @RequestHeader HttpHeaders httpHeaders) {
    String username = ServerUtils.extractUsernameFromHeaders(httpHeaders);
    if (username == null) {
      return ResponseEntity.unprocessableEntity().body("Error! Cannot resolve user");
    }
    Optional<Account> optionalAccount = accountRepository.getAccounts().stream()
        .filter(account -> account.getName().equals(username))
        .findFirst();
    if (!optionalAccount.isPresent()) {
      return ResponseEntity.badRequest().body("No associated account found to update location to");
    }
    Account account = optionalAccount.get();
    log.info("Updating location of {} {}", account.getRole(), account.getName());
    account.setLocation(location);
    return ResponseEntity.ok("Successfully updated location token of " + username);
  }

  @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity login() {
    return ResponseEntity.ok("Success");
  }
}
