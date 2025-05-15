package com.example.course.repository;

import com.example.course.domain.CourseItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CourseItem entity.
 */
@Repository
public interface CourseItemRepository extends JpaRepository<CourseItem, Long> {
    List<CourseItem> findByCourseId(Long courseId);

    default Optional<CourseItem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CourseItem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CourseItem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select courseItem from CourseItem courseItem left join fetch courseItem.course",
        countQuery = "select count(courseItem) from CourseItem courseItem"
    )
    Page<CourseItem> findAllWithToOneRelationships(Pageable pageable);

    @Query("select courseItem from CourseItem courseItem left join fetch courseItem.course")
    List<CourseItem> findAllWithToOneRelationships();

    @Query("select courseItem from CourseItem courseItem left join fetch courseItem.course where courseItem.id =:id")
    Optional<CourseItem> findOneWithToOneRelationships(@Param("id") Long id);
}
