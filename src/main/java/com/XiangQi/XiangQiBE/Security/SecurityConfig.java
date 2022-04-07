package com.XiangQi.XiangQiBE.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  private PasswordEncoder passwordEncoder;

  @Autowired
  public SecurityConfig(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
    .antMatchers("/api/v1/")
    .permitAll()
    .anyRequest()
    .authenticated()
    .and()
    .formLogin()
    .usernameParameter("username")
    .passwordParameter("password");
  }

  @Override
  @Bean
  protected UserDetailsService userDetailsService() {
    var user = User.builder()
    .username("Tester1")
    .password(passwordEncoder.encode("Password"))
    .roles("PLAYER")
    .build();

    return new InMemoryUserDetailsManager(user);
  }
}
