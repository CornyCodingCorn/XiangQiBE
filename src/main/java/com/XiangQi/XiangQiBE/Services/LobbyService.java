package com.XiangQi.XiangQiBE.Services;

import java.util.List;
import com.XiangQi.XiangQiBE.Models.LobbiesMessage;
import com.XiangQi.XiangQiBE.Models.Lobby;
import com.XiangQi.XiangQiBE.Models.LobbyMessage;
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

    public Lobby Create(String player) throws LobbyException {
        var joinedLobby = lobbyRepo.findByPlayer(player);
        if (joinedLobby.isPresent()) {
            Quit(player);
        }

        var lobby = new Lobby(player);
        lobbyRepo.save(lobby);
        sendLobbiesMessage(lobby, false);

        return lobby;
    }

    public List<Lobby> GetAll() {
        return lobbyRepo.findAll();
    }

    public Lobby Join(String lobbyID, String player) throws LobbyException {
        var lobby = lobbyRepo.findById(lobbyID)
                .orElseThrow(() -> new LobbyException(lobbyID, "Doesn't exist"));
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

        lobbyRepo.save(lobby);
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

        // If valid
        sendMessageToLobbyMove(lobby.getId(), player, move);
        lobbyRepo.save(lobby);
        return lobby;
    }

    public Lobby Ready(String player) throws LobbyException {
        var lobby = getPlayerLobby(player);

        sendMessageToLobby(lobby.getId(), LobbyMessage.Type.CHANGE_READY, player);

        lobby.Ready(player);
        lobbyRepo.save(lobby);
        if (lobby.isPlayer1Ready() && lobby.isPlayer2Ready()) {
            lobby.Start();
            sendMessageToLobby(lobby.getId(), LobbyMessage.Type.START, player);
        }

        return lobby;
    }

    public void Quit(String player) throws LobbyException {
        var lobby = getPlayerLobby(player);

        sendMessageToLobby(lobby.getId(), LobbyMessage.Type.DISCONNECT, player);
        lobby.Quit(player);

        if (lobby.getPlayer1() == null && lobby.getPlayer2() == null) {
            sendLobbiesMessage(lobby, true);
            lobbyRepo.delete(lobby);
            return;
        }

        lobbyRepo.save(lobby);
    }

    public Lobby getPlayerLobby(String player) throws LobbyException {
        var lobby = lobbyRepo.findByPlayer(player)
                .orElseThrow(() -> new LobbyException("unknown", "Player hasn't join any lobby"));
        return lobby;
    }

    private void sendLobbiesMessage(Lobby lobby, boolean forRemoval) {
        LobbiesMessage message = new LobbiesMessage();
        message.setLobby(new LobbyDto(lobby));
        if (forRemoval)
            message.setType(LobbiesMessage.Type.REMOVE);
        else
            message.setType(LobbiesMessage.Type.CREATE);

        simpMessagingTemplate.convertAndSend(LOBBY_WS_URL, message);

    }

    private void sendMessageToLobby(String lobbyID, LobbyMessage.Type type, String player) {
        LobbyMessage message = new LobbyMessage();
        message.setType(type);
        message.setPlayer(player);

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
        return LOBBY_WS_URL + lobbyID;
    }
}
