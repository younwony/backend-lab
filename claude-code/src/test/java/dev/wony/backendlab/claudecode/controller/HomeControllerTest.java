package dev.wony.backendlab.claudecode.controller;

import dev.wony.backendlab.claudecode.service.SeasonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@Import(SeasonService.class)
@DisplayName("HomeController 테스트")
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("홈 페이지 요청 시 정상적으로 렌더링되어야 한다")
    void shouldRenderHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("currentTime"))
                .andExpect(model().attribute("message", "안녕하세요! Spring Boot 웹 애플리케이션입니다."));
    }

    @Test
    @DisplayName("홈 페이지의 현재 시간은 올바른 형식이어야 한다")
    void shouldDisplayCurrentTimeInCorrectFormat() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentTime",
                        matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
    }

    @Test
    @DisplayName("소개 페이지 요청 시 정상적으로 렌더링되어야 한다")
    void shouldRenderAboutPage() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("about"))
                .andExpect(model().attributeExists("title"))
                .andExpect(model().attributeExists("description"))
                .andExpect(model().attribute("title", "소개"))
                .andExpect(model().attribute("description",
                        "이것은 Spring Boot와 Thymeleaf를 사용한 간단한 웹 애플리케이션입니다."));
    }

    @Test
    @DisplayName("홈 페이지에 계절 정보가 포함되어야 한다")
    void shouldContainSeasonInfo() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("season"))
                .andExpect(model().attributeExists("seasonName"));
    }
}
