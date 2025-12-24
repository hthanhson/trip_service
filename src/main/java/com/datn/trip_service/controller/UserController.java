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
