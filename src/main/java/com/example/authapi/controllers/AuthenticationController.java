package com.example.authapi.controllers;

import com.example.authapi.domain.user.*;
import com.example.authapi.infra.security.TokenService;
import com.example.authapi.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
//        Encrypt the password
        String encryptedPassword = new BCryptPasswordEncoder().encode(registerDto.password());
        User newUser = new User(registerDto.name(), registerDto.email(), encryptedPassword, registerDto.role());

//        Save the user
        return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(newUser));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO authenticationDTO) {
//        Check if the account is locked
        if (tokenService.isAccountLocked(authenticationDTO.email())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        //        Authenticate the user
        try {
            var usernamePasswordToken = new UsernamePasswordAuthenticationToken(authenticationDTO.email(), authenticationDTO.password());
            var auth = authenticationManager.authenticate(usernamePasswordToken);
            User userAuthenticated = (User) auth.getPrincipal();

//            Reset the login attempts
            tokenService.loginAttempts.remove(authenticationDTO.email());

//        Generate the token of the user
            var tokenUser = tokenService.generateToken(userAuthenticated);
            var refreshToken = tokenService.generateRefreshToken(userAuthenticated);

//        Save the refresh token in the user
            userAuthenticated.setRefreshToken(refreshToken);
            userRepository.save(userAuthenticated);
//        Return the token
            UserRole role = userAuthenticated.getRole();
            return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDTO(userAuthenticated.getName(), tokenUser, role, refreshToken));
        } catch (BadCredentialsException ex) {
            tokenService.registerFailedAttempt(authenticationDTO.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (LockedException ex) {
            return ResponseEntity.status(HttpStatus.LOCKED).build();
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<LoginResponseDTO> refreshToken(@RequestBody @Valid RefreshTokenRequestDTO refreshTokenRequestDTO) {
        var refreshToken = refreshTokenRequestDTO.refreshToken();

        var userOptional = userRepository.findByRefreshToken(refreshToken);
        if (userOptional.isEmpty() || !tokenService.getNameFromToken(refreshToken).equals(userOptional.get().getEmail())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userOptional.get();
        var newAccessToken = tokenService.generateToken(user);
        var newRefreshToken = tokenService.generateRefreshToken(user);

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDTO(user.getName(), newAccessToken, user.getRole(), newRefreshToken));
    }
}
