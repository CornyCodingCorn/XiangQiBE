package com.XiangQi.XiangQiBE.Repositories;

import org.springframework.stereotype.Repository;
import com.XiangQi.XiangQiBE.Models.Token;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

@Repository
public interface TokenRepo extends MongoRepository<Token, String> {
    @Query("{username: ?0}")
    List<Token> getAllPlayerToken(String username);
}
