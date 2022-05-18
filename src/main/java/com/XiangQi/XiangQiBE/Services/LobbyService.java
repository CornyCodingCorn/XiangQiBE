package com.XiangQi.XiangQiBE.Services;

import java.util.List;
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
            lobby.getMoves().add(move + " " + lobby.getBoard());

            // Update the board then get the result
            String newBoard= board.UpdateBoard(move);
            var result = board.CheckResult(lobby.isRedTurn());

            // Update the data to db
            lobby.setRedTurn(!lobby.isRedTurn());
            lobby.setBoard(newBoard);
            if (result != Result.CONTINUE) {
                lobby.setState(State.FINISHED);
            }

            switch (result) {
                case CONTINUE:
                sendMessageToLobbyMove(lobby.getId(), player, move);                
                break;
                case RED_WIN:
                // Send a win message with red player's name
                sendMessageToLobby(lobby.getId(), LobbyMessage.Type.END, lobby.getRedPlayer(), lobby,  LobbyMessage.EndType.constructData(EndType.WIN, move));
                break;
                case BLACK_WIN:
                // Same with red win
                sendMessageToLobby(lobby.getId(), LobbyMessage.Type.END, lobby.getBlackPlayer(), lobby, LobbyMessage.EndType.constructData(EndType.WIN, move));
                break;
                case DRAW:
                // Send a draw message with the name of who ever send the last move message
                sendMessageToLobby(lobby.getId(), LobbyMessage.Type.END, player, lobby, "DRAW " + move);
                break;
            }
        } else {
            throw new LobbyException(lobby.getId(), "Move " + move + " is not possible");
        }


        lobbyRepo.save(lobby);
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
            lobby.setState(Lobby.State.FINISHED);
            sendMessageToLobby(lobby.getId(), LobbyMessage.Type.END, lobby.getPlayer1(), lobby, LobbyMessage.EndType.WIN.getValue()); 
        } else if (lobby.getState() == Lobby.State.WAITING) {
            // If a player quit and it doesn't get delete which mean the other player is still there the lobby will reopen
            sendLobbiesMessage(lobby, player, false);
        }

        lobbyRepo.save(lobby);
    }

    public void Concede(String player) throws LobbyException {
        var lobby = getPlayerLobby(player);
        var otherPlayer = lobby.getPlayer1().equals(player) ?  lobby.getPlayer2() : lobby.getPlayer1();
        sendMessageToLobby(lobby.getId(), LobbyMessage.Type.END, otherPlayer, lobby, LobbyMessage.EndType.constructData(EndType.WIN, ""));
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

    private void sendMessageToLobby(String lobbyID, LobbyMessage.Type type, String player, Lobby lobby, String data) {
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
}
