package wony.dev.board.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wony.dev.board.board.model.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
