package dev.wony.backendlab.board.member.repository;

import dev.wony.backendlab.board.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
