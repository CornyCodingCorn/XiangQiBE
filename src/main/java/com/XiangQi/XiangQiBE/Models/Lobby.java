package com.XiangQi.XiangQiBE.Models;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import com.mongodb.lang.Nullable;
import org.hibernate.validator.internal.util.stereotypes.Immutable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@CompoundIndex(unique = true, def = "{'player1: 1', 'player2': 1}")
public class Lobby {
    private static final String BOARD = ""
    + "000000000"
    + "000000000"
    + "000000000"
    + "000000000"
    + "000000000"
    + "000000000"
    + "000000000"
    + "000000000"
    + "000000000"
    + "000000000";

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

    public Lobby(String player1) {
        this.player1 = player1;
        this.redPlayer = player1;

        moves = new ArrayList<String>();
    }

    public void Start() {
        state = State.PLAYING;
        board = BOARD;
    }

    public void Finish() {
        state = State.FINISHED;
    }

    public void PlayAgain() {
        var oPlayer = this.redPlayer;
        this.redPlayer = this.blackPlayer;
        this.blackPlayer = oPlayer;

        this.board = BOARD;
        moves.clear();
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
        }

        if (player1.equals(player)) {
            player1 = player2;
            player1Ready = player2Ready;

            player2 = null;
            player2Ready = false;

            if (state == State.WAITING) {
                // If the lobby is in waiting state then player 2 will become red player
                redPlayer = player2;
                blackPlayer = null;
            }
        }
    }
}
