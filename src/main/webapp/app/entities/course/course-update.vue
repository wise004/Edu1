<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="onlineCoursePlatformApp.course.home.createOrEditLabel"
          data-cy="CourseCreateUpdateHeading"
          v-text="t$('onlineCoursePlatformApp.course.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="course.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="course.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('onlineCoursePlatformApp.course.title')" for="course-title"></label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="course-title"
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
            <label class="form-control-label" v-text="t$('onlineCoursePlatformApp.course.description')" for="course-description"></label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="course-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('onlineCoursePlatformApp.course.author')" for="course-author"></label>
            <select class="form-control" id="course-author" data-cy="author" name="author" v-model="course.author">
              <option :value="null"></option>
              <option
                :value="course.author && userOption.id === course.author.id ? course.author : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.username }}
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
<script lang="ts" src="./course-update.component.ts"></script>
