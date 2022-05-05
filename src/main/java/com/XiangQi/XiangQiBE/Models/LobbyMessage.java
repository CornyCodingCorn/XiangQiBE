package com.XiangQi.XiangQiBE.Models;

import com.XiangQi.XiangQiBE.dto.LobbyDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LobbyMessage {
    @AllArgsConstructor
    public enum Type {
        JOIN(1),
        DISCONNECT(2),
        CHANGE_READY(3),
        MOVE(4),
        START(5);

        @Getter
        private int value;
    }

    private String player;
    private LobbyDto lobby;
    private String data;
    private Type type;
}
