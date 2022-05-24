package com.XiangQi.XiangQiBE.dto;

import com.XiangQi.XiangQiBE.Models.Lobby;
import com.XiangQi.XiangQiBE.Models.LobbySetting;

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

    private boolean player2Ready;
    private boolean player1Ready;

    private String blackPlayer;
    private String redPlayer;

    private String board;

    private LobbySetting setting;

    public LobbyDto(Lobby lobby) {
        id = lobby.getId();
        
        player1 = lobby.getPlayer1();
        player2 = lobby.getPlayer2();

        blackPlayer = lobby.getBlackPlayer();
        redPlayer = lobby.getRedPlayer();

        player1Ready = lobby.isPlayer1Ready();
        player2Ready = lobby.isPlayer2Ready();

        board = lobby.getBoard();

        setting = lobby.getSetting();
    }
}
