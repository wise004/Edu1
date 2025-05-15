package com.example.course.web.rest;

import com.example.course.domain.CourseProgress;
import com.example.course.repository.CourseProgressRepository;
import com.example.course.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.example.course.domain.CourseProgress}.
 */
@RestController
@RequestMapping("/api/course-progresses")
@Transactional
public class CourseProgressResource {

    private static final Logger LOG = LoggerFactory.getLogger(CourseProgressResource.class);

    private static final String ENTITY_NAME = "courseProgress";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CourseProgressRepository courseProgressRepository;

    public CourseProgressResource(CourseProgressRepository courseProgressRepository) {
        this.courseProgressRepository = courseProgressRepository;
    }

    /**
     * {@code POST  /course-progresses} : Create a new courseProgress.
     *
     * @param courseProgress the courseProgress to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new courseProgress, or with status {@code 400 (Bad Request)} if the courseProgress has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CourseProgress> createCourseProgress(@RequestBody CourseProgress courseProgress) throws URISyntaxException {
        LOG.debug("REST request to save CourseProgress : {}", courseProgress);
        if (courseProgress.getId() != null) {
            throw new BadRequestAlertException("A new courseProgress cannot already have an ID", ENTITY_NAME, "idexists");
        }
        courseProgress = courseProgressRepository.save(courseProgress);
        return ResponseEntity.created(new URI("/api/course-progresses/" + courseProgress.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, courseProgress.getId().toString()))
            .body(courseProgress);
    }

    /**
     * {@code PUT  /course-progresses/:id} : Updates an existing courseProgress.
     *
     * @param id the id of the courseProgress to save.
     * @param courseProgress the courseProgress to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseProgress,
     * or with status {@code 400 (Bad Request)} if the courseProgress is not valid,
     * or with status {@code 500 (Internal Server Error)} if the courseProgress couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseProgress> updateCourseProgress(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CourseProgress courseProgress
    ) throws URISyntaxException {
        LOG.debug("REST request to update CourseProgress : {}, {}", id, courseProgress);
        if (courseProgress.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseProgress.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseProgressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        courseProgress = courseProgressRepository.save(courseProgress);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseProgress.getId().toString()))
            .body(courseProgress);
    }

    /**
     * {@code PATCH  /course-progresses/:id} : Partial updates given fields of an existing courseProgress, field will ignore if it is null
     *
     * @param id the id of the courseProgress to save.
     * @param courseProgress the courseProgress to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseProgress,
     * or with status {@code 400 (Bad Request)} if the courseProgress is not valid,
     * or with status {@code 404 (Not Found)} if the courseProgress is not found,
     * or with status {@code 500 (Internal Server Error)} if the courseProgress couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CourseProgress> partialUpdateCourseProgress(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CourseProgress courseProgress
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CourseProgress partially : {}, {}", id, courseProgress);
        if (courseProgress.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseProgress.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseProgressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CourseProgress> result = courseProgressRepository
            .findById(courseProgress.getId())
            .map(existingCourseProgress -> {
                if (courseProgress.getCompletedItems() != null) {
                    existingCourseProgress.setCompletedItems(courseProgress.getCompletedItems());
                }
                if (courseProgress.getIsCompleted() != null) {
                    existingCourseProgress.setIsCompleted(courseProgress.getIsCompleted());
                }

                return existingCourseProgress;
            })
            .map(courseProgressRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseProgress.getId().toString())
        );
    }

    /**
     * {@code GET  /course-progresses} : get all the courseProgresses.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of courseProgresses in body.
     */
    @GetMapping("")
    public List<CourseProgress> getAllCourseProgresses(
        @RequestParam(name = "filter", required = false) String filter,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        if ("certificate-is-null".equals(filter)) {
            LOG.debug("REST request to get all CourseProgresss where certificate is null");
            return StreamSupport.stream(courseProgressRepository.findAll().spliterator(), false)
                .filter(courseProgress -> courseProgress.getCertificate() == null)
                .toList();
        }
        LOG.debug("REST request to get all CourseProgresses");
        if (eagerload) {
            return courseProgressRepository.findAllWithEagerRelationships();
        } else {
            return courseProgressRepository.findAll();
        }
    }

    /**
     * {@code GET  /course-progresses/:id} : get the "id" courseProgress.
     *
     * @param id the id of the courseProgress to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the courseProgress, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseProgress> getCourseProgress(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CourseProgress : {}", id);
        Optional<CourseProgress> courseProgress = courseProgressRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(courseProgress);
    }

    /**
     * {@code DELETE  /course-progresses/:id} : delete the "id" courseProgress.
     *
     * @param id the id of the courseProgress to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourseProgress(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CourseProgress : {}", id);
        courseProgressRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
