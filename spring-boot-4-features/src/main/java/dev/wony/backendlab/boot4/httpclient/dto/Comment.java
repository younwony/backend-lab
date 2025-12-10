package dev.wony.backendlab.boot4.httpclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JSONPlaceholder API의 Comment DTO
 *
 * @see <a href="https://jsonplaceholder.typicode.com/">JSONPlaceholder</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    private Long id;
    private Long postId;
    private String name;
    private String email;
    private String body;
}
