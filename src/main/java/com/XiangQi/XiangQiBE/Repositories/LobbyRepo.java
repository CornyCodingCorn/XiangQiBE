package com.XiangQi.XiangQiBE.Repositories;

import java.util.List;
import java.util.Optional;
import com.XiangQi.XiangQiBE.Models.Lobby;
import com.XiangQi.XiangQiBE.Models.Lobby.State;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LobbyRepo extends MongoRepository<Lobby, String>{
    @Query("{ $or: [ { player1: ?0 }, { player2: ?0 }] }")
    Optional<Lobby> findByPlayer(String player);

    List<Lobby> findByPlayer1(String player1);
    List<Lobby> findByPlayer2(String player1);

    @Query("{ state: ?0 }")
    List<Lobby> findByState(State state);

    @Query("{$and: [{player2: {$exists: false}}, {state: \"WAITING\"}, {\"setting.isPrivateLobby\": false}]}")
    List<Lobby> findEmpty();
}
