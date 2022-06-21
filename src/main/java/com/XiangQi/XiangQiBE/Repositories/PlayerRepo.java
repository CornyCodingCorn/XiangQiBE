package com.XiangQi.XiangQiBE.Repositories;

import java.util.List;
import java.util.Optional;
import com.XiangQi.XiangQiBE.Models.Player;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepo extends MongoRepository<Player, String> {
    Optional<Player> findByUsername(String username);
    Optional<Player> findByEmail(String email);

    @Aggregation(pipeline = {
        "{$sort: {rankingPoint: -1}}",
        "{$limit: ?0}",
    })
    List<Player> getTopPlayers(int count);

    @Query(value = "{rankingPoint: {$gt: ?0}}", count = true)
    int countHigherRank(int rp);
}
