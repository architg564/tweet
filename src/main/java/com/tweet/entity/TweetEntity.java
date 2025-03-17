package com.tweet.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class TweetEntity {

    @Id
    private String tweetId;
    private String username;
    private String message;
    private String time;
    private List<String> likes = new ArrayList<>();
    private List<Comments> comments = new ArrayList<>();
}
