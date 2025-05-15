package com.example.course.repository;

import com.example.course.domain.CourseProgress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CourseProgress entity.
 */
@Repository
public interface CourseProgressRepository extends JpaRepository<CourseProgress, Long> {
    @Query("select courseProgress from CourseProgress courseProgress where courseProgress.student.login = ?#{authentication.name}")
    List<CourseProgress> findByStudentIsCurrentUser();

    default Optional<CourseProgress> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CourseProgress> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CourseProgress> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select courseProgress from CourseProgress courseProgress left join fetch courseProgress.student left join fetch courseProgress.course",
        countQuery = "select count(courseProgress) from CourseProgress courseProgress"
    )
    Page<CourseProgress> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select courseProgress from CourseProgress courseProgress left join fetch courseProgress.student left join fetch courseProgress.course"
    )
    List<CourseProgress> findAllWithToOneRelationships();

    @Query(
        "select courseProgress from CourseProgress courseProgress left join fetch courseProgress.student left join fetch courseProgress.course where courseProgress.id =:id"
    )
    Optional<CourseProgress> findOneWithToOneRelationships(@Param("id") Long id);
}
