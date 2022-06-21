package com.XiangQi.XiangQiBE.Services;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ValidationException;
import com.XiangQi.XiangQiBE.Components.Validator;
import com.XiangQi.XiangQiBE.Models.Player;
import com.XiangQi.XiangQiBE.Models.Request;
import com.XiangQi.XiangQiBE.Repositories.MatchesRepo;
import com.XiangQi.XiangQiBE.Repositories.PlayerRepo;
import com.XiangQi.XiangQiBE.Repositories.TokenRepo;

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
  private TokenRepo tokenRepo;
  private Validator validator;
  private MatchesRepo matchesRepo;

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

  public Player changePassword(String username, String password) throws UsernameNotFoundException {
    var player = get(username);
    var tokens = tokenRepo.getAllPlayerToken(username);
    for (var token : tokens) {
      tokenRepo.delete(token);
    }

    password = passwordEncoder.encode(password);
    player.setPassword(password);
    validator.validate(player);

    playerRepo.save(player);

    return player;
  }

  public Player get(String username) throws UsernameNotFoundException {
    var player = playerRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    player.setLostMatches(matchesRepo.countLostMatches(username));
    player.setWinMatches(matchesRepo.countWinMatches(username));
    player.setDrawMatches(matchesRepo.countDrawMatches(username));
    player.setRank(playerRepo.countHigherRank(player.getRankingPoint()) + 1);
    
    return player;
  }

  public Player changeProfile(String username, int index) throws UsernameNotFoundException {
    var player = get(username);

    player.setProfile(index);
    playerRepo.save(player);

    return player;
  }

  public void UpdateScore(String victor, String loser) {
    try {
      final int baseScore = 69;
      final int maxScore = 150;

      final var victorPlayer = get(victor);
      final var loserPlayer = get(loser);

      if (victorPlayer.getRankingPoint() == null) {
        victorPlayer.setRankingPoint(0);
      }
      if (loserPlayer.getRankingPoint() == null) {
        loserPlayer.setRankingPoint(0);
      }

      final int delta = victorPlayer.getRankingPoint() - loserPlayer.getRankingPoint();
      final int transferScore = baseScore - (int)Math.round(Math.pow(delta / baseScore, 3));
      
      victorPlayer.setRankingPoint(victorPlayer.getRankingPoint() + Math.max(0, Math.min(maxScore, transferScore)));
      loserPlayer.setRankingPoint(Math.max(0, loserPlayer.getRankingPoint() - Math.max(0, transferScore)));

      playerRepo.save(victorPlayer);
      playerRepo.save(loserPlayer);
    } catch (Exception e) {} 
  }

  public List<String> GetTopPlayer(int count) {
    var players = playerRepo.getTopPlayers(count);

    var result = new ArrayList<String>();
    for (var player : players) {
      result.add(player.getUsername() + " " + player.getRankingPoint());
    }

    return result;
  }
}
