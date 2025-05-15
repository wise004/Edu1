package com.example.course.web.rest;

import com.example.course.domain.LessonProgress;
import com.example.course.repository.LessonProgressRepository;
import com.example.course.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
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
 * REST controller for managing {@link com.example.course.domain.LessonProgress}.
 */
@RestController
@RequestMapping("/api/lesson-progresses")
@Transactional
public class LessonProgressResource {

    private static final Logger LOG = LoggerFactory.getLogger(LessonProgressResource.class);

    private static final String ENTITY_NAME = "lessonProgress";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LessonProgressRepository lessonProgressRepository;

    public LessonProgressResource(LessonProgressRepository lessonProgressRepository) {
        this.lessonProgressRepository = lessonProgressRepository;
    }

    /**
     * {@code POST  /lesson-progresses} : Create a new lessonProgress.
     *
     * @param lessonProgress the lessonProgress to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lessonProgress, or with status {@code 400 (Bad Request)} if the lessonProgress has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LessonProgress> createLessonProgress(@Valid @RequestBody LessonProgress lessonProgress)
        throws URISyntaxException {
        LOG.debug("REST request to save LessonProgress : {}", lessonProgress);
        if (lessonProgress.getId() != null) {
            throw new BadRequestAlertException("A new lessonProgress cannot already have an ID", ENTITY_NAME, "idexists");
        }
        lessonProgress = lessonProgressRepository.save(lessonProgress);
        return ResponseEntity.created(new URI("/api/lesson-progresses/" + lessonProgress.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, lessonProgress.getId().toString()))
            .body(lessonProgress);
    }

    /**
     * {@code PUT  /lesson-progresses/:id} : Updates an existing lessonProgress.
     *
     * @param id the id of the lessonProgress to save.
     * @param lessonProgress the lessonProgress to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lessonProgress,
     * or with status {@code 400 (Bad Request)} if the lessonProgress is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lessonProgress couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LessonProgress> updateLessonProgress(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LessonProgress lessonProgress
    ) throws URISyntaxException {
        LOG.debug("REST request to update LessonProgress : {}, {}", id, lessonProgress);
        if (lessonProgress.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lessonProgress.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lessonProgressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        lessonProgress = lessonProgressRepository.save(lessonProgress);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lessonProgress.getId().toString()))
            .body(lessonProgress);
    }

    /**
     * {@code PATCH  /lesson-progresses/:id} : Partial updates given fields of an existing lessonProgress, field will ignore if it is null
     *
     * @param id the id of the lessonProgress to save.
     * @param lessonProgress the lessonProgress to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lessonProgress,
     * or with status {@code 400 (Bad Request)} if the lessonProgress is not valid,
     * or with status {@code 404 (Not Found)} if the lessonProgress is not found,
     * or with status {@code 500 (Internal Server Error)} if the lessonProgress couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LessonProgress> partialUpdateLessonProgress(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LessonProgress lessonProgress
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LessonProgress partially : {}, {}", id, lessonProgress);
        if (lessonProgress.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lessonProgress.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lessonProgressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LessonProgress> result = lessonProgressRepository
            .findById(lessonProgress.getId())
            .map(existingLessonProgress -> {
                if (lessonProgress.getViewed() != null) {
                    existingLessonProgress.setViewed(lessonProgress.getViewed());
                }
                if (lessonProgress.getViewedDate() != null) {
                    existingLessonProgress.setViewedDate(lessonProgress.getViewedDate());
                }

                return existingLessonProgress;
            })
            .map(lessonProgressRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lessonProgress.getId().toString())
        );
    }

    /**
     * {@code GET  /lesson-progresses} : get all the lessonProgresses.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of lessonProgresses in body.
     */
    @GetMapping("")
    public List<LessonProgress> getAllLessonProgresses(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload,
        @RequestParam(name = "courseItemId", required = false) Long courseItemId
    ) {
        LOG.debug("REST request to get LessonProgresses for courseItemId: {}", courseItemId);
        if (courseItemId != null) {
            return lessonProgressRepository.findByCourseItemId(courseItemId);
        }
        if (eagerload) {
            return lessonProgressRepository.findAllWithEagerRelationships();
        } else {
            return lessonProgressRepository.findAll();
        }
    }

    /**
     * {@code GET  /lesson-progresses/:id} : get the "id" lessonProgress.
     *
     * @param id the id of the lessonProgress to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lessonProgress, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LessonProgress> getLessonProgress(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LessonProgress : {}", id);
        Optional<LessonProgress> lessonProgress = lessonProgressRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(lessonProgress);
    }

    /**
     * {@code DELETE  /lesson-progresses/:id} : delete the "id" lessonProgress.
     *
     * @param id the id of the lessonProgress to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLessonProgress(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LessonProgress : {}", id);
        lessonProgressRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PatchMapping("/{id}/mark-viewed")
    public ResponseEntity<LessonProgress> markLessonViewed(@PathVariable Long id) throws URISyntaxException {
        LOG.debug("REST request to mark LessonProgress as viewed: {}", id);
        Optional<LessonProgress> progressOpt = lessonProgressRepository.findById(id);
        if (!progressOpt.isPresent()) {
            throw new BadRequestAlertException("LessonProgress not found", ENTITY_NAME, "idnotfound");
        }
        LessonProgress progress = progressOpt.get();
        progress.setViewed(true);
        progress.setViewedDate(Instant.from(LocalDateTime.now()));
        progress = lessonProgressRepository.save(progress);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(progress);
    }
}
