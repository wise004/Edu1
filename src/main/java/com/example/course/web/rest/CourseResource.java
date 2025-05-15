package com.example.course.web.rest;

import com.example.course.domain.Course;
import com.example.course.domain.CourseItem;
import com.example.course.repository.CourseItemRepository;
import com.example.course.repository.CourseRepository;
import com.example.course.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.example.course.domain.Course}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CourseResource {

    private static final Logger LOG = LoggerFactory.getLogger(CourseResource.class);

    private static final String ENTITY_NAME = "course";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CourseRepository courseRepository;
    private final CourseItemRepository courseItemRepository; // Add this

    public CourseResource(CourseRepository courseRepository, CourseItemRepository courseItemRepository) {
        this.courseRepository = courseRepository;
        this.courseItemRepository = courseItemRepository; // Add this
    }

    /**
     * {@code POST  /courses} : Create a new course.
     */
    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course) throws URISyntaxException {
        LOG.debug("REST request to save Course : {}", course);
        if (course.getId() != null) {
            throw new BadRequestAlertException("A new course cannot already have an ID", ENTITY_NAME, "idexists");
        }
        course = courseRepository.save(course);
        return ResponseEntity.created(new URI("/api/courses/" + course.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, course.getId().toString()))
            .body(course);
    }

    /**
     * {@code PUT  /courses/:id} : Updates an existing course.
     */
    @PutMapping("/courses/{id}")
    public ResponseEntity<Course> updateCourse(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Course course
    ) throws URISyntaxException {
        LOG.debug("REST request to update Course : {}, {}", id, course);
        if (course.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, course.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        course = courseRepository.save(course);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, course.getId().toString()))
            .body(course);
    }

    /**
     * {@code PATCH  /courses/:id} : Partial updates given fields of an existing course.
     */
    @PatchMapping(value = "/courses/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Course> partialUpdateCourse(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Course course
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Course partially : {}, {}", id, course);
        if (course.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, course.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Course> result = courseRepository
            .findById(course.getId())
            .map(existingCourse -> {
                if (course.getTitle() != null) {
                    existingCourse.setTitle(course.getTitle());
                }
                if (course.getDescription() != null) {
                    existingCourse.setDescription(course.getDescription());
                }
                return existingCourse;
            })
            .map(courseRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, course.getId().toString())
        );
    }

    /**
     * {@code GET  /courses} : get all the courses.
     */
    @GetMapping("/courses")
    public List<Course> getAllCourses(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all Courses");
        if (eagerload) {
            return courseRepository.findAllWithEagerRelationships();
        } else {
            return courseRepository.findAll();
        }
    }

    /**
     * {@code GET  /courses/:id} : get the "id" course.
     */
    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Course : {}", id);
        Optional<Course> course = courseRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(course);
    }

    /**
     * {@code DELETE  /courses/:id} : delete the "id" course.
     */
    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Course : {}", id);
        courseRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET /courses/:courseId/items} : Get all items for a specific course.
     *
     * @param courseId the id of the course to retrieve items for.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of course items in body.
     */
    @GetMapping("/courses/{courseId}/items")
    public ResponseEntity<List<CourseItem>> getCourseItems(@PathVariable Long courseId) {
        LOG.debug("REST request to get CourseItems for Course: {}", courseId);
        // Check if the course exists
        if (!courseRepository.existsById(courseId)) {
            LOG.warn("Course with ID {} not found", courseId);
            return ResponseEntity.notFound().build();
        }
        LOG.debug("Course with ID {} exists", courseId);
        // Fetch the course items
        List<CourseItem> courseItems = courseItemRepository.findByCourseId(courseId);
        LOG.debug("Found {} CourseItems for Course ID {}: {}", courseItems.size(), courseId, courseItems);
        return ResponseEntity.ok().body(courseItems);
    }
}
