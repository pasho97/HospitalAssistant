package com.hospital.assistant.auth;

import com.hospital.assistant.account.repo.AccountRepository;
import com.hospital.assistant.model.Account;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthProvider implements AuthenticationProvider {
  private PasswordEncoder passwordEncoder;
  private AccountRepository accountRepository;

  @Autowired
  public AuthProvider(PasswordEncoder passwordEncoder,
                      AccountRepository accountRepository) {
    this.passwordEncoder = passwordEncoder;
    this.accountRepository = accountRepository;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Optional<Account> optionalAccount = accountRepository.getAccounts().stream().filter(account -> account.getName()
        .equals(authentication.getName())).findFirst();

    if (!optionalAccount.isPresent()) {
      return null;
    }
    Account account = optionalAccount.get();
    String credentials = String.valueOf(authentication.getCredentials());
    if (!passwordEncoder.matches(credentials, account.getPasswordHash())) {

      return null;
    }
    Authentication auth = new
        UsernamePasswordAuthenticationToken(authentication.getName(),
                                            authentication.getCredentials(),
                                            authentication.getAuthorities());

    return auth;
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
  }
}
