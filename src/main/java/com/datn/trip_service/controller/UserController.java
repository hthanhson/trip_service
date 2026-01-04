package com.datn.trip_service.controller;

import com.datn.trip_service.model.User;
import com.datn.trip_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<User>> getFollowers(@PathVariable String userId) {
        try {
            List<User> followers = userService.getFollowers(userId);
            return ResponseEntity.ok(followers);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
