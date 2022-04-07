package com.XiangQi.XiangQiBE.Controllers;

import java.util.LinkedList;

import com.XiangQi.XiangQiBE.Models.Matches;
import com.XiangQi.XiangQiBE.Models.Player;
import com.XiangQi.XiangQiBE.Repositories.PlayerRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PlayerController {
  @Autowired
	PlayerRepo playerRepo;

  @PostMapping("/players/create")
  public Player CreatePlayer() {
    var matches = new LinkedList<Matches>();
    matches.add(new Matches("123", 12, 02, 2001));
    matches.add(new Matches("124", 13, 02, 2001));
    matches.add(new Matches("125", 15, 02, 2001));

    return playerRepo.save(new Player("Test2", "Password", matches));
  }

  @GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}
}
