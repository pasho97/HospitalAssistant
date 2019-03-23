package com.hospital.assistant.account.repo;

import com.hospital.assistant.model.Account;
import com.hospital.assistant.model.Role;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Primary
@Component
public class AccountFactoryImpl implements AccountFactory {
  private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

  @Override
  public Account createInstance(String username, String password, Role role) {
    return Account.builder()
        .name(username)
        .id(UUID.randomUUID().toString())
        .role(role)
        .passwordHash(PASSWORD_ENCODER.encode(password))
        .build();
  }
}
