package com.XiangQi.XiangQiBE.dto;

import com.XiangQi.XiangQiBE.Models.Lobby;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LobbyDto {
    private String id;

    private String player1;
    private String player2;

    private String blackPlayer;
    private String redPlayer;

    public LobbyDto(Lobby lobby) {
        id = lobby.getId();

        player1 = lobby.getPlayer1();
        player2 = lobby.getPlayer2();

        blackPlayer = lobby.getBlackPlayer();
        redPlayer = lobby.getRedPlayer();
    }
}
