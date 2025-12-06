package dev.wony.backendlab.board.index;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IndexController.class)
@DisplayName("IndexController 테스트")
class IndexControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("인덱스 페이지 요청 시 환영 메시지를 반환한다")
    void index_ReturnsWelcomeMessage() throws Exception {
        // given & when & then
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hi, Wony World"));
    }
}
