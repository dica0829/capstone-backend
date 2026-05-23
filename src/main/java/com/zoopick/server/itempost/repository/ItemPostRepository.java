package com.zoopick.server.itempost.repository;

import com.zoopick.server.exception.DataNotFoundException;
import com.zoopick.server.item.entity.*;
import com.zoopick.server.itempost.dto.ItemPostFilter;
import com.zoopick.server.itempost.entity.ItemPost;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemPostRepository extends JpaRepository<ItemPost, Long>, JpaSpecificationExecutor<ItemPost> {
    static Specification<ItemPost> hasStatus(ItemStatus status) {
        return (root, query, criteriaBuilder) -> {
            Join<Object, Object> itemJoin = root.join("item");
            if (status == null)
                return criteriaBuilder.not(itemJoin.get("status").equalTo(ItemStatus.RETURNED));
            return criteriaBuilder.equal(itemJoin.get("status"), status);
        };
    }

    static Specification<ItemPost> hasCategory(ItemCategory category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null)
                return null;
            Join<Object, Object> itemJoin = root.join("item");
            return criteriaBuilder.equal(itemJoin.get("category"), category);
        };
    }

    static Specification<ItemPost> hasColor(ItemColor color) {
        return (root, query, criteriaBuilder) -> {
            if (color == null)
                return null;
            Join<Object, Object> itemJoin = root.join("item");
            return criteriaBuilder.equal(itemJoin.get("color"), color);
        };
    }

    static Specification<ItemPost> applyFilter(ItemPostFilter filter) {
        if (filter == null)
            return Specification.where(hasStatus(null));
        return Specification.where(hasStatus(filter.getStatus()))
                .and(hasCategory(filter.getCategory()))
                .and(hasColor(filter.getColor()));
    }

    default ItemPost findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> DataNotFoundException.from("게시물", id));
    }

    long countByUserId(Long userId);
    ItemPost findByItem(Item item);

    @Query("SELECT ip FROM ItemPost ip JOIN FETCH ip.item WHERE ip.item.id IN :itemIds")
    List<ItemPost> findAllByItemIdsWithItem(@Param("itemIds") List<Long> itemIds);

    List<ItemPost> findAllByUser_IdAndItem_Type(Long userId, ItemType type);

    Optional<ItemPost> findByItemId(long id);

    default ItemPost findByItemIdOrThrow(long id) {
        return findByItemId(id)
                .orElseThrow(() -> DataNotFoundException.from("게시물/아이템", id));
    }
}
