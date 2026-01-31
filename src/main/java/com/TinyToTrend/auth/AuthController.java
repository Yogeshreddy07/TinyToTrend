package com.tinytotrend.auth;

import com.tinytotrend.user.User;
import com.tinytotrend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String email = request.get("email");
            String password = request.get("password");

            if (userRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email already registered"));
            }

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole("USER");

            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "User registered successfully",
                    "userId", user.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");

            Optional<User> userOpt = userRepository.findByEmail(email);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid email or password"));
            }

            User user = userOpt.get();

            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid email or password"));
            }

            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "userId", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "role", user.getRole()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }
}
