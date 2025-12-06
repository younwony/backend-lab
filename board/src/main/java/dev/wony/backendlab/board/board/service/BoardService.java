package dev.wony.backendlab.board.board.service;

import dev.wony.backendlab.board.board.model.BoardDto;

import java.util.List;

public interface BoardService {

    BoardDto save(BoardDto boardDto);

    BoardDto findById(Long id);

    List<BoardDto> findAll();

    void update(Long id, BoardDto boardDto);

    void deleteById(Long id);

    void deleteAll();
}
