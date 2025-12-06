package dev.wony.backendlab.board.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private static final String LOGIN_PAGE = "login";
    private static final String LOGOUT_PAGE = "logout";

    @GetMapping("/login")
    public String login() {
        return LOGIN_PAGE;
    }

    @GetMapping("/logout")
    public String logout() {
        return LOGOUT_PAGE;
    }
}
