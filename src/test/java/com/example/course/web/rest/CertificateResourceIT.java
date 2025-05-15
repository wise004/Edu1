package com.example.course.web.rest;

import static com.example.course.domain.CertificateAsserts.*;
import static com.example.course.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.course.IntegrationTest;
import com.example.course.domain.Certificate;
import com.example.course.repository.CertificateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CertificateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CertificateResourceIT {

    private static final Instant DEFAULT_ISSUE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ISSUE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CERTIFICATE_URL = "AAAAAAAAAA";
    private static final String UPDATED_CERTIFICATE_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/certificates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCertificateMockMvc;

    private Certificate certificate;

    private Certificate insertedCertificate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Certificate createEntity() {
        return new Certificate().issueDate(DEFAULT_ISSUE_DATE).certificateUrl(DEFAULT_CERTIFICATE_URL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Certificate createUpdatedEntity() {
        return new Certificate().issueDate(UPDATED_ISSUE_DATE).certificateUrl(UPDATED_CERTIFICATE_URL);
    }

    @BeforeEach
    public void initTest() {
        certificate = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCertificate != null) {
            certificateRepository.delete(insertedCertificate);
            insertedCertificate = null;
        }
    }

    @Test
    @Transactional
    void createCertificate() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Certificate
        var returnedCertificate = om.readValue(
            restCertificateMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificate)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Certificate.class
        );

        // Validate the Certificate in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCertificateUpdatableFieldsEquals(returnedCertificate, getPersistedCertificate(returnedCertificate));

        insertedCertificate = returnedCertificate;
    }

    @Test
    @Transactional
    void createCertificateWithExistingId() throws Exception {
        // Create the Certificate with an existing ID
        certificate.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCertificateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificate)))
            .andExpect(status().isBadRequest());

        // Validate the Certificate in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIssueDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        certificate.setIssueDate(null);

        // Create the Certificate, which fails.

        restCertificateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificate)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCertificates() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList
        restCertificateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(certificate.getId().intValue())))
            .andExpect(jsonPath("$.[*].issueDate").value(hasItem(DEFAULT_ISSUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].certificateUrl").value(hasItem(DEFAULT_CERTIFICATE_URL)));
    }

    @Test
    @Transactional
    void getCertificate() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        // Get the certificate
        restCertificateMockMvc
            .perform(get(ENTITY_API_URL_ID, certificate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(certificate.getId().intValue()))
            .andExpect(jsonPath("$.issueDate").value(DEFAULT_ISSUE_DATE.toString()))
            .andExpect(jsonPath("$.certificateUrl").value(DEFAULT_CERTIFICATE_URL));
    }

    @Test
    @Transactional
    void getNonExistingCertificate() throws Exception {
        // Get the certificate
        restCertificateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCertificate() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certificate
        Certificate updatedCertificate = certificateRepository.findById(certificate.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCertificate are not directly saved in db
        em.detach(updatedCertificate);
        updatedCertificate.issueDate(UPDATED_ISSUE_DATE).certificateUrl(UPDATED_CERTIFICATE_URL);

        restCertificateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCertificate.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCertificate))
            )
            .andExpect(status().isOk());

        // Validate the Certificate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCertificateToMatchAllProperties(updatedCertificate);
    }

    @Test
    @Transactional
    void putNonExistingCertificate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificate.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, certificate.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(certificate))
            )
            .andExpect(status().isBadRequest());

        // Validate the Certificate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCertificate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificate.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(certificate))
            )
            .andExpect(status().isBadRequest());

        // Validate the Certificate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCertificate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificate.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificate)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Certificate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCertificateWithPatch() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certificate using partial update
        Certificate partialUpdatedCertificate = new Certificate();
        partialUpdatedCertificate.setId(certificate.getId());

        partialUpdatedCertificate.certificateUrl(UPDATED_CERTIFICATE_URL);

        restCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCertificate.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCertificate))
            )
            .andExpect(status().isOk());

        // Validate the Certificate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCertificateUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCertificate, certificate),
            getPersistedCertificate(certificate)
        );
    }

    @Test
    @Transactional
    void fullUpdateCertificateWithPatch() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certificate using partial update
        Certificate partialUpdatedCertificate = new Certificate();
        partialUpdatedCertificate.setId(certificate.getId());

        partialUpdatedCertificate.issueDate(UPDATED_ISSUE_DATE).certificateUrl(UPDATED_CERTIFICATE_URL);

        restCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCertificate.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCertificate))
            )
            .andExpect(status().isOk());

        // Validate the Certificate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCertificateUpdatableFieldsEquals(partialUpdatedCertificate, getPersistedCertificate(partialUpdatedCertificate));
    }

    @Test
    @Transactional
    void patchNonExistingCertificate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificate.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, certificate.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(certificate))
            )
            .andExpect(status().isBadRequest());

        // Validate the Certificate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCertificate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificate.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(certificate))
            )
            .andExpect(status().isBadRequest());

        // Validate the Certificate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCertificate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificate.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(certificate)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Certificate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCertificate() throws Exception {
        // Initialize the database
        insertedCertificate = certificateRepository.saveAndFlush(certificate);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the certificate
        restCertificateMockMvc
            .perform(delete(ENTITY_API_URL_ID, certificate.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return certificateRepository.count();
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

    protected Certificate getPersistedCertificate(Certificate certificate) {
        return certificateRepository.findById(certificate.getId()).orElseThrow();
    }

    protected void assertPersistedCertificateToMatchAllProperties(Certificate expectedCertificate) {
        assertCertificateAllPropertiesEquals(expectedCertificate, getPersistedCertificate(expectedCertificate));
    }

    protected void assertPersistedCertificateToMatchUpdatableProperties(Certificate expectedCertificate) {
        assertCertificateAllUpdatablePropertiesEquals(expectedCertificate, getPersistedCertificate(expectedCertificate));
    }
}
