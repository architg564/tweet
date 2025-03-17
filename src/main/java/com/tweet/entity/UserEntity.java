package com.tweet.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class UserEntity {
    @Id
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private long number;
    private String roles;
}
