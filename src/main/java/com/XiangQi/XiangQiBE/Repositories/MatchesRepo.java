package com.XiangQi.XiangQiBE.Repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.XiangQi.XiangQiBE.Models.Match;

@Repository
public interface MatchesRepo extends MongoRepository<Match, String> {
    @Query("{ $or: [ { redPlayer: ?0 }, { blackPlayer: ?0 }] }")
    List<Match> findByPlayer(String player);

    @Query(value = "{ $and: [{$or: [ { redPlayer: ?0 }, { blackPlayer: ?0 }]}, { victor: ?0}] }", count = true)
    int countWinMatches(String player);
    @Query(value = "{ $and: [{$or: [ { redPlayer: ?0 }, { blackPlayer: ?0 }]}, { victor: {$ne: ?0}}] }", count = true)
    int countLostMatches(String player);
    @Query(value = "{ $and: [{$or: [ { redPlayer: ?0 }, { blackPlayer: ?0 }]}, {victor: \"None\"}] }", count = true)
    int countDrawMatches(String player);
}
