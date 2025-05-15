package com.example.course.web.rest;

import com.example.course.domain.CourseItem;
import com.example.course.repository.CourseItemRepository;
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
 * REST controller for managing {@link com.example.course.domain.CourseItem}.
 */
@RestController
@RequestMapping("/api/course-items")
@Transactional
public class CourseItemResource { // /course/${courseId}/items`

    private static final Logger LOG = LoggerFactory.getLogger(CourseItemResource.class);

    private static final String ENTITY_NAME = "courseItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CourseItemRepository courseItemRepository;

    public CourseItemResource(CourseItemRepository courseItemRepository) {
        this.courseItemRepository = courseItemRepository;
    }

    /**
     * {@code POST  /course-items} : Create a new courseItem.
     *
     * @param courseItem the courseItem to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new courseItem, or with status {@code 400 (Bad Request)} if the courseItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CourseItem> createCourseItem(@Valid @RequestBody CourseItem courseItem) throws URISyntaxException {
        LOG.debug("REST request to save CourseItem : {}", courseItem);
        if (courseItem.getId() != null) {
            throw new BadRequestAlertException("A new courseItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        courseItem = courseItemRepository.save(courseItem);
        return ResponseEntity.created(new URI("/api/course-items/" + courseItem.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, courseItem.getId().toString()))
            .body(courseItem);
    }

    /**
     * {@code PUT  /course-items/:id} : Updates an existing courseItem.
     *
     * @param id the id of the courseItem to save.
     * @param courseItem the courseItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseItem,
     * or with status {@code 400 (Bad Request)} if the courseItem is not valid,
     * or with status {@code 500 (Internal Server Error)} if the courseItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseItem> updateCourseItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CourseItem courseItem
    ) throws URISyntaxException {
        LOG.debug("REST request to update CourseItem : {}, {}", id, courseItem);
        if (courseItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        courseItem = courseItemRepository.save(courseItem);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseItem.getId().toString()))
            .body(courseItem);
    }

    /**
     * {@code PATCH  /course-items/:id} : Partial updates given fields of an existing courseItem, field will ignore if it is null
     *
     * @param id the id of the courseItem to save.
     * @param courseItem the courseItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseItem,
     * or with status {@code 400 (Bad Request)} if the courseItem is not valid,
     * or with status {@code 404 (Not Found)} if the courseItem is not found,
     * or with status {@code 500 (Internal Server Error)} if the courseItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CourseItem> partialUpdateCourseItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CourseItem courseItem
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CourseItem partially : {}, {}", id, courseItem);
        if (courseItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CourseItem> result = courseItemRepository
            .findById(courseItem.getId())
            .map(existingCourseItem -> {
                if (courseItem.getTitle() != null) {
                    existingCourseItem.setTitle(courseItem.getTitle());
                }
                if (courseItem.getItemType() != null) {
                    existingCourseItem.setItemType(courseItem.getItemType());
                }
                if (courseItem.getContentType() != null) {
                    existingCourseItem.setContentType(courseItem.getContentType());
                }
                if (courseItem.getContent() != null) {
                    existingCourseItem.setContent(courseItem.getContent());
                }
                if (courseItem.getPassingScore() != null) {
                    existingCourseItem.setPassingScore(courseItem.getPassingScore());
                }

                return existingCourseItem;
            })
            .map(courseItemRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseItem.getId().toString())
        );
    }

    /**
     * {@code GET  /course-items} : get all the courseItems.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of courseItems in body.
     */
    @GetMapping("")
    public List<CourseItem> getAllCourseItems(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all CourseItems");
        if (eagerload) {
            return courseItemRepository.findAllWithEagerRelationships();
        } else {
            return courseItemRepository.findAll();
        }
    }

    /**
     * {@code GET  /course-items/:id} : get the "id" courseItem.
     *
     * @param id the id of the courseItem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the courseItem, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseItem> getCourseItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CourseItem : {}", id);
        Optional<CourseItem> courseItem = courseItemRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(courseItem);
    }

    /**
     * {@code DELETE  /course-items/:id} : delete the "id" courseItem.
     *
     * @param id the id of the courseItem to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourseItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CourseItem : {}", id);
        courseItemRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
