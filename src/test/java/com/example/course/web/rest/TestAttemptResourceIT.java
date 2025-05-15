package com.example.course.web.rest;

import static com.example.course.domain.TestAttemptAsserts.*;
import static com.example.course.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.course.IntegrationTest;
import com.example.course.domain.TestAttempt;
import com.example.course.repository.TestAttemptRepository;
import com.example.course.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link TestAttemptResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TestAttemptResourceIT {

    private static final Integer DEFAULT_SCORE = 1;
    private static final Integer UPDATED_SCORE = 2;

    private static final Boolean DEFAULT_PASSED = false;
    private static final Boolean UPDATED_PASSED = true;

    private static final Instant DEFAULT_ATTEMPT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ATTEMPT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/test-attempts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TestAttemptRepository testAttemptRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private TestAttemptRepository testAttemptRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTestAttemptMockMvc;

    private TestAttempt testAttempt;

    private TestAttempt insertedTestAttempt;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TestAttempt createEntity() {
        return new TestAttempt().score(DEFAULT_SCORE).passed(DEFAULT_PASSED).attemptDate(DEFAULT_ATTEMPT_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TestAttempt createUpdatedEntity() {
        return new TestAttempt().score(UPDATED_SCORE).passed(UPDATED_PASSED).attemptDate(UPDATED_ATTEMPT_DATE);
    }

    @BeforeEach
    public void initTest() {
        testAttempt = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTestAttempt != null) {
            testAttemptRepository.delete(insertedTestAttempt);
            insertedTestAttempt = null;
        }
    }

    @Test
    @Transactional
    void createTestAttempt() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TestAttempt
        var returnedTestAttempt = om.readValue(
            restTestAttemptMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testAttempt)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TestAttempt.class
        );

        // Validate the TestAttempt in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTestAttemptUpdatableFieldsEquals(returnedTestAttempt, getPersistedTestAttempt(returnedTestAttempt));

        insertedTestAttempt = returnedTestAttempt;
    }

    @Test
    @Transactional
    void createTestAttemptWithExistingId() throws Exception {
        // Create the TestAttempt with an existing ID
        testAttempt.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTestAttemptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testAttempt)))
            .andExpect(status().isBadRequest());

        // Validate the TestAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTestAttempts() throws Exception {
        // Initialize the database
        insertedTestAttempt = testAttemptRepository.saveAndFlush(testAttempt);

        // Get all the testAttemptList
        restTestAttemptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(testAttempt.getId().intValue())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)))
            .andExpect(jsonPath("$.[*].passed").value(hasItem(DEFAULT_PASSED)))
            .andExpect(jsonPath("$.[*].attemptDate").value(hasItem(DEFAULT_ATTEMPT_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTestAttemptsWithEagerRelationshipsIsEnabled() throws Exception {
        when(testAttemptRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTestAttemptMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(testAttemptRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTestAttemptsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(testAttemptRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTestAttemptMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(testAttemptRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTestAttempt() throws Exception {
        // Initialize the database
        insertedTestAttempt = testAttemptRepository.saveAndFlush(testAttempt);

        // Get the testAttempt
        restTestAttemptMockMvc
            .perform(get(ENTITY_API_URL_ID, testAttempt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(testAttempt.getId().intValue()))
            .andExpect(jsonPath("$.score").value(DEFAULT_SCORE))
            .andExpect(jsonPath("$.passed").value(DEFAULT_PASSED))
            .andExpect(jsonPath("$.attemptDate").value(DEFAULT_ATTEMPT_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTestAttempt() throws Exception {
        // Get the testAttempt
        restTestAttemptMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTestAttempt() throws Exception {
        // Initialize the database
        insertedTestAttempt = testAttemptRepository.saveAndFlush(testAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the testAttempt
        TestAttempt updatedTestAttempt = testAttemptRepository.findById(testAttempt.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTestAttempt are not directly saved in db
        em.detach(updatedTestAttempt);
        updatedTestAttempt.score(UPDATED_SCORE).passed(UPDATED_PASSED).attemptDate(UPDATED_ATTEMPT_DATE);

        restTestAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTestAttempt.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTestAttempt))
            )
            .andExpect(status().isOk());

        // Validate the TestAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTestAttemptToMatchAllProperties(updatedTestAttempt);
    }

    @Test
    @Transactional
    void putNonExistingTestAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        testAttempt.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTestAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, testAttempt.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(testAttempt))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTestAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        testAttempt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(testAttempt))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTestAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        testAttempt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestAttemptMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testAttempt)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TestAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTestAttemptWithPatch() throws Exception {
        // Initialize the database
        insertedTestAttempt = testAttemptRepository.saveAndFlush(testAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the testAttempt using partial update
        TestAttempt partialUpdatedTestAttempt = new TestAttempt();
        partialUpdatedTestAttempt.setId(testAttempt.getId());

        partialUpdatedTestAttempt.score(UPDATED_SCORE);

        restTestAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTestAttempt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTestAttempt))
            )
            .andExpect(status().isOk());

        // Validate the TestAttempt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTestAttemptUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTestAttempt, testAttempt),
            getPersistedTestAttempt(testAttempt)
        );
    }

    @Test
    @Transactional
    void fullUpdateTestAttemptWithPatch() throws Exception {
        // Initialize the database
        insertedTestAttempt = testAttemptRepository.saveAndFlush(testAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the testAttempt using partial update
        TestAttempt partialUpdatedTestAttempt = new TestAttempt();
        partialUpdatedTestAttempt.setId(testAttempt.getId());

        partialUpdatedTestAttempt.score(UPDATED_SCORE).passed(UPDATED_PASSED).attemptDate(UPDATED_ATTEMPT_DATE);

        restTestAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTestAttempt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTestAttempt))
            )
            .andExpect(status().isOk());

        // Validate the TestAttempt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTestAttemptUpdatableFieldsEquals(partialUpdatedTestAttempt, getPersistedTestAttempt(partialUpdatedTestAttempt));
    }

    @Test
    @Transactional
    void patchNonExistingTestAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        testAttempt.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTestAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, testAttempt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(testAttempt))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTestAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        testAttempt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(testAttempt))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTestAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        testAttempt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestAttemptMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(testAttempt)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TestAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTestAttempt() throws Exception {
        // Initialize the database
        insertedTestAttempt = testAttemptRepository.saveAndFlush(testAttempt);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the testAttempt
        restTestAttemptMockMvc
            .perform(delete(ENTITY_API_URL_ID, testAttempt.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return testAttemptRepository.count();
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

    protected TestAttempt getPersistedTestAttempt(TestAttempt testAttempt) {
        return testAttemptRepository.findById(testAttempt.getId()).orElseThrow();
    }

    protected void assertPersistedTestAttemptToMatchAllProperties(TestAttempt expectedTestAttempt) {
        assertTestAttemptAllPropertiesEquals(expectedTestAttempt, getPersistedTestAttempt(expectedTestAttempt));
    }

    protected void assertPersistedTestAttemptToMatchUpdatableProperties(TestAttempt expectedTestAttempt) {
        assertTestAttemptAllUpdatablePropertiesEquals(expectedTestAttempt, getPersistedTestAttempt(expectedTestAttempt));
    }
}
