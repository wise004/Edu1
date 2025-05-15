<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save">
        <h2
          id="onlineCoursePlatformApp.courseItem.home.createOrEditLabel"
          data-cy="CourseItemCreateUpdateHeading"
          v-text="t$('onlineCoursePlatformApp.courseItem.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="courseItem.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="courseItem.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('onlineCoursePlatformApp.courseItem.title')" for="course-item-title"></label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="course-item-title"
              data-cy="title"
              :class="{ valid: !v$.title.$invalid, invalid: v$.title.$invalid }"
              v-model="v$.title.$model"
              required
            />
            <div v-if="v$.title.$anyDirty && v$.title.$invalid">
              <small class="form-text text-danger" v-for="error of v$.title.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('onlineCoursePlatformApp.courseItem.itemType')" for="course-item-itemType"></label>
            <select
              class="form-control"
              name="itemType"
              :class="{ valid: !v$.itemType.$invalid, invalid: v$.itemType.$invalid }"
              v-model="v$.itemType.$model"
              id="course-item-itemType"
              data-cy="itemType"
              required
            >
              <option
                v-for="itemType in itemTypeValues"
                :key="itemType"
                :value="itemType"
                :label="t$('onlineCoursePlatformApp.ItemType.' + itemType)"
              >
                {{ itemType }}
              </option>
            </select>
            <div v-if="v$.itemType.$anyDirty && v$.itemType.$invalid">
              <small class="form-text text-danger" v-for="error of v$.itemType.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('onlineCoursePlatformApp.courseItem.contentType')"
              for="course-item-contentType"
            ></label>
            <select
              class="form-control"
              name="contentType"
              :class="{ valid: !v$.contentType.$invalid, invalid: v$.contentType.$invalid }"
              v-model="v$.contentType.$model"
              id="course-item-contentType"
              data-cy="contentType"
            >
              <option
                v-for="contentType in contentTypeValues"
                :key="contentType"
                :value="contentType"
                :label="t$('onlineCoursePlatformApp.ContentType.' + contentType)"
              >
                {{ contentType }}
              </option>
            </select>
          </div>

          <!-- Dinamik shakl maydoni: -->
          <div v-if="v$.contentType.$model === 'UPLOADED_VIDEO'" class="form-group">
            <label for="videoFile">Video fayl</label>
            <input type="file" class="form-control" id="videoFile" @change="handleFileUpload" accept="video/*" />
          </div>
          <div v-else-if="v$.contentType.$model === 'YOUTUBE_VIDEO'" class="form-group">
            <label class="form-control-label" v-text="t$('onlineCoursePlatformApp.courseItem.content')" for="course-item-content"></label>
            <input
              type="text"
              class="form-control"
              name="content"
              id="course-item-content"
              data-cy="content"
              :class="{ valid: !v$.content.$invalid, invalid: v$.content.$invalid }"
              v-model="v$.content.$model"
            />
          </div>
          <div v-else-if="v$.contentType.$model === 'TEXT'" class="form-group">
            <label class="form-control-label" v-text="t$('onlineCoursePlatformApp.courseItem.content')" for="course-item-content"></label>
            <textarea
              class="form-control"
              name="content"
              id="course-item-content"
              data-cy="content"
              :class="{ valid: !v$.content.$invalid, invalid: v$.content.$invalid }"
              v-model="v$.content.$model"
              rows="4"
            ></textarea>
          </div>

          <div class="form-group" v-if="v$.itemType.$model === 'TEST'">
            <label
              class="form-control-label"
              v-text="t$('onlineCoursePlatformApp.courseItem.passingScore')"
              for="course-item-passingScore"
            ></label>
            <input
              type="number"
              class="form-control"
              name="passingScore"
              id="course-item-passingScore"
              data-cy="passingScore"
              :class="{ valid: !v$.passingScore.$invalid, invalid: v$.passingScore.$invalid }"
              v-model.number="v$.passingScore.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('onlineCoursePlatformApp.courseItem.course')" for="course-item-course"></label>
            <select class="form-control" id="course-item-course" data-cy="course" name="course" v-model="courseItem.course">
              <option :value="null"></option>
              <option
                :value="courseItem.course && courseOption.id === courseItem.course.id ? courseItem.course : courseOption"
                v-for="courseOption in courses"
                :key="courseOption.id"
              >
                {{ courseOption.title }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState">
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

<script lang="ts" src="./course-item-update.component.ts"></script>
