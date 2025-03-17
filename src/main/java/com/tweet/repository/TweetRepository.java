package com.tweet.repository;

import com.tweet.entity.TweetEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TweetRepository extends MongoRepository<TweetEntity, String>{
    List<TweetEntity> findByUsername(String username);
}
