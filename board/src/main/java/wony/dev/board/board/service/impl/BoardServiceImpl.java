package wony.dev.board.board.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wony.dev.board.board.model.Board;
import wony.dev.board.board.model.BoardDto;
import wony.dev.board.board.repository.BoardRepository;
import wony.dev.board.board.service.BoardService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private static final String BOARD_NOT_FOUND_MESSAGE = "게시글이 존재하지 않습니다.";

    private final BoardRepository boardRepository;

    @Override
    public BoardDto save(BoardDto boardDto) {
        return BoardDto.of(boardRepository.save(boardDto.toEntity()));
    }

    @Override
    @Transactional(readOnly = true)
    public BoardDto findById(Long id) {
        return BoardDto.of(boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(BOARD_NOT_FOUND_MESSAGE)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardDto> findAll() {
        return boardRepository.findAll().stream()
                .map(BoardDto::of)
                .collect(Collectors.toList());
    }

    @Override
    public void update(Long id, BoardDto boardDto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(BOARD_NOT_FOUND_MESSAGE));
        board.update(boardDto);
    }

    @Override
    public void deleteById(Long id) {
        boardRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        boardRepository.deleteAll();
    }
}
