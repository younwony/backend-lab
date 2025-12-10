package dev.wony.backendlab.boot4.httpclient.service;

import dev.wony.backendlab.boot4.httpclient.dto.Post;
import dev.wony.backendlab.boot4.httpclient.dto.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

/**
 * JSONPlaceholder Users API를 위한 Declarative HTTP Interface
 *
 * <p>사용자 정보를 조회하는 API를 선언적으로 정의합니다.</p>
 *
 * @see PostService
 */
@HttpExchange("/users")
public interface UserService {

    /**
     * 모든 사용자 조회
     *
     * @return 사용자 목록
     */
    @GetExchange
    List<User> getAllUsers();

    /**
     * 특정 사용자 조회
     *
     * @param id 사용자 ID
     * @return 사용자 정보
     */
    @GetExchange("/{id}")
    User getUserById(@PathVariable Long id);

    /**
     * 특정 사용자의 게시물 조회
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 게시물 목록
     */
    @GetExchange("/{userId}/posts")
    List<Post> getPostsByUserId(@PathVariable Long userId);
}
