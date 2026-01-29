package com.devops.userservice.service;
import com.devops.userservice.entity.User;
import com.devops.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

 private final UserRepository userRepository;

 @Transactional
 public User createUser(User user) {
 if (userRepository.existsByUsername(user.getUsername())) {
 throw new RuntimeException("Username already exists");
 }
 if (userRepository.existsByEmail(user.getEmail())) {
 throw new RuntimeException("Email already exists");
 }
 log.info("Creating user: {}", user.getUsername());
 return userRepository.save(user);
 }

 public User getUserById(Long id) {
 return userRepository.findById(id)
 .orElseThrow(() -> new RuntimeException("User not found: " + id));

 }

 public User getUserByUsername(String username) {
 return userRepository.findByUsername(username)
 .orElseThrow(() -> new RuntimeException("User not found: " + username));
 }

 public List<User> getAllUsers() {
 return userRepository.findAll();
 }

 @Transactional
 public User updateUser(Long id, User userDetails) {
 User user = getUserById(id);
 user.setFirstName(userDetails.getFirstName());
 user.setLastName(userDetails.getLastName());
 user.setEmail(userDetails.getEmail());
 return userRepository.save(user);
 }

 @Transactional
 public void deleteUser(Long id) {
 if (!userRepository.existsById(id)) {
 throw new RuntimeException("User not found: " + id);
 }
 userRepository.deleteById(id);
 }
}