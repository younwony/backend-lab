package dev.wony.backendlab.board.member.service;

import dev.wony.backendlab.board.member.model.Member;
import dev.wony.backendlab.board.member.model.MemberDTO;

import java.util.List;

public interface MemberService {

    List<Member> findAll();

    Member findById(Long id);

    MemberDTO save(MemberDTO memberDTO);

    void update(Long id, MemberDTO memberDTO);

    void delete(Long id);

    void deleteAll();
}
