package wony.dev.board.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
@DisplayName("LoginController 테스트")
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("로그인 페이지 요청 시 login을 반환한다")
    void login_ReturnsLoginPage() throws Exception {
        // given & when & then
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string("login"));
    }

    @Test
    @DisplayName("로그아웃 페이지 요청 시 logout을 반환한다")
    void logout_ReturnsLogoutPage() throws Exception {
        // given & when & then
        mockMvc.perform(get("/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("logout"));
    }
}
