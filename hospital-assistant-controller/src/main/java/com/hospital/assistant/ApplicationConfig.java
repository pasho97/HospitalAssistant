package com.hospital.assistant;

import com.hospital.assistant.account.repo.AccountFactory;
import com.hospital.assistant.account.repo.AccountRepoInMemory;
import com.hospital.assistant.account.repo.AccountRepository;
import com.hospital.assistant.model.Account;
import com.hospital.assistant.model.Role;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class ApplicationConfig extends WebSecurityConfigurerAdapter {
  @Value("${google.assistant.account.username:google}")
  private String googleAssistantUsername;
  @Value("${google.assistant.account.defaultPassword:assistant}")
  private String googleAssistantPassword;
  @Value("${google.assistant.account.defaultRole:ADMIN}")
  private Role googleAssistantRole;

  @Value("${test.account.username:admin}")
  private String testAccountUsername;
  @Value("${test.account.defaultPassword:test}")
  private String testAccountPassword;
  @Value("${test.account.defaultRole:ADMIN}")
  private Role testAccountRole;


  @Bean
  @Autowired
  public AccountRepository accountRepository(AccountFactory accountFactory) {
    Account googleAssistantAcc = accountFactory.createInstance(googleAssistantUsername,
                                                               googleAssistantPassword,
                                                               googleAssistantRole);
    Account testAcc = accountFactory.createInstance(testAccountUsername, testAccountPassword, testAccountRole);
    return new AccountRepoInMemory(Arrays.asList(googleAssistantAcc, testAcc));
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .authorizeRequests().anyRequest().authenticated()
        .and()
        .httpBasic();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth, AccountRepository accountRepository,
                              AuthenticationEventPublisher authEventPublisher)
      throws Exception {
    InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> authenticationManagerBuilderInMemoryUserDetailsManagerConfigurer = auth
        .inMemoryAuthentication();

    for (Account account : accountRepository.getAccounts()) {
      authenticationManagerBuilderInMemoryUserDetailsManagerConfigurer = authenticationManagerBuilderInMemoryUserDetailsManagerConfigurer
          .withUser(account.getName())
          .password(account.getPasswordHash())
          .roles(account.getRole().name())
          .and();
    }
    authenticationManagerBuilderInMemoryUserDetailsManagerConfigurer.passwordEncoder(new BCryptPasswordEncoder());

    auth.authenticationEventPublisher(authEventPublisher);
  }
}
