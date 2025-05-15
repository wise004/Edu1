package com.example.course.web.rest;

import static com.example.course.domain.CourseProgressAsserts.*;
import static com.example.course.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.course.IntegrationTest;
import com.example.course.domain.CourseProgress;
import com.example.course.repository.CourseProgressRepository;
import com.example.course.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CourseProgressResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CourseProgressResourceIT {

    private static final Integer DEFAULT_COMPLETED_ITEMS = 1;
    private static final Integer UPDATED_COMPLETED_ITEMS = 2;

    private static final Boolean DEFAULT_IS_COMPLETED = false;
    private static final Boolean UPDATED_IS_COMPLETED = true;

    private static final String ENTITY_API_URL = "/api/course-progresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CourseProgressRepository courseProgressRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private CourseProgressRepository courseProgressRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseProgressMockMvc;

    private CourseProgress courseProgress;

    private CourseProgress insertedCourseProgress;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseProgress createEntity() {
        return new CourseProgress().completedItems(DEFAULT_COMPLETED_ITEMS).isCompleted(DEFAULT_IS_COMPLETED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseProgress createUpdatedEntity() {
        return new CourseProgress().completedItems(UPDATED_COMPLETED_ITEMS).isCompleted(UPDATED_IS_COMPLETED);
    }

    @BeforeEach
    public void initTest() {
        courseProgress = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCourseProgress != null) {
            courseProgressRepository.delete(insertedCourseProgress);
            insertedCourseProgress = null;
        }
    }

    @Test
    @Transactional
    void createCourseProgress() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CourseProgress
        var returnedCourseProgress = om.readValue(
            restCourseProgressMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseProgress)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CourseProgress.class
        );

        // Validate the CourseProgress in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCourseProgressUpdatableFieldsEquals(returnedCourseProgress, getPersistedCourseProgress(returnedCourseProgress));

        insertedCourseProgress = returnedCourseProgress;
    }

    @Test
    @Transactional
    void createCourseProgressWithExistingId() throws Exception {
        // Create the CourseProgress with an existing ID
        courseProgress.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseProgress)))
            .andExpect(status().isBadRequest());

        // Validate the CourseProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCourseProgresses() throws Exception {
        // Initialize the database
        insertedCourseProgress = courseProgressRepository.saveAndFlush(courseProgress);

        // Get all the courseProgressList
        restCourseProgressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseProgress.getId().intValue())))
            .andExpect(jsonPath("$.[*].completedItems").value(hasItem(DEFAULT_COMPLETED_ITEMS)))
            .andExpect(jsonPath("$.[*].isCompleted").value(hasItem(DEFAULT_IS_COMPLETED)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCourseProgressesWithEagerRelationshipsIsEnabled() throws Exception {
        when(courseProgressRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseProgressMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(courseProgressRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCourseProgressesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(courseProgressRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseProgressMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(courseProgressRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCourseProgress() throws Exception {
        // Initialize the database
        insertedCourseProgress = courseProgressRepository.saveAndFlush(courseProgress);

        // Get the courseProgress
        restCourseProgressMockMvc
            .perform(get(ENTITY_API_URL_ID, courseProgress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(courseProgress.getId().intValue()))
            .andExpect(jsonPath("$.completedItems").value(DEFAULT_COMPLETED_ITEMS))
            .andExpect(jsonPath("$.isCompleted").value(DEFAULT_IS_COMPLETED));
    }

    @Test
    @Transactional
    void getNonExistingCourseProgress() throws Exception {
        // Get the courseProgress
        restCourseProgressMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCourseProgress() throws Exception {
        // Initialize the database
        insertedCourseProgress = courseProgressRepository.saveAndFlush(courseProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseProgress
        CourseProgress updatedCourseProgress = courseProgressRepository.findById(courseProgress.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCourseProgress are not directly saved in db
        em.detach(updatedCourseProgress);
        updatedCourseProgress.completedItems(UPDATED_COMPLETED_ITEMS).isCompleted(UPDATED_IS_COMPLETED);

        restCourseProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCourseProgress.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCourseProgress))
            )
            .andExpect(status().isOk());

        // Validate the CourseProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCourseProgressToMatchAllProperties(updatedCourseProgress);
    }

    @Test
    @Transactional
    void putNonExistingCourseProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseProgress.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseProgress.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseProgress))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCourseProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseProgress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseProgress))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCourseProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseProgress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseProgressMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseProgress)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCourseProgressWithPatch() throws Exception {
        // Initialize the database
        insertedCourseProgress = courseProgressRepository.saveAndFlush(courseProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseProgress using partial update
        CourseProgress partialUpdatedCourseProgress = new CourseProgress();
        partialUpdatedCourseProgress.setId(courseProgress.getId());

        partialUpdatedCourseProgress.isCompleted(UPDATED_IS_COMPLETED);

        restCourseProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseProgress))
            )
            .andExpect(status().isOk());

        // Validate the CourseProgress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseProgressUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCourseProgress, courseProgress),
            getPersistedCourseProgress(courseProgress)
        );
    }

    @Test
    @Transactional
    void fullUpdateCourseProgressWithPatch() throws Exception {
        // Initialize the database
        insertedCourseProgress = courseProgressRepository.saveAndFlush(courseProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseProgress using partial update
        CourseProgress partialUpdatedCourseProgress = new CourseProgress();
        partialUpdatedCourseProgress.setId(courseProgress.getId());

        partialUpdatedCourseProgress.completedItems(UPDATED_COMPLETED_ITEMS).isCompleted(UPDATED_IS_COMPLETED);

        restCourseProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseProgress))
            )
            .andExpect(status().isOk());

        // Validate the CourseProgress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseProgressUpdatableFieldsEquals(partialUpdatedCourseProgress, getPersistedCourseProgress(partialUpdatedCourseProgress));
    }

    @Test
    @Transactional
    void patchNonExistingCourseProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseProgress.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, courseProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseProgress))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCourseProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseProgress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseProgress))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCourseProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseProgress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseProgressMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(courseProgress)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCourseProgress() throws Exception {
        // Initialize the database
        insertedCourseProgress = courseProgressRepository.saveAndFlush(courseProgress);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the courseProgress
        restCourseProgressMockMvc
            .perform(delete(ENTITY_API_URL_ID, courseProgress.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return courseProgressRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected CourseProgress getPersistedCourseProgress(CourseProgress courseProgress) {
        return courseProgressRepository.findById(courseProgress.getId()).orElseThrow();
    }

    protected void assertPersistedCourseProgressToMatchAllProperties(CourseProgress expectedCourseProgress) {
        assertCourseProgressAllPropertiesEquals(expectedCourseProgress, getPersistedCourseProgress(expectedCourseProgress));
    }

    protected void assertPersistedCourseProgressToMatchUpdatableProperties(CourseProgress expectedCourseProgress) {
        assertCourseProgressAllUpdatablePropertiesEquals(expectedCourseProgress, getPersistedCourseProgress(expectedCourseProgress));
    }
}
