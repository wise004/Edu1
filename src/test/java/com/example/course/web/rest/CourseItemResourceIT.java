package com.example.course.web.rest;

import static com.example.course.domain.CourseItemAsserts.*;
import static com.example.course.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.course.IntegrationTest;
import com.example.course.domain.CourseItem;
import com.example.course.domain.enumeration.ContentType;
import com.example.course.domain.enumeration.ItemType;
import com.example.course.repository.CourseItemRepository;
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
 * Integration tests for the {@link CourseItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CourseItemResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final ItemType DEFAULT_ITEM_TYPE = ItemType.LESSON;
    private static final ItemType UPDATED_ITEM_TYPE = ItemType.TEST;

    private static final ContentType DEFAULT_CONTENT_TYPE = ContentType.UPLOADED_VIDEO;
    private static final ContentType UPDATED_CONTENT_TYPE = ContentType.YOUTUBE_VIDEO;

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Integer DEFAULT_PASSING_SCORE = 1;
    private static final Integer UPDATED_PASSING_SCORE = 2;

    private static final String ENTITY_API_URL = "/api/course-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CourseItemRepository courseItemRepository;

    @Mock
    private CourseItemRepository courseItemRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseItemMockMvc;

    private CourseItem courseItem;

    private CourseItem insertedCourseItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseItem createEntity() {
        return new CourseItem()
            .title(DEFAULT_TITLE)
            .itemType(DEFAULT_ITEM_TYPE)
            .contentType(DEFAULT_CONTENT_TYPE)
            .content(DEFAULT_CONTENT)
            .passingScore(DEFAULT_PASSING_SCORE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseItem createUpdatedEntity() {
        return new CourseItem()
            .title(UPDATED_TITLE)
            .itemType(UPDATED_ITEM_TYPE)
            .contentType(UPDATED_CONTENT_TYPE)
            .content(UPDATED_CONTENT)
            .passingScore(UPDATED_PASSING_SCORE);
    }

    @BeforeEach
    public void initTest() {
        courseItem = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCourseItem != null) {
            courseItemRepository.delete(insertedCourseItem);
            insertedCourseItem = null;
        }
    }

    @Test
    @Transactional
    void createCourseItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CourseItem
        var returnedCourseItem = om.readValue(
            restCourseItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseItem)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CourseItem.class
        );

        // Validate the CourseItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCourseItemUpdatableFieldsEquals(returnedCourseItem, getPersistedCourseItem(returnedCourseItem));

        insertedCourseItem = returnedCourseItem;
    }

    @Test
    @Transactional
    void createCourseItemWithExistingId() throws Exception {
        // Create the CourseItem with an existing ID
        courseItem.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseItem)))
            .andExpect(status().isBadRequest());

        // Validate the CourseItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        courseItem.setTitle(null);

        // Create the CourseItem, which fails.

        restCourseItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseItem)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkItemTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        courseItem.setItemType(null);

        // Create the CourseItem, which fails.

        restCourseItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseItem)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCourseItems() throws Exception {
        // Initialize the database
        insertedCourseItem = courseItemRepository.saveAndFlush(courseItem);

        // Get all the courseItemList
        restCourseItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].itemType").value(hasItem(DEFAULT_ITEM_TYPE.toString())))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].passingScore").value(hasItem(DEFAULT_PASSING_SCORE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCourseItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(courseItemRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(courseItemRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCourseItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(courseItemRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(courseItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCourseItem() throws Exception {
        // Initialize the database
        insertedCourseItem = courseItemRepository.saveAndFlush(courseItem);

        // Get the courseItem
        restCourseItemMockMvc
            .perform(get(ENTITY_API_URL_ID, courseItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(courseItem.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.itemType").value(DEFAULT_ITEM_TYPE.toString()))
            .andExpect(jsonPath("$.contentType").value(DEFAULT_CONTENT_TYPE.toString()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.passingScore").value(DEFAULT_PASSING_SCORE));
    }

    @Test
    @Transactional
    void getNonExistingCourseItem() throws Exception {
        // Get the courseItem
        restCourseItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCourseItem() throws Exception {
        // Initialize the database
        insertedCourseItem = courseItemRepository.saveAndFlush(courseItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseItem
        CourseItem updatedCourseItem = courseItemRepository.findById(courseItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCourseItem are not directly saved in db
        em.detach(updatedCourseItem);
        updatedCourseItem
            .title(UPDATED_TITLE)
            .itemType(UPDATED_ITEM_TYPE)
            .contentType(UPDATED_CONTENT_TYPE)
            .content(UPDATED_CONTENT)
            .passingScore(UPDATED_PASSING_SCORE);

        restCourseItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCourseItem.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCourseItem))
            )
            .andExpect(status().isOk());

        // Validate the CourseItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCourseItemToMatchAllProperties(updatedCourseItem);
    }

    @Test
    @Transactional
    void putNonExistingCourseItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseItem.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseItem.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCourseItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCourseItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseItem)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCourseItemWithPatch() throws Exception {
        // Initialize the database
        insertedCourseItem = courseItemRepository.saveAndFlush(courseItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseItem using partial update
        CourseItem partialUpdatedCourseItem = new CourseItem();
        partialUpdatedCourseItem.setId(courseItem.getId());

        partialUpdatedCourseItem.title(UPDATED_TITLE).itemType(UPDATED_ITEM_TYPE).passingScore(UPDATED_PASSING_SCORE);

        restCourseItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseItem))
            )
            .andExpect(status().isOk());

        // Validate the CourseItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCourseItem, courseItem),
            getPersistedCourseItem(courseItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateCourseItemWithPatch() throws Exception {
        // Initialize the database
        insertedCourseItem = courseItemRepository.saveAndFlush(courseItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseItem using partial update
        CourseItem partialUpdatedCourseItem = new CourseItem();
        partialUpdatedCourseItem.setId(courseItem.getId());

        partialUpdatedCourseItem
            .title(UPDATED_TITLE)
            .itemType(UPDATED_ITEM_TYPE)
            .contentType(UPDATED_CONTENT_TYPE)
            .content(UPDATED_CONTENT)
            .passingScore(UPDATED_PASSING_SCORE);

        restCourseItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseItem))
            )
            .andExpect(status().isOk());

        // Validate the CourseItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseItemUpdatableFieldsEquals(partialUpdatedCourseItem, getPersistedCourseItem(partialUpdatedCourseItem));
    }

    @Test
    @Transactional
    void patchNonExistingCourseItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseItem.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, courseItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCourseItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCourseItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(courseItem)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCourseItem() throws Exception {
        // Initialize the database
        insertedCourseItem = courseItemRepository.saveAndFlush(courseItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the courseItem
        restCourseItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, courseItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return courseItemRepository.count();
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

    protected CourseItem getPersistedCourseItem(CourseItem courseItem) {
        return courseItemRepository.findById(courseItem.getId()).orElseThrow();
    }

    protected void assertPersistedCourseItemToMatchAllProperties(CourseItem expectedCourseItem) {
        assertCourseItemAllPropertiesEquals(expectedCourseItem, getPersistedCourseItem(expectedCourseItem));
    }

    protected void assertPersistedCourseItemToMatchUpdatableProperties(CourseItem expectedCourseItem) {
        assertCourseItemAllUpdatablePropertiesEquals(expectedCourseItem, getPersistedCourseItem(expectedCourseItem));
    }
}
