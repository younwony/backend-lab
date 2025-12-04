package dev.wony.backendlab.patterns.ddd.product.domain.repository;

import dev.wony.backendlab.patterns.ddd.product.domain.entity.Product;
import dev.wony.backendlab.patterns.ddd.product.domain.entity.ProductStatus;
import dev.wony.backendlab.patterns.ddd.product.domain.vo.CategoryId;
import dev.wony.backendlab.patterns.ddd.product.domain.vo.ProductId;

import java.util.List;
import java.util.Optional;

/**
 * 상품 Repository 인터페이스.
 * <p>
 * DDD에서 Repository는 Aggregate의 영속성을 담당합니다.
 * 도메인 계층에서는 인터페이스만 정의하고, 구현은 인프라 계층에서 합니다.
 */
public interface ProductRepository {

    /**
     * 상품 저장
     *
     * @param product 저장할 상품
     * @return 저장된 상품
     */
    Product save(Product product);

    /**
     * ID로 상품 조회
     *
     * @param id 상품 ID
     * @return 상품 Optional
     */
    Optional<Product> findById(ProductId id);

    /**
     * ID로 상품 조회 (없으면 예외)
     *
     * @param id 상품 ID
     * @return 상품
     * @throws ProductNotFoundException 상품이 존재하지 않는 경우
     */
    default Product getById(ProductId id) {
        return findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    /**
     * 카테고리별 상품 목록 조회
     *
     * @param categoryId 카테고리 ID
     * @return 상품 목록
     */
    List<Product> findByCategoryId(CategoryId categoryId);

    /**
     * 상태별 상품 목록 조회
     *
     * @param status 상품 상태
     * @return 상품 목록
     */
    List<Product> findByStatus(ProductStatus status);

    /**
     * 판매중인 상품 목록 조회
     *
     * @return 판매중 상품 목록
     */
    default List<Product> findOnSaleProducts() {
        return findByStatus(ProductStatus.ON_SALE);
    }

    /**
     * 모든 상품 조회
     *
     * @return 전체 상품 목록
     */
    List<Product> findAll();

    /**
     * 상품 삭제
     *
     * @param id 삭제할 상품 ID
     */
    void deleteById(ProductId id);

    /**
     * 상품 존재 여부 확인
     *
     * @param id 상품 ID
     * @return 존재하면 true
     */
    boolean existsById(ProductId id);

    /**
     * 상품 개수 조회
     *
     * @return 전체 상품 수
     */
    long count();

    /**
     * 상품 미존재 예외
     */
    class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(ProductId id) {
            super(String.format("상품을 찾을 수 없습니다: %s", id));
        }
    }
}
