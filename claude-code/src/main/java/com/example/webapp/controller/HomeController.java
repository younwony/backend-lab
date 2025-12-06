package com.example.webapp.controller;

import com.example.webapp.service.SeasonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class HomeController {

    private final SeasonService seasonService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // View names
    private static final String VIEW_INDEX = "index";
    private static final String VIEW_ABOUT = "about";

    // Model attribute names
    private static final String ATTR_MESSAGE = "message";
    private static final String ATTR_CURRENT_TIME = "currentTime";
    private static final String ATTR_TITLE = "title";
    private static final String ATTR_DESCRIPTION = "description";
    private static final String ATTR_SEASON = "season";
    private static final String ATTR_SEASON_NAME = "seasonName";

    // Message constants
    private static final String WELCOME_MESSAGE = "안녕하세요! Spring Boot 웹 애플리케이션입니다.";
    private static final String ABOUT_TITLE = "소개";
    private static final String ABOUT_DESCRIPTION = "이것은 Spring Boot와 Thymeleaf를 사용한 간단한 웹 애플리케이션입니다.";

    public HomeController(SeasonService seasonService) {
        this.seasonService = seasonService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute(ATTR_MESSAGE, WELCOME_MESSAGE);
        model.addAttribute(ATTR_CURRENT_TIME, getCurrentFormattedTime());
        addSeasonAttributes(model);
        return VIEW_INDEX;
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute(ATTR_TITLE, ABOUT_TITLE);
        model.addAttribute(ATTR_DESCRIPTION, ABOUT_DESCRIPTION);
        addSeasonAttributes(model);
        return VIEW_ABOUT;
    }

    private String getCurrentFormattedTime() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    /**
     * 모델에 계절 관련 속성 추가
     */
    private void addSeasonAttributes(Model model) {
        model.addAttribute(ATTR_SEASON, seasonService.getCurrentSeasonEnglishName());
        model.addAttribute(ATTR_SEASON_NAME, seasonService.getCurrentSeasonKoreanName());
    }
}
