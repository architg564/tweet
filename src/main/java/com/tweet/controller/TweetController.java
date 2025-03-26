package com.tweet.controller;

import com.tweet.entity.Comments;
import com.tweet.entity.TweetEntity;
import com.tweet.entity.TweetUpdate;
import com.tweet.repository.TweetRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@Log4j2
public class TweetController {

    @Autowired
    private TweetRepository tweetRepository;

    @PostMapping("{username}/add")
    public ResponseEntity<?> saveTweet(@PathVariable String username, @RequestBody TweetEntity tweetEntity){
        tweetEntity.setTweetId(UUID.randomUUID().toString().replace("-",""));
        return new ResponseEntity<>(tweetRepository.save(tweetEntity), HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<?> getAllTweet(){
        return new ResponseEntity<>(tweetRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("{username}")
    public ResponseEntity<?> getTweetByUsername(@PathVariable String username){
        log.info(username);
        return new ResponseEntity<>(tweetRepository.findByUsername(username), HttpStatus.OK);
    }

    @PutMapping("{username}/update/{id}")
    public ResponseEntity<?> updateTweet(@PathVariable("username") String username, @PathVariable("id") String id, @RequestBody TweetUpdate tweetUpdate){
        Optional<TweetEntity> tweetEntity = tweetRepository.findById(id);
        if(tweetEntity.isEmpty())return new ResponseEntity<>("Tweet not found",HttpStatus.NOT_FOUND);
        TweetEntity tweetModel1 = tweetEntity.get();
        tweetModel1.setMessage(tweetUpdate.getMessage());
        return new ResponseEntity<>(tweetRepository.save(tweetModel1),HttpStatus.ACCEPTED);
    }


    @DeleteMapping("{username}/delete/{id}")
    public ResponseEntity<?> deleteTweet(@PathVariable("username") String username,@PathVariable("id") String id){
        tweetRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Tweet Deleted Successfully");
        return ResponseEntity.ok(response);
    }


    @PostMapping("{username}/like/{id}")
    public ResponseEntity<?> likeTweet(@PathVariable("username") String username,@PathVariable("id") String id){
        Optional<TweetEntity> tweetEntity= tweetRepository.findById(id);
        if (tweetEntity.isEmpty()) return new ResponseEntity<>("Tweet Not Found",HttpStatus.NOT_FOUND);
        TweetEntity tweetModel=tweetEntity.get();
        if (tweetModel.getLikes().contains(username)) {
            tweetModel.getLikes().remove(username); // Unlike
        } else {
            tweetModel.getLikes().add(username); // Like
        }
        return new ResponseEntity<>(tweetRepository.save(tweetModel),HttpStatus.OK);
    }

    @GetMapping("{username}/liked-tweets")
    public ResponseEntity<?> getLikedTweets(@PathVariable("username") String username) {
        List<String> likedTweetIds = tweetRepository.findAll().stream()
                .filter(tweet -> tweet.getLikes().contains(username))
                .map(TweetEntity::getTweetId)
                .collect(Collectors.toList());

        return new ResponseEntity<>(likedTweetIds, HttpStatus.OK);
    }


    @PostMapping("{username}/reply/{id}")
    public ResponseEntity<?> replyTweet(@PathVariable("username") String username,@PathVariable("id") String id,@RequestBody Comments comments){
        Optional<TweetEntity> tweetEntity= tweetRepository.findById(id);
        if (tweetEntity.isEmpty()) return new ResponseEntity<>("Tweet Not Found",HttpStatus.NOT_FOUND);
        TweetEntity tweetModel1=tweetEntity.get();
        tweetModel1.getComments().add(comments);
        return new ResponseEntity<>(tweetRepository.save(tweetModel1),HttpStatus.OK);
    }
}