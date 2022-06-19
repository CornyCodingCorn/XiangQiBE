package com.XiangQi.XiangQiBE.Models;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import com.mongodb.lang.NonNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Document("Players")
@Getter
@RequiredArgsConstructor
public class Player {
  @Id
  private String id;

  @Indexed(unique = true)
  @NotBlank
  @lombok.NonNull
  @Size(max=20)
  private String username;
  
  @Setter
  @NonNull
  private Integer profile = 0;

  @Setter
  @NonNull
  private Integer lostMatches = 0;

  @Setter
  @NonNull
  private Integer winMatches = 0;

  @Setter
  @NonNull
  private Integer drawMatches = 0;

  @Setter
  @NotBlank
  @Size(max=120)
  @lombok.NonNull
  private String password;

  @Setter
  @Indexed(unique = true)
  @NotBlank
  @Email
  @lombok.NonNull
  @Size(max=50)
  private String email;

  @Setter
  @PositiveOrZero
  private int Score = 0;

  @NonNull
  private boolean validated = false;

  public void validate() {
    validated = true;
  }
}
