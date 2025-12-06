package wony.dev.board.board;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wony.dev.board.board.model.BoardDto;
import wony.dev.board.board.service.BoardService;

@RequestMapping("/boards")
@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardDto> saveBoard(@RequestBody BoardDto boardDto) {
        BoardDto registerBoard = boardService.save(boardDto);
        return ResponseEntity.ok(boardService.findById(registerBoard.getId()));
    }

    @GetMapping
    public ResponseEntity<?> boards() {
        return ResponseEntity.ok(boardService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDto> board(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardDto> update(@PathVariable Long id, @RequestBody BoardDto boardDto) {
        boardService.update(id, boardDto);
        return ResponseEntity.ok(boardService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boardService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
