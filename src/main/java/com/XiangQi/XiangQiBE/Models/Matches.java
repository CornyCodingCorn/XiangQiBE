package com.XiangQi.XiangQiBE.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document()
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Matches {
  @Id
  private String id;
  private Integer day;
  private Integer month;
  private Integer year;
}
