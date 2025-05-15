package com.example.course.web.rest;

import com.example.course.domain.Certificate;
import com.example.course.repository.CertificateRepository;
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
 * REST controller for managing {@link com.example.course.domain.Certificate}.
 */
@RestController
@RequestMapping("/api/certificates")
@Transactional
public class CertificateResource {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateResource.class);

    private static final String ENTITY_NAME = "certificate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CertificateRepository certificateRepository;

    public CertificateResource(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    /**
     * {@code POST  /certificates} : Create a new certificate.
     *
     * @param certificate the certificate to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new certificate, or with status {@code 400 (Bad Request)} if the certificate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Certificate> createCertificate(@Valid @RequestBody Certificate certificate) throws URISyntaxException {
        LOG.debug("REST request to save Certificate : {}", certificate);
        if (certificate.getId() != null) {
            throw new BadRequestAlertException("A new certificate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        certificate = certificateRepository.save(certificate);
        return ResponseEntity.created(new URI("/api/certificates/" + certificate.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, certificate.getId().toString()))
            .body(certificate);
    }

    /**
     * {@code PUT  /certificates/:id} : Updates an existing certificate.
     *
     * @param id the id of the certificate to save.
     * @param certificate the certificate to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated certificate,
     * or with status {@code 400 (Bad Request)} if the certificate is not valid,
     * or with status {@code 500 (Internal Server Error)} if the certificate couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Certificate> updateCertificate(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Certificate certificate
    ) throws URISyntaxException {
        LOG.debug("REST request to update Certificate : {}, {}", id, certificate);
        if (certificate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, certificate.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!certificateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        certificate = certificateRepository.save(certificate);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, certificate.getId().toString()))
            .body(certificate);
    }

    /**
     * {@code PATCH  /certificates/:id} : Partial updates given fields of an existing certificate, field will ignore if it is null
     *
     * @param id the id of the certificate to save.
     * @param certificate the certificate to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated certificate,
     * or with status {@code 400 (Bad Request)} if the certificate is not valid,
     * or with status {@code 404 (Not Found)} if the certificate is not found,
     * or with status {@code 500 (Internal Server Error)} if the certificate couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Certificate> partialUpdateCertificate(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Certificate certificate
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Certificate partially : {}, {}", id, certificate);
        if (certificate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, certificate.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!certificateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Certificate> result = certificateRepository
            .findById(certificate.getId())
            .map(existingCertificate -> {
                if (certificate.getIssueDate() != null) {
                    existingCertificate.setIssueDate(certificate.getIssueDate());
                }
                if (certificate.getCertificateUrl() != null) {
                    existingCertificate.setCertificateUrl(certificate.getCertificateUrl());
                }

                return existingCertificate;
            })
            .map(certificateRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, certificate.getId().toString())
        );
    }

    /**
     * {@code GET  /certificates} : get all the certificates.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of certificates in body.
     */
    @GetMapping("")
    public List<Certificate> getAllCertificates() {
        LOG.debug("REST request to get all Certificates");
        return certificateRepository.findAll();
    }

    /**
     * {@code GET  /certificates/:id} : get the "id" certificate.
     *
     * @param id the id of the certificate to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the certificate, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getCertificate(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Certificate : {}", id);
        Optional<Certificate> certificate = certificateRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(certificate);
    }

    /**
     * {@code DELETE  /certificates/:id} : delete the "id" certificate.
     *
     * @param id the id of the certificate to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Certificate : {}", id);
        certificateRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
