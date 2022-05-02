package com.XiangQi.XiangQiBE.dto;

import javax.validation.constraints.NotBlank;
import com.XiangQi.XiangQiBE.Models.Player;
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

    public PlayerDto(Player player) {
        this.id = player.getId();
        this.username = player.getUsername();
        this.email = player.getEmail();
    }
}
