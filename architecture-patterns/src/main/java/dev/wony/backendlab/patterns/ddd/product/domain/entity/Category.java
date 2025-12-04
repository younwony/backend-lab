package dev.wony.backendlab.patterns.ddd.product.domain.entity;

import dev.wony.backendlab.patterns.ddd.product.domain.vo.CategoryId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 카테고리 Entity.
 * <p>
 * 상품이 속하는 카테고리를 나타냅니다.
 * 별도의 Aggregate Root로 관리됩니다.
 */
public class Category {

    private final CategoryId id;
    private String name;
    private String description;
    private CategoryId parentId; // 상위 카테고리 (null이면 최상위)
    private int displayOrder;
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Category(CategoryId id, String name, String description,
                     CategoryId parentId, int displayOrder) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.parentId = parentId;
        this.displayOrder = displayOrder;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * 새 카테고리 생성 (팩토리 메서드)
     *
     * @param name         카테고리명
     * @param description  설명
     * @param parentId     상위 카테고리 ID (null 가능)
     * @param displayOrder 표시 순서
     * @return 새로 생성된 Category
     */
    public static Category create(String name, String description,
                                   CategoryId parentId, int displayOrder) {
        validateName(name);
        return new Category(
                CategoryId.generate(),
                name.trim(),
                description,
                parentId,
                displayOrder
        );
    }

    /**
     * 최상위 카테고리 생성
     *
     * @param name         카테고리명
     * @param description  설명
     * @param displayOrder 표시 순서
     * @return 새로 생성된 최상위 Category
     */
    public static Category createRoot(String name, String description, int displayOrder) {
        return create(name, description, null, displayOrder);
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("카테고리명은 필수입니다");
        }
        if (name.trim().length() > 50) {
            throw new IllegalArgumentException("카테고리명은 50자를 초과할 수 없습니다");
        }
    }

    /**
     * 카테고리 정보 수정
     *
     * @param name        새 카테고리명
     * @param description 새 설명
     */
    public void updateInfo(String name, String description) {
        validateName(name);
        this.name = name.trim();
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 표시 순서 변경
     *
     * @param displayOrder 새 표시 순서
     */
    public void changeDisplayOrder(int displayOrder) {
        if (displayOrder < 0) {
            throw new IllegalArgumentException("표시 순서는 0 이상이어야 합니다");
        }
        this.displayOrder = displayOrder;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 상위 카테고리 변경
     *
     * @param parentId 새 상위 카테고리 ID
     */
    public void changeParent(CategoryId parentId) {
        // 자기 자신을 부모로 설정 불가
        if (parentId != null && parentId.equals(this.id)) {
            throw new IllegalArgumentException("자기 자신을 상위 카테고리로 설정할 수 없습니다");
        }
        this.parentId = parentId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 카테고리 활성화
     */
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 카테고리 비활성화
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 최상위 카테고리인지 확인
     *
     * @return 최상위이면 true
     */
    public boolean isRoot() {
        return parentId == null;
    }

    // Getters

    public CategoryId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public CategoryId getParentId() {
        return parentId;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Category{id=%s, name='%s', active=%s}", id, name, active);
    }
}
