package wony.dev.board.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wony.dev.board.member.model.MemberDTO;
import wony.dev.board.member.service.MemberService;

@RequestMapping("/members")
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberDTO> saveMember(MemberDTO memberDTO) {
        return ResponseEntity.ok(memberService.save(memberDTO));
    }

    @GetMapping
    public ResponseEntity<?> members() {
        return ResponseEntity.ok(memberService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMember(@PathVariable("id") Long id) {
        return ResponseEntity.ok(memberService.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") Long id, MemberDTO memberDTO) {
        memberService.update(id, memberDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        memberService.delete(id);
        return ResponseEntity.ok().build();
    }
}
