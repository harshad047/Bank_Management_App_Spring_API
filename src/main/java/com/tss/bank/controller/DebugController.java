package com.tss.bank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/debug")
public class DebugController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> getAuthInfo(Authentication authentication) {
        Map<String, Object> authInfo = new HashMap<>();
        
        if (authentication != null) {
            authInfo.put("authenticated", authentication.isAuthenticated());
            authInfo.put("username", authentication.getName());
            authInfo.put("authorities", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            authInfo.put("principal", authentication.getPrincipal().toString());
        } else {
            authInfo.put("authenticated", false);
            authInfo.put("message", "No authentication found");
        }
        
        return ResponseEntity.ok(authInfo);
    }
}
