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
    private Float winLostRatio;
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

        final int win = player.getWinMatches();
        final int lost = player.getLostMatches();
        totalMatches = win + lost + player.getDrawMatches();
        if (win + lost == 0) {
            winLostRatio = null;
        } else if (lost == 0) {
            winLostRatio = Float.POSITIVE_INFINITY;
        } else {
            winLostRatio = win / (float)lost;
        }

        //Calculate rank
        rank = player.getRank();
    }
}
