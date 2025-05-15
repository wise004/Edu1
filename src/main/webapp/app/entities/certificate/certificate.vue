<template>
  <div>
    <h2 id="page-heading" data-cy="CertificateHeading">
      <span v-text="t$('onlineCoursePlatformApp.certificate.home.title')" id="certificate-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('onlineCoursePlatformApp.certificate.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'CertificateCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-certificate"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('onlineCoursePlatformApp.certificate.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && certificates && certificates.length === 0">
      <span v-text="t$('onlineCoursePlatformApp.certificate.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="certificates && certificates.length > 0">
      <table class="table table-striped" aria-describedby="certificates">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.certificate.issueDate')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.certificate.certificateUrl')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.certificate.courseProgress')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="certificate in certificates" :key="certificate.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'CertificateView', params: { certificateId: certificate.id } }">{{ certificate.id }}</router-link>
            </td>
            <td>{{ formatDateShort(certificate.issueDate) || '' }}</td>
            <td>{{ certificate.certificateUrl }}</td>
            <td>
              <div v-if="certificate.courseProgress">
                <router-link :to="{ name: 'CourseProgressView', params: { courseProgressId: certificate.courseProgress.id } }">{{
                  certificate.courseProgress.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'CertificateView', params: { certificateId: certificate.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'CertificateEdit', params: { certificateId: certificate.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(certificate)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span
          id="onlineCoursePlatformApp.certificate.delete.question"
          data-cy="certificateDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-certificate-heading" v-text="t$('onlineCoursePlatformApp.certificate.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-certificate"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeCertificate()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./certificate.component.ts"></script>
