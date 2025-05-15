<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="onlineCoursePlatformApp.courseProgress.home.createOrEditLabel"
          data-cy="CourseProgressCreateUpdateHeading"
          v-text="t$('onlineCoursePlatformApp.courseProgress.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="courseProgress.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="courseProgress.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('onlineCoursePlatformApp.courseProgress.completedItems')"
              for="course-progress-completedItems"
            ></label>
            <input
              type="number"
              class="form-control"
              name="completedItems"
              id="course-progress-completedItems"
              data-cy="completedItems"
              :class="{ valid: !v$.completedItems.$invalid, invalid: v$.completedItems.$invalid }"
              v-model.number="v$.completedItems.$model"
            />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('onlineCoursePlatformApp.courseProgress.isCompleted')"
              for="course-progress-isCompleted"
            ></label>
            <input
              type="checkbox"
              class="form-check"
              name="isCompleted"
              id="course-progress-isCompleted"
              data-cy="isCompleted"
              :class="{ valid: !v$.isCompleted.$invalid, invalid: v$.isCompleted.$invalid }"
              v-model="v$.isCompleted.$model"
            />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('onlineCoursePlatformApp.courseProgress.student')"
              for="course-progress-student"
            ></label>
            <select class="form-control" id="course-progress-student" data-cy="student" name="student" v-model="courseProgress.student">
              <option :value="null"></option>
              <option
                :value="courseProgress.student && userOption.id === courseProgress.student.id ? courseProgress.student : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.username }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('onlineCoursePlatformApp.courseProgress.course')"
              for="course-progress-course"
            ></label>
            <select class="form-control" id="course-progress-course" data-cy="course" name="course" v-model="courseProgress.course">
              <option :value="null"></option>
              <option
                :value="courseProgress.course && courseOption.id === courseProgress.course.id ? courseProgress.course : courseOption"
                v-for="courseOption in courses"
                :key="courseOption.id"
              >
                {{ courseOption.title }}
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
<script lang="ts" src="./course-progress-update.component.ts"></script>
