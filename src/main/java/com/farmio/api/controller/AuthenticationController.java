package com.farmio.api.controller;


import com.farmio.api.dto.DataAuth;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager manager;

    @PostMapping
    public ResponseEntity goLogin(@RequestBody @Valid DataAuth dataAuth) {
        var token = new UsernamePasswordAuthenticationToken(dataAuth.login(), dataAuth.pass());
        var authon = manager.authenticate(token);

        return ResponseEntity.ok().build();
    }
}
