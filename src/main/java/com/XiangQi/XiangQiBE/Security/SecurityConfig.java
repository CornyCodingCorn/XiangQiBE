package com.XiangQi.XiangQiBE.Security;

import com.XiangQi.XiangQiBE.Security.Jwt.AuthEntryPointJwt;
import com.XiangQi.XiangQiBE.Security.Jwt.AuthTokenFilter;
import com.XiangQi.XiangQiBE.Services.PlayerDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Decoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  private PlayerDetailService playerDetailService;
  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;
  @Autowired
  private CorsConfigurationSource corsConfigurationSource;

  private final String salt;

  public SecurityConfig(@Value("${xiangqibe.app.salt}") String salt) {
    this.salt = salt;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    Decoder decoder = Base64.getDecoder();

    SecureRandom secureRandom = new SecureRandom(decoder.decode(salt));
    return new BCryptPasswordEncoder(10, secureRandom);
  }

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }
  
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.userDetailsService(playerDetailService).passwordEncoder(passwordEncoder());
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable().cors().configurationSource(corsConfigurationSource).and()
    .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
    .authorizeRequests().antMatchers("/api/auth/register", "/api/auth/login", "/api/auth/refresh").permitAll()
    .antMatchers("/api/test/**").permitAll()
    .anyRequest().authenticated();

  http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
  }
}
