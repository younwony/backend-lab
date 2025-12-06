package wony.dev.board.member.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import wony.dev.board.member.model.Member;
import wony.dev.board.member.model.MemberDTO;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("MemberRepository 테스트")
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 저장 테스트")
    void save_Member() {
        // given
        Member member = MemberDTO.builder()
                .name("테스트 회원")
                .build()
                .toEntity();

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember.getName()).isEqualTo("테스트 회원");
    }

    @Test
    @DisplayName("회원 조회 테스트")
    void findById_Member() {
        // given
        Member member = memberRepository.save(MemberDTO.builder()
                .name("테스트 회원")
                .build()
                .toEntity());

        // when
        Optional<Member> foundMember = memberRepository.findById(member.getId());

        // then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("테스트 회원");
    }

    @Test
    @DisplayName("전체 회원 조회 테스트")
    void findAll_Members() {
        // given
        memberRepository.save(MemberDTO.builder().name("회원1").build().toEntity());
        memberRepository.save(MemberDTO.builder().name("회원2").build().toEntity());

        // when
        List<Member> members = memberRepository.findAll();

        // then
        assertThat(members).hasSize(2);
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    void delete_Member() {
        // given
        Member member = memberRepository.save(MemberDTO.builder()
                .name("테스트 회원")
                .build()
                .toEntity());

        // when
        memberRepository.deleteById(member.getId());

        // then
        Optional<Member> deletedMember = memberRepository.findById(member.getId());
        assertThat(deletedMember).isEmpty();
    }
}
