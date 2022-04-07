package com.XiangQi.XiangQiBE.Models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Document("Players")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Player {
  @Id
  private String userName;
  private String password;
  private List<Matches> matches;
}
