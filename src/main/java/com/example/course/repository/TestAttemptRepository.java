package com.example.course.repository;

import com.example.course.domain.TestAttempt;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TestAttempt entity.
 */
@Repository
public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {
    @Query("select testAttempt from TestAttempt testAttempt where testAttempt.student.login = ?#{authentication.name}")
    List<TestAttempt> findByStudentIsCurrentUser();

    default Optional<TestAttempt> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TestAttempt> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TestAttempt> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select testAttempt from TestAttempt testAttempt left join fetch testAttempt.student left join fetch testAttempt.courseItem",
        countQuery = "select count(testAttempt) from TestAttempt testAttempt"
    )
    Page<TestAttempt> findAllWithToOneRelationships(Pageable pageable);

    @Query("select testAttempt from TestAttempt testAttempt left join fetch testAttempt.student left join fetch testAttempt.courseItem")
    List<TestAttempt> findAllWithToOneRelationships();

    @Query(
        "select testAttempt from TestAttempt testAttempt left join fetch testAttempt.student left join fetch testAttempt.courseItem where testAttempt.id =:id"
    )
    Optional<TestAttempt> findOneWithToOneRelationships(@Param("id") Long id);
}
