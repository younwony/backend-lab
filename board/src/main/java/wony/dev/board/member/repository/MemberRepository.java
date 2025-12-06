package wony.dev.board.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wony.dev.board.member.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
