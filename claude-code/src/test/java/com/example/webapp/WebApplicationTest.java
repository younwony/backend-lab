package com.example.webapp;

import com.example.webapp.controller.HomeController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("WebApplication 통합 테스트")
class WebApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HomeController homeController;

    @Test
    @DisplayName("Spring 애플리케이션 컨텍스트가 정상적으로 로드되어야 한다")
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    @DisplayName("HomeController 빈이 정상적으로 등록되어야 한다")
    void homeControllerShouldBeLoaded() {
        assertThat(homeController).isNotNull();
    }

    @Test
    @DisplayName("애플리케이션 컨텍스트에 필수 빈들이 존재해야 한다")
    void shouldContainRequiredBeans() {
        assertThat(applicationContext.containsBean("homeController")).isTrue();
    }
}
