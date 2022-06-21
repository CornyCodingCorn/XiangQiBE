package com.XiangQi.XiangQiBE.dto;

import javax.validation.constraints.NotBlank;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;

import com.XiangQi.XiangQiBE.Models.Player;
import com.XiangQi.XiangQiBE.Repositories.PlayerRepo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlayerDto {
    @NotBlank
    private String id;
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    private Integer profile;
    @NotBlank
    private float winLostRatio;
    @NotBlank
    private int totalMatches;
    @NotBlank
    private int rank;
    @NotBlank
    private int rankingPoint;

    public PlayerDto(Player player) {
        this.id = player.getId();
        this.username = player.getUsername();
        this.email = player.getEmail();
        this.profile = player.getProfile();
        this.rankingPoint = player.getRankingPoint();

        totalMatches = player.getWinMatches() + player.getLostMatches() + player.getDrawMatches();
        winLostRatio = totalMatches == 0  ? player.getWinMatches() : player.getWinMatches() / (float)player.getLostMatches();

        //Calculate rank
        rank = player.getRank();
    }
}
