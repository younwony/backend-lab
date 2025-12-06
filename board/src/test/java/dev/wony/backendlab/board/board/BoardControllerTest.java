package dev.wony.backendlab.board.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.wony.backendlab.board.board.model.BoardDto;
import dev.wony.backendlab.board.board.service.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardController.class)
@DisplayName("BoardController 테스트")
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private BoardService boardService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("게시글 저장 요청 시 저장된 게시글을 반환한다")
    void saveBoard_ReturnsSavedBoard() throws Exception {
        // given
        BoardDto requestDto = BoardDto.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();
        BoardDto savedDto = BoardDto.builder()
                .id(1L)
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        when(boardService.save(any(BoardDto.class))).thenReturn(savedDto);
        when(boardService.findById(anyLong())).thenReturn(savedDto);

        // when & then
        mockMvc.perform(post("/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("테스트 제목"));
    }

    @Test
    @DisplayName("전체 게시글 조회 요청 시 게시글 목록을 반환한다")
    void boards_ReturnsBoardList() throws Exception {
        // given
        List<BoardDto> boards = List.of(
                BoardDto.builder().id(1L).title("제목1").content("내용1").build(),
                BoardDto.builder().id(2L).title("제목2").content("내용2").build()
        );
        when(boardService.findAll()).thenReturn(boards);

        // when & then
        mockMvc.perform(get("/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("게시글 단건 조회 요청 시 해당 게시글을 반환한다")
    void board_ReturnsSingleBoard() throws Exception {
        // given
        BoardDto boardDto = BoardDto.builder()
                .id(1L)
                .title("테스트 제목")
                .content("테스트 내용")
                .build();
        when(boardService.findById(1L)).thenReturn(boardDto);

        // when & then
        mockMvc.perform(get("/boards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("테스트 제목"));
    }

    @Test
    @DisplayName("게시글 수정 요청 시 수정된 게시글을 반환한다")
    void update_ReturnsUpdatedBoard() throws Exception {
        // given
        BoardDto requestDto = BoardDto.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();
        BoardDto updatedDto = BoardDto.builder()
                .id(1L)
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        doNothing().when(boardService).update(anyLong(), any(BoardDto.class));
        when(boardService.findById(1L)).thenReturn(updatedDto);

        // when & then
        mockMvc.perform(put("/boards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"));
    }

    @Test
    @DisplayName("게시글 삭제 요청 시 성공 응답을 반환한다")
    void delete_ReturnsSuccess() throws Exception {
        // given
        doNothing().when(boardService).deleteById(1L);

        // when & then
        mockMvc.perform(delete("/boards/1"))
                .andExpect(status().isOk());
    }
}
