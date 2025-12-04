package dev.wony.backendlab.patterns.ddd.product.domain.repository;

import dev.wony.backendlab.patterns.ddd.product.domain.entity.Category;
import dev.wony.backendlab.patterns.ddd.product.domain.vo.CategoryId;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 Repository 인터페이스.
 */
public interface CategoryRepository {

    /**
     * 카테고리 저장
     *
     * @param category 저장할 카테고리
     * @return 저장된 카테고리
     */
    Category save(Category category);

    /**
     * ID로 카테고리 조회
     *
     * @param id 카테고리 ID
     * @return 카테고리 Optional
     */
    Optional<Category> findById(CategoryId id);

    /**
     * ID로 카테고리 조회 (없으면 예외)
     *
     * @param id 카테고리 ID
     * @return 카테고리
     * @throws CategoryNotFoundException 카테고리가 존재하지 않는 경우
     */
    default Category getById(CategoryId id) {
        return findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    /**
     * 최상위 카테고리 목록 조회
     *
     * @return 최상위 카테고리 목록
     */
    List<Category> findRootCategories();

    /**
     * 하위 카테고리 목록 조회
     *
     * @param parentId 상위 카테고리 ID
     * @return 하위 카테고리 목록
     */
    List<Category> findByParentId(CategoryId parentId);

    /**
     * 활성 카테고리 목록 조회
     *
     * @return 활성 카테고리 목록
     */
    List<Category> findActiveCategories();

    /**
     * 모든 카테고리 조회
     *
     * @return 전체 카테고리 목록
     */
    List<Category> findAll();

    /**
     * 카테고리 삭제
     *
     * @param id 삭제할 카테고리 ID
     */
    void deleteById(CategoryId id);

    /**
     * 카테고리 존재 여부 확인
     *
     * @param id 카테고리 ID
     * @return 존재하면 true
     */
    boolean existsById(CategoryId id);

    /**
     * 카테고리 미존재 예외
     */
    class CategoryNotFoundException extends RuntimeException {
        public CategoryNotFoundException(CategoryId id) {
            super(String.format("카테고리를 찾을 수 없습니다: %s", id));
        }
    }
}
