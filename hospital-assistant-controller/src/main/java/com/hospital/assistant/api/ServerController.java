package com.hospital.assistant.api;

import com.hospital.assistant.account.repo.AccountFactory;
import com.hospital.assistant.account.repo.AccountRepository;
import com.hospital.assistant.api.dto.RegisterAccountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
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
}
