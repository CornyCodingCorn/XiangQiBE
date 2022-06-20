package com.XiangQi.XiangQiBE.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.XiangQi.XiangQiBE.Models.Request;

@Repository
public interface RequestRepo extends MongoRepository<Request, String> {
    
}
