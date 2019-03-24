package com.hospital.assistant;

import com.hospital.assistant.account.repo.AccountFactory;
import com.hospital.assistant.account.repo.AccountRepoInMemory;
import com.hospital.assistant.account.repo.AccountRepository;
import com.hospital.assistant.auth.AuthProvider;
import com.hospital.assistant.auth.JwtAuthenticationFilter;
import com.hospital.assistant.auth.JwtAuthorizationFilter;
import com.hospital.assistant.model.Account;
import com.hospital.assistant.model.IntervalDto;
import com.hospital.assistant.model.Role;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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

  @Autowired private AuthProvider authProvider;

  @Bean
  @Autowired
  public AccountRepository accountRepository(AccountFactory accountFactory) {
    Account googleAssistantAcc = accountFactory.createInstance(googleAssistantUsername,
                                                               googleAssistantPassword,
                                                               googleAssistantRole);
    googleAssistantAcc.getInterval().add(new IntervalDto(LocalDateTime.ofInstant(Instant.now()
                                                                                     .minus(1, ChronoUnit.HOURS),
                                                                                 ZoneId.systemDefault()),
                                                         LocalDateTime.ofInstant(Instant.now()
                                                                                     .plus(7, ChronoUnit.HOURS),
                                                                                 ZoneId.systemDefault())));
    Account testAcc = accountFactory.createInstance(testAccountUsername, testAccountPassword, testAccountRole);
    return new AccountRepoInMemory(Arrays.asList(googleAssistantAcc, testAcc));
  }

  private static final String[] PUBLIC_APIS = new String[]{"/account", "/api", "/api/authentication"};

  @Autowired private AccountRepository accountRepository;
  @Autowired private AuthenticationEventPublisher authEventPublisher;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and()
        .csrf().disable()
        .authorizeRequests()
        .antMatchers(PUBLIC_APIS).permitAll()
        .anyRequest().authenticated()
        .and()
        .addFilter(new BasicAuthenticationFilter(authenticationManager()))
        .addFilter(new JwtAuthenticationFilter(authenticationManager()))
        .addFilter(new JwtAuthorizationFilter(authenticationManager()))
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authProvider);
    auth.authenticationEventPublisher(authEventPublisher);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());

    return source;
  }
}
