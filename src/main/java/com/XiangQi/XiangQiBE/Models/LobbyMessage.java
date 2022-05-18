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
        START(5),
        // If a player concede they will send end message
        END(6);

        @Getter
        private int value;
    }

    @AllArgsConstructor
    public enum EndType {
        WIN("WIN"),
        DRAW("DRAW");

        @Getter
        private String value;
        public static String constructData(EndType type, String moveStr) {
            return type + " " + moveStr;
        }
    }

    private String player;
    private LobbyDto lobby;
    /**
     * Could be anything depending on the message type
     * `${WIN or DRAW} ${lastMoveStr or "" depending on if it happen by player quitting or by move}` if END
     * moveStr if MOVE
     * noting if CHANGE_READY, DISCONNECT, JOIN, START
     */
    private String data;
    private Type type;
}
