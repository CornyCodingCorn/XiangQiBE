package com.XiangQi.XiangQiBE.Services;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.XiangQi.XiangQiBE.Components.Board;
import com.XiangQi.XiangQiBE.Components.Board.Result;
import com.XiangQi.XiangQiBE.Models.LobbiesMessage;
import com.XiangQi.XiangQiBE.Models.Lobby;
import com.XiangQi.XiangQiBE.Models.LobbyMessage;
import com.XiangQi.XiangQiBE.Models.Lobby.State;
import com.XiangQi.XiangQiBE.Models.LobbyMessage.EndType;
import com.XiangQi.XiangQiBE.Repositories.LobbyRepo;
import com.XiangQi.XiangQiBE.dto.LobbyDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LobbyService {
    public static final String LOBBY_WS_URL = "/lobbies";

    public class LobbyException extends Exception {
        public LobbyException(String lobbyID, String message) {
            super("Lobby " + lobbyID + ": " + message);
        }
    }

    private LobbyRepo lobbyRepo;
    private SimpMessagingTemplate simpMessagingTemplate;
    private Board board;
    private HashMap<String, Timer> schedules = new HashMap<>();

    public Lobby Create(String player) throws LobbyException {
        var joinedLobby = lobbyRepo.findByPlayer(player);
        if (joinedLobby.isPresent()) {
            Quit(player);
        }

        var lobby = new Lobby(player);
        lobbyRepo.save(lobby);
        sendLobbiesMessage(lobby, player, false);

        return lobby;
    }

    public List<Lobby> GetAll() {
        return lobbyRepo.findEmpty();
    }

    public Lobby Join(String lobbyID, String player) throws LobbyException {
        var lobby = lobbyRepo.findById(lobbyID)
                .orElseThrow(() -> new LobbyException(lobbyID, "Doesn't exist"));

        if (lobby.getPlayer1().equals(player)) {
            return lobby;
        }

        if (lobby.getPlayer2() != null) {
            if ((!lobby.getPlayer2().equals(player) && !lobby.getPlayer1().equals(player))) {
                throw new LobbyException(lobbyID, "The room is full");
            }
        } else {
            var joinedLobby = lobbyRepo.findByPlayer(player);
            if (joinedLobby.isPresent()) {
                try {
                    Quit(player);
                } catch (Exception e) {
                    /** Doesn't matter */
                }
            }

            lobby.Join(player);
        }

        // If 2 player slot is full then send the message to delete the lobby client list
        if (lobby.getPlayer1() != null && lobby.getPlayer2() != null) {
            sendLobbiesMessage(lobby, player, true);
        }

        lobbyRepo.save(lobby);
        sendMessageToLobby(lobbyID, LobbyMessage.Type.JOIN, player, lobby, "");
        return lobby;
    }

    public Lobby Move(String player, String move) throws LobbyException {
        var lobby = getPlayerLobby(player);

        // Check if moves valid
        if (lobby.getState() == Lobby.State.FINISHED) {
            throw new LobbyException(lobby.getId(), "The lobby has finished playing");
        }

        if (lobby.isBlack(player) && lobby.isRedTurn()) {
            throw new LobbyException(lobby.getId(), "This isn't black's turn");
        } else if (lobby.isRed(player) && !lobby.isRedTurn()) {
            throw new LobbyException(lobby.getId(), "This isn't red's turn");
        }

        // Update board component then check if the move is valid
        board.setBoard(lobby.getBoard());
        if (board.IsMoveValid(move)) {
            // Save the move along with the current board state separated by a space
            lobby.getMoves().add(move + " " + lobby.getBoard());

            // Reset the request.
            if (!lobby.isUndoRequestRejected()) {
                rejectUndo(lobby, player);
            }
            lobby.ResetUndoRequest();

            // Update the board then get the result
            String newBoard = board.UpdateBoard(move);
            var result = board.CheckResult(lobby.isRedTurn());

            // Update the data to db
            lobby.setRedTurn(!lobby.isRedTurn());
            lobby.setBoard(newBoard);
            if (result == Result.CONTINUE) {
                sendMessageToLobbyMove(lobby.getId(), player, move);
                lobbyRepo.save(lobby);
                return lobby;
            }

            String victor = "";
            String data = "";
            switch (result) {
                case RED_WIN:
                    // Send a win message with red player's name
                    victor = lobby.getRedPlayer();
                    data = LobbyMessage.EndType.constructData(EndType.WIN, move);
                    break;
                case BLACK_WIN:
                    // Same with red win
                    victor = lobby.getBlackPlayer();
                    data = LobbyMessage.EndType.constructData(EndType.WIN, move);
                    break;
                case DRAW:
                // Send a draw message with the name of who ever send the last move message
                    victor = "None";
                    data = LobbyMessage.EndType.constructData(EndType.DRAW, move);
                    break;
                default:
                    break;
            }

            finishMatch(lobby, victor, data);
        } else {
            throw new LobbyException(lobby.getId(), "Move " + move + " is not possible");
        }

        return lobby;
    }

    public Lobby Ready(String player) throws LobbyException {
        var lobby = getPlayerLobby(player);

        lobby.Ready(player);
        sendMessageToLobby(lobby.getId(), LobbyMessage.Type.CHANGE_READY, player, lobby, "");
        if (lobby.isPlayer1Ready() && lobby.isPlayer2Ready()) {
            lobby.Start();
            sendMessageToLobby(lobby.getId(), LobbyMessage.Type.START, player, lobby, "");
        }
        lobbyRepo.save(lobby);

        return lobby;
    }

    public void Quit(String player) throws LobbyException {
        var lobby = getPlayerLobby(player);
        lobby.Quit(player);

        // If lobby is remove then no need to post disconnect to player
        if (lobby.getPlayer1() == null && lobby.getPlayer2() == null) {
            sendLobbiesMessage(lobby, player, true);
            lobbyRepo.delete(lobby);
            return;
        }

        sendMessageToLobby(lobby.getId(), LobbyMessage.Type.DISCONNECT, player, lobby, "");
        if (lobby.getState() == Lobby.State.PLAYING) {
            // If playing then player 1 win
            finishMatch(lobby, lobby.getPlayer1(), LobbyMessage.EndType.WIN.toString());
        } else if (lobby.getState() == Lobby.State.WAITING) {
            // If a player quit and it doesn't get delete which mean the other player is still there
            // the lobby will reopen
            sendLobbiesMessage(lobby, player, false);
        }

        lobbyRepo.save(lobby);
    }

    public void Concede(String player) throws LobbyException {
        var lobby = getPlayerLobby(player);
        var otherPlayer =
                lobby.getPlayer1().equals(player) ? lobby.getPlayer2() : lobby.getPlayer1();
        sendMessageToLobby(lobby.getId(), LobbyMessage.Type.END, otherPlayer, lobby,
                LobbyMessage.EndType.constructData(EndType.WIN, ""));
    }

    public void AskForUndo(String player) throws LobbyException {
        var lobby = getPlayerLobby(player);
        // Check if it's player's turn
        if (lobby.isRed(player) ? !lobby.isRedTurn() : lobby.isRedTurn() || lobby.getMoves().size() < 2)
            return;

        // If the request have something append it then it has been rejected and should be ignore.
        if (lobby.getUndoRequest() !=  null) {
            var undoRequestInfo = lobby.getUndoRequest().split(" ");
            if (undoRequestInfo.length > 1) return;
        }

        // Send undo request message;
        sendMessageToLobby(lobby.getId(), LobbyMessage.Type.UNDO_REQUEST, player, lobby, "");
        // Schedule timeout for the request
        createRequestTimerForLobby(lobby.getId());

        lobby.RequestUndo(player);
        lobbyRepo.save(lobby);
    }

    public void ReplyToUndo(String player, LobbyMessage message) throws LobbyException {
        var lobby = getPlayerLobby(player);
        var undoRequest = lobby.getUndoRequest();
        if (undoRequest == null || lobby.getUndoRequest().equals(player))
            return;

        if (!message.getData().equals(LobbyMessage.UndoType.ACCEPTED.getValue())) {
            rejectUndo(lobby, player);
            return;
        }

        // Send accept message and undo message;
        sendMessageToLobby(lobby.getId(), LobbyMessage.Type.UNDO_REPLY, player, lobby,
                LobbyMessage.UndoType.ACCEPTED.getValue());

        String undoMoveStr = "";
        String newStr = "";
        int moveSize = lobby.getMoves().size();
        for (int i = 2; i > 0; i--) {
            newStr = lobby.getMoves().remove(--moveSize);
            // Swap old and new pos.
            undoMoveStr += newStr.substring(3, 5) + newStr.charAt(2) + newStr.substring(0, 2);
            undoMoveStr += (i > 1 ? " " : "");
        }
        
        sendMessageToLobby(lobby.getId(), LobbyMessage.Type.UNDO, player, lobby, undoMoveStr);
        // Restore the board state.
        lobby.setBoard(newStr.split(" ")[1]);
        lobby.ResetUndoRequest();
        lobbyRepo.save(lobby);
    }

    public Lobby getPlayerLobby(String player) throws LobbyException {
        var lobby = lobbyRepo.findByPlayer(player)
                .orElseThrow(() -> new LobbyException("unknown", "Player hasn't join any lobby"));
        return lobby;
    }

    private void sendLobbiesMessage(Lobby lobby, String player, boolean forRemoval) {
        LobbiesMessage message = new LobbiesMessage();
        message.setLobby(new LobbyDto(lobby));
        message.setPlayer(player);
        if (forRemoval)
            message.setType(LobbiesMessage.Type.REMOVE);
        else
            message.setType(LobbiesMessage.Type.CREATE);

        simpMessagingTemplate.convertAndSend(LOBBY_WS_URL, message);

    }

    private void sendMessageToLobby(String lobbyID, LobbyMessage.Type type, String player,
            Lobby lobby, String data) {
        LobbyMessage message = new LobbyMessage();
        message.setType(type);
        message.setPlayer(player);
        message.setLobby(new LobbyDto(lobby));
        message.setData(data);

        simpMessagingTemplate.convertAndSend(createLobbyEndpoint(lobbyID), message);
    }

    private void sendMessageToLobbyMove(String lobbyID, String player, String move) {
        LobbyMessage message = new LobbyMessage();
        message.setType(LobbyMessage.Type.MOVE);
        message.setData(move);
        message.setPlayer(player);

        simpMessagingTemplate.convertAndSend(createLobbyEndpoint(lobbyID), message);
    }

    private String createLobbyEndpoint(String lobbyID) {
        return LOBBY_WS_URL + "/" + lobbyID;
    }

    private void createRequestTimerForLobby(String lobbyID) {
        TimerTask task = new TimerTask() {
            public void run() {
                var lobby = lobbyRepo.findById(lobbyID).get();

                Timer timer = schedules.get(lobbyID);
                if (timer != null) {
                    timer.cancel();
                    schedules.remove(lobbyID);
                }

                if (lobby.isUndoRequestRejected())
                    return;
                rejectUndo(lobby, lobby.getPlayer1().matches(lobby.getUndoRequest()) ? lobby.getPlayer2() : lobby.getPlayer1());
            }
        };

        Timer timer = new Timer();
        Timer other = schedules.get(lobbyID);
        if (other != null) {
            other.cancel();
            schedules.remove(lobbyID);
        }

        schedules.put(lobbyID, timer);
        timer.schedule(task, 15000);
    }

    private void rejectUndo(Lobby lobby, String player) {
        sendMessageToLobby(lobby.getId(), LobbyMessage.Type.UNDO_REPLY, player,
                lobby, LobbyMessage.UndoType.REJECTED.getValue());
        lobby.RejectUndo();
        lobbyRepo.save(lobby);
    }

    private void finishMatch(Lobby lobby, String victor, String data) {
        lobby.setState(Lobby.State.FINISHED);
        lobbyRepo.save(lobby);
        sendMessageToLobby(lobby.getId(), LobbyMessage.Type.END, victor, lobby, data);
    }
}
