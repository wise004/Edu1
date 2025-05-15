<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="onlineCoursePlatformApp.certificate.home.createOrEditLabel"
          data-cy="CertificateCreateUpdateHeading"
          v-text="t$('onlineCoursePlatformApp.certificate.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="certificate.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="certificate.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('onlineCoursePlatformApp.certificate.issueDate')"
              for="certificate-issueDate"
            ></label>
            <div class="d-flex">
              <input
                id="certificate-issueDate"
                data-cy="issueDate"
                type="datetime-local"
                class="form-control"
                name="issueDate"
                :class="{ valid: !v$.issueDate.$invalid, invalid: v$.issueDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.issueDate.$model)"
                @change="updateInstantField('issueDate', $event)"
              />
            </div>
            <div v-if="v$.issueDate.$anyDirty && v$.issueDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.issueDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('onlineCoursePlatformApp.certificate.certificateUrl')"
              for="certificate-certificateUrl"
            ></label>
            <input
              type="text"
              class="form-control"
              name="certificateUrl"
              id="certificate-certificateUrl"
              data-cy="certificateUrl"
              :class="{ valid: !v$.certificateUrl.$invalid, invalid: v$.certificateUrl.$invalid }"
              v-model="v$.certificateUrl.$model"
            />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('onlineCoursePlatformApp.certificate.courseProgress')"
              for="certificate-courseProgress"
            ></label>
            <select
              class="form-control"
              id="certificate-courseProgress"
              data-cy="courseProgress"
              name="courseProgress"
              v-model="certificate.courseProgress"
            >
              <option :value="null"></option>
              <option
                :value="
                  certificate.courseProgress && courseProgressOption.id === certificate.courseProgress.id
                    ? certificate.courseProgress
                    : courseProgressOption
                "
                v-for="courseProgressOption in courseProgresses"
                :key="courseProgressOption.id"
              >
                {{ courseProgressOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./certificate-update.component.ts"></script>
