package wony.dev.board.board.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import wony.dev.board.board.model.Board;
import wony.dev.board.board.model.BoardDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("BoardRepository 테스트")
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Test
    @DisplayName("게시글 저장 테스트")
    void save_Board() {
        // given
        Board board = BoardDto.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build()
                .toEntity();

        // when
        Board savedBoard = boardRepository.save(board);

        // then
        assertThat(savedBoard.getId()).isNotNull();
        assertThat(savedBoard.getTitle()).isEqualTo("테스트 제목");
        assertThat(savedBoard.getContent()).isEqualTo("테스트 내용");
    }

    @Test
    @DisplayName("게시글 조회 테스트")
    void findById_Board() {
        // given
        Board board = boardRepository.save(BoardDto.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build()
                .toEntity());

        // when
        Optional<Board> foundBoard = boardRepository.findById(board.getId());

        // then
        assertThat(foundBoard).isPresent();
        assertThat(foundBoard.get().getTitle()).isEqualTo("테스트 제목");
    }

    @Test
    @DisplayName("전체 게시글 조회 테스트")
    void findAll_Boards() {
        // given
        boardRepository.save(BoardDto.builder().title("제목1").content("내용1").build().toEntity());
        boardRepository.save(BoardDto.builder().title("제목2").content("내용2").build().toEntity());

        // when
        List<Board> boards = boardRepository.findAll();

        // then
        assertThat(boards).hasSize(2);
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void delete_Board() {
        // given
        Board board = boardRepository.save(BoardDto.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build()
                .toEntity());

        // when
        boardRepository.deleteById(board.getId());

        // then
        Optional<Board> deletedBoard = boardRepository.findById(board.getId());
        assertThat(deletedBoard).isEmpty();
    }
}
