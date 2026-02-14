package com.utkarsh.journalApp.controller;

import com.utkarsh.journalApp.api.response.WeatherResponse;
import com.utkarsh.journalApp.entity.User;
import com.utkarsh.journalApp.repository.UserRepository;
import com.utkarsh.journalApp.service.UserService;
import com.utkarsh.journalApp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private com.utkarsh.journalApp.service.EmailService emailService;

    // @GetMapping
    // public List<User> getAllUsers() {
    // return userService.getAll();
    // }

    @PostMapping
    public void createUser(@RequestBody User user) {
        userService.saveNewUser(user);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User userInDb = userService.findByUsername(userName);
        if (userInDb != null) {
            userInDb.setUsername(user.getUsername());
            userInDb.setPassword(user.getPassword());
            userService.saveNewUser(userInDb);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUserById() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userRepository.deleteByUsername(authentication.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<?> greeting(@RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        // Default to Bengaluru if no coordinates provided
        double latitude = (lat != null) ? lat : 12.9716;
        double longitude = (lon != null) ? lon : 77.5946;
        String locationDisplay = (lat != null && lon != null) ? "Current Location" : "Bengaluru";

        WeatherResponse weatherResponse = weatherService.getWeather("Bengaluru", latitude, longitude);

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("username", username);
        if (user != null) {
            response.put("email", user.getEmail());
            response.put("sentimentAnalysis", user.isSentimentAnalysis());
            response.put("roles", user.getRoles());
        }

        if (weatherResponse != null) {
            response.put("weather", weatherResponse.getCurrent());
            response.put("location", locationDisplay);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/test-email")
    public ResponseEntity<?> testEmail() {
        try {
            emailService.sendMail("utkarshdubey0242@gmail.com", "Test Email from Journal App",
                    "Hello! This is a test email to verify your SMTP configuration.");
            return new ResponseEntity<>("Email sent successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to send email: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}