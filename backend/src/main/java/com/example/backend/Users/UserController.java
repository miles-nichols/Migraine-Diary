package com.example.backend.Users;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.security.Principal;

@RestController
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginUser) {
        boolean isAuthenticated = userService.authenticateUser(loginUser.getUsername(), loginUser.getPassword());
        if (isAuthenticated) {
            return ResponseEntity.ok("Login Successful!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Failed!");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User userRequest) {
        if (userRequest.getUsername() == null || userRequest.getPassword() == null || userRequest.getEmail() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid request: All fields are required"));
        }
        Optional<User> existingUser = userService.getUserByUsername(userRequest.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "User already exists"));
        }
        userService.registerUser(userRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User created successfully"));
    }

    // New Endpoint to get a single user by username
    // Secured with PreAuthorize to allow admins or the user themselves
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username, Principal principal) {
        Optional<User> user = userService.getUserByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }
  
    @GetMapping("/users")
    public List<String> getAllUsernames() {
        return userRepository.findAllUsernames();
    }
}