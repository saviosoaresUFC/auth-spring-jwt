package com.example.authapi.controllers;

import com.example.authapi.domain.user.AuthenticationDTO;
import com.example.authapi.domain.user.LoginResponseDTO;
import com.example.authapi.domain.user.RegisterDto;
import com.example.authapi.domain.user.User;
import com.example.authapi.infra.security.TokenService;
import com.example.authapi.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody @Valid RegisterDto registerDto) {
//        Check if the user already exists
        if (userRepository.findByEmail(registerDto.email()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
//        Encrypt the password
        String encryptedPassword = new BCryptPasswordEncoder().encode(registerDto.password());
        User newUser = new User(registerDto.name(), registerDto.email(), encryptedPassword, registerDto.role());

//        Save the user
        return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(newUser));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO authenticationDTO) {
//        Authenticate the user
        var usernamePasswordToken = new UsernamePasswordAuthenticationToken(authenticationDTO.email(), authenticationDTO.password());
        var auth = authenticationManager.authenticate(usernamePasswordToken);
        User userAuthenticated = (User) auth.getPrincipal();
//        Generate the token of the user
        var tokenUser = tokenService.generateToken(userAuthenticated);
//        Return the token
        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDTO(userAuthenticated.getName(), tokenUser));
    }
}
