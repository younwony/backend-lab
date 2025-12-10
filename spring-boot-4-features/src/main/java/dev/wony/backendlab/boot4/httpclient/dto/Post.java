package dev.wony.backendlab.boot4.httpclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JSONPlaceholder API의 Post DTO
 *
 * @see <a href="https://jsonplaceholder.typicode.com/">JSONPlaceholder</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    private Long id;
    private Long userId;
    private String title;
    private String body;
}
