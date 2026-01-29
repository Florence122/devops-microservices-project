package com.devops.userservice.controller;
import com.devops.userservice.entity.User;
import com.devops.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

 private final UserService userService;

 @PostMapping
 public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
 User createdUser = userService.createUser(user);
 return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
 }

 @GetMapping
 public ResponseEntity<List<User>> getAllUsers() {
 return ResponseEntity.ok(userService.getAllUsers());
 }

 @GetMapping("/{id}")

 public ResponseEntity<User> getUserById(@PathVariable Long id) {
 return ResponseEntity.ok(userService.getUserById(id));
 }

 @GetMapping("/username/{username}")
 public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
 return ResponseEntity.ok(userService.getUserByUsername(username));
 }

 @PutMapping("/{id}")
 public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
 return ResponseEntity.ok(userService.updateUser(id, user));
 }

 @DeleteMapping("/{id}")
 public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
 userService.deleteUser(id);
 Map<String, String> response = new HashMap<>();
 response.put("message", "User deleted successfully");
 return ResponseEntity.ok(response);
 }

 @GetMapping("/health")
 public ResponseEntity<Map<String, String>> health() {
 Map<String, String> health = new HashMap<>();
 health.put("status", "UP");
 health.put("service", "user-service");
 return ResponseEntity.ok(health);
 }
}