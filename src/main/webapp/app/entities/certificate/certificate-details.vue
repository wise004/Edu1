<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="certificate">
        <h2 class="jh-entity-heading" data-cy="certificateDetailsHeading">
          <span v-text="t$('onlineCoursePlatformApp.certificate.detail.title')"></span> {{ certificate.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.certificate.issueDate')"></span>
          </dt>
          <dd>
            <span v-if="certificate.issueDate">{{ formatDateLong(certificate.issueDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.certificate.certificateUrl')"></span>
          </dt>
          <dd>
            <span>{{ certificate.certificateUrl }}</span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.certificate.courseProgress')"></span>
          </dt>
          <dd>
            <div v-if="certificate.courseProgress">
              <router-link :to="{ name: 'CourseProgressView', params: { courseProgressId: certificate.courseProgress.id } }">{{
                certificate.courseProgress.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="certificate.id"
          :to="{ name: 'CertificateEdit', params: { certificateId: certificate.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./certificate-details.component.ts"></script>
