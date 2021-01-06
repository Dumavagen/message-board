package com.example.messageboard.controller;

import com.example.messageboard.dto.AuthRequest;
import com.example.messageboard.dto.AuthResponse;
import com.example.messageboard.dto.RegistrationRequestDTO;
import com.example.messageboard.jwt.JwtProvider;
import com.example.messageboard.model.User;
import com.example.messageboard.service.ProfileService;
import com.example.messageboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final UserService userService;
    private final ProfileService profileService;
    private final JwtProvider jwtProvider;

    @Autowired
    public AuthController(UserService userService, ProfileService profileService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.profileService = profileService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequestDTO registrationRequestDTO) {
        User user = new User();
        user.setEmail(registrationRequestDTO.getEmail());
        user.setPassword(registrationRequestDTO.getPassword());
        user.setFirstName(registrationRequestDTO.getFirstName());
        user.setLastName(registrationRequestDTO.getLastName());
        user.setAge(registrationRequestDTO.getAge());
        userService.saveUser(user);
        profileService.saveProfile(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/auth")
    public AuthResponse auth(@RequestBody AuthRequest request) {
        User user = userService.findByEmailAndPassword(request.getEmail(), request.getPassword());
        String token = jwtProvider.generateToken(user.getEmail());
        return new AuthResponse(token);
    }
}

