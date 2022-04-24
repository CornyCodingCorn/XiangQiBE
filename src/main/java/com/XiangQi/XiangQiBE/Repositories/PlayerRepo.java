package com.XiangQi.XiangQiBE.Repositories;

import java.util.Optional;
import com.XiangQi.XiangQiBE.Models.Player;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepo extends MongoRepository<Player, String> {
    Optional<Player> findByUsername(String username);
    Optional<Player> findByEmail(String email);


}
