package com.tweet.controller;

import com.tweet.entity.NewPassword;
import com.tweet.entity.UserEntity;
import com.tweet.repository.UserRepository;
import com.tweet.security.JwtResponse;
import com.tweet.security.JwtUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@Log4j2
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody UserEntity userEntity) {
        log.info("registering user");
        try {
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            userEntity.setRoles("ROLE_USER");
            return new ResponseEntity<>(userRepository.save(userEntity), HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("user not registered");
            return new ResponseEntity<>("error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody UserEntity userEntity) {
        log.info("login user");
        log.info(userEntity);
        String username = userEntity.getUsername();
        String password = userEntity.getPassword();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            log.debug("successfully logged in with userName: {}", username);
        } catch (Exception e) {
            log.info("user not logged in");
            return new ResponseEntity<>("Bad Credentials " + username, HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(userEntity.getUsername());
        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setToken(jwtUtil.generateToken(userDetails));
        jwtResponse.setRole(userRepository.findById(userEntity.getUsername()).get().getRoles());
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    @PutMapping("{username}/forgot")
    public ResponseEntity<?> changePassword(@PathVariable("username") String username, @RequestBody NewPassword newPassword) {
        log.info("changing user password");
        try {
            UserEntity userEntity = userRepository.findById(username).orElse(null);
            userEntity.setPassword(newPassword.getPassword());
            userRepository.save(userEntity);
        }catch(Exception e){
            log.info("password not changed");
            return new ResponseEntity<>("username not found",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(userRepository.findById(username), HttpStatus.OK);
    }

    @GetMapping("user/all")
    public ResponseEntity<List<UserEntity>> listUser() {
        log.info("finding all user");
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("user/search/{username}")
    public ResponseEntity<Optional<UserEntity>> listAUser(@PathVariable("username") String username) {
        log.info("finding user by username");
        return new ResponseEntity<>(userRepository.findById(username), HttpStatus.OK);
    }

}
