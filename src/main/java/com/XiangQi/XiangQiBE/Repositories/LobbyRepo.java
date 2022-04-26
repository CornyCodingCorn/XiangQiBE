package com.XiangQi.XiangQiBE.Repositories;

import java.util.Optional;
import com.XiangQi.XiangQiBE.Models.Lobby;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LobbyRepo extends MongoRepository<Lobby, String>{
    @Query("{ $limit: 1, $or: [ { player1: ?0 }, { player2: ?0 }] }")
    Optional<Lobby> findByPlayer(String player);
}
