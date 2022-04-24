package com.XiangQi.XiangQiBE.Services;

import java.util.Date;
import javax.validation.Valid;
import javax.validation.ValidationException;
import com.XiangQi.XiangQiBE.Components.Validator;
import com.XiangQi.XiangQiBE.Models.Player;
import com.XiangQi.XiangQiBE.Repositories.PlayerRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Service
@AllArgsConstructor
public class PlayerService {
  @Getter
  public class EmailExistsException extends Exception {
    private String email;
    EmailExistsException(String email) { super("Email " + email + " is already in use"); this.email = email; }
    
  }
  @Getter
  public class UsernameExistsException extends Exception {
    private String username;
    UsernameExistsException(String username) { super("User name " + username + " is already exists"); this.username = username; }
  }
  @Getter
  public class UsernameNotFoundException extends Exception {
    private String username;
    UsernameNotFoundException(String username) { super("User name " + username + " doesn't exist"); this.username = username; }
  }

  private PasswordEncoder passwordEncoder;
  private PlayerRepo playerRepo;
  private Validator validator;

  public Player create(String username, String password, String email) throws EmailExistsException, UsernameExistsException, ValidationException {
    if (playerRepo.findByUsername(username).isPresent()) {
      throw new UsernameExistsException(username);
    }

    if (playerRepo.findByEmail(email).isPresent()) {
      throw new EmailExistsException(email);
    }

    password = passwordEncoder.encode(password);
    Player player = new Player(username, password, email);
    validator.validate(player);

    playerRepo.save(player);

    return player;
  }
}
