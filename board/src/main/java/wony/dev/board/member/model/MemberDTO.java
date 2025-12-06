package wony.dev.board.member.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {

    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MemberDTO toDTO(Member member) {
        return MemberDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .id(id)
                .name(name)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
