package com.XiangQi.XiangQiBE.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.XiangQi.XiangQiBE.Models.Match;

public interface MatchesRepo extends MongoRepository<Match, String> {
    
}
