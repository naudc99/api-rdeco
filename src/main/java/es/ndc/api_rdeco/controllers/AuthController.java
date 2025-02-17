package es.ndc.api_rdeco.controllers;

import es.ndc.api_rdeco.entities.UserEntity;
import es.ndc.api_rdeco.models.JwtTokenDto;
import es.ndc.api_rdeco.models.LoginDto;
import es.ndc.api_rdeco.models.ResponseDto;
import es.ndc.api_rdeco.services.AuthService;
import es.ndc.api_rdeco.services.UserService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController{

    @Autowired
    AuthService authService;

    @Autowired
    UserService userService;

    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllUserNames() {
        return authService.getAllUserNames();
    }

    @PostMapping("/register")
    private ResponseEntity<ResponseDto> register(@RequestBody @Valid UserEntity userNew, BindingResult result) throws Exception {
        return authService.register(userNew, result);
    }

    @PostMapping("/login")
    private ResponseEntity<JwtTokenDto> login(@RequestBody LoginDto loginRequest) throws Exception {
        return authService.login(loginRequest);
    }
}
