package dev.wony.backendlab.board.member.service.impl;

import dev.wony.backendlab.board.member.model.Member;
import dev.wony.backendlab.board.member.model.MemberDTO;
import dev.wony.backendlab.board.member.repository.MemberRepository;
import dev.wony.backendlab.board.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private static final String MEMBER_NOT_FOUND_MESSAGE = "해당 회원이 없습니다.";

    private final MemberRepository memberRepository;

    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Override
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND_MESSAGE));
    }

    @Override
    public MemberDTO save(MemberDTO memberDTO) {
        Member saveMember = memberRepository.save(memberDTO.toEntity());
        return MemberDTO.toDTO(saveMember);
    }

    @Override
    public void update(Long id, MemberDTO memberDTO) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND_MESSAGE));
        member.update(memberDTO);
        memberRepository.save(member);
    }

    @Override
    public void delete(Long id) {
        memberRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        memberRepository.deleteAll();
    }
}
