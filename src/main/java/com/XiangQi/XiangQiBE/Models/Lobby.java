package com.XiangQi.XiangQiBE.Models;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;

import com.XiangQi.XiangQiBE.utils.JsonUtils;
import com.mongodb.lang.Nullable;
import org.hibernate.validator.internal.util.stereotypes.Immutable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@CompoundIndex(unique = true, def = "{'player1: 1', 'player2': 1}")
public class Lobby {
    private static final String BOARD =
    "rheakaehr" +
    "000000000" +
    "0c00000c0" +
    "p0p0p0p0p" +
    "000000000" +
    "000000000" +
    "P0P0P0P0P" +
    "0C00000C0" +
    "000000000" +
    "RHEAKAEHR";
    // "rheak0C00" +
    // "0000a00C0" +
    // "0c00000c0" +
    // "p0p0p0p0p" +
    // "000000000" +
    // "000000000" +
    // "P0P0P0P0P" +
    // "000000000" +
    // "000000000" +
    // "RHEAKAEHR";

    @AllArgsConstructor
    public enum State {
        WAITING(1),
        PLAYING(2),
        FINISHED(3);

        @Getter
        private int value;
    }

    @Id
    @Immutable
    private String id;

    @Setter
    @NotBlank
    private String player1;

    @Setter
    @Nullable
    private String player2;

    @Setter
    private boolean player1Ready;
    @Setter
    private boolean player2Ready;

    // Set by server
    @Nullable
    @Setter
    private String blackPlayer;
    @Nullable
    @Setter
    private String redPlayer;

    @NotBlank
    @Setter
    private String board = BOARD;
    
    @NotBlank
    @Setter
    // Move will be moveStr + board separated by a space;
    private List<String> moves;

    @Setter
    @NotBlank
    private boolean isRedTurn = true;

    @NotBlank
    @Setter
    private State state = State.WAITING;

    // @Setter
    // @NotBlank
    // private boolean isPrivate = false;

    @Setter
    private boolean player1PlayAgain = false;
    @Setter
    private boolean player2PlayAgain = false;

    @Setter
    @NotBlank
    private LobbySetting setting;
    // If != null then the string value represents the player that made the request
    // If split by space and have more than 1 element then the request have been rejected before.
    private String undoRequest = null;

    public Lobby(String player1) {
        this.player1 = player1;
        this.redPlayer = player1;

        moves = new ArrayList<String>();

        setting = new LobbySetting(2,10,false,false);
    }

    public void Start() {
        state = State.PLAYING;
        board = BOARD;
    }

    public void Finish() {
        state = State.FINISHED;
    }

    public void PlayAgain() {
        var oPlayer = redPlayer;
        redPlayer = blackPlayer;
        blackPlayer = oPlayer;

        isRedTurn = true;
        player1PlayAgain = false;
        player2PlayAgain = false;

        board = BOARD;
        moves.clear();
        state = State.PLAYING;
    }

    public void Join(String player2) {
        this.player2 = player2;
        if (this.redPlayer == null) {
            this.redPlayer = player2;
        } else {
            this.blackPlayer = player2;
        }
    }

    public void Ready(String player) {
        if (player1.equals(player)) {
            player1Ready = !player1Ready;
        } 
        else if (player2 != null && player2.equals(player)) {
            player2Ready = !player2Ready;
        }
    }

    public boolean isRed(String player) {
        return redPlayer.equals(player);
    }
    public boolean isBlack(String player) {
        return blackPlayer.equals(player);
    }

    public void Quit(String player) {
        if (player2 != null && player2.equals(player)) {
            player2 = null;
            player2Ready = false;
            player2PlayAgain = false;
        }

        if (player1.equals(player)) {
            player1 = player2;
            player1Ready = player2Ready;
            player1PlayAgain = player2PlayAgain;

            player2 = null;
            player2Ready = false;
            player2PlayAgain = false;

            if (state == State.WAITING) {
                // If the lobby is in waiting state then player 2 will become red player
                redPlayer = player2;
                blackPlayer = null;
            }
        }
    }

    public void RequestUndo(String player) {
        if (player.equals(player1) || player.equals(player2)) {
            undoRequest = player;
        }
    }
    public void RejectUndo() {
        if (undoRequest == null) {return;}
        undoRequest += " rejected";
    }
    public String getUndoRequest() {
        return undoRequest == null ? null : undoRequest.split(" ")[0];
    }
    public boolean isUndoRequestRejected() {
        return undoRequest == null || undoRequest.split(" ").length > 1;
    }
    public void ResetUndoRequest() {
        undoRequest = null;
    }

    public void ChangeSetting(String settingStr) throws Exception {
        try {
            var set = JsonUtils.getJsonAsMap(settingStr);
            setting.setMinPerTurn(Integer.parseInt(set.get("minPerTurn")));
            setting.setTotalMin(Integer.parseInt(set.get("totalMin")));
            setting.setVsBot(Boolean.parseBoolean(set.get("isVsBot")));
            setting.setPrivate(Boolean.parseBoolean(set.get("isPrivate")));
        } catch (Exception e) {
            //TO DO Exception
            throw new Exception("Something wrong with setting", e);
        }
    }
}
