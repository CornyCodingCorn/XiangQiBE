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
        END(6),

        // Can't be sent by player, only sent by server
        UNDO(7),
        // When sent by player it meant that they will ask for undo and send over to other player
        UNDO_REQUEST(8),
        // When other player sends it with data = "accepted" then that means they accept the undo request. If there is no undo then ignore.
        UNDO_REPLY(9);

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

    @AllArgsConstructor
    public enum UndoType {
        REJECTED("REJECTED"),
        ACCEPTED("ACCEPTED");

        @Getter
        private String value;
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
