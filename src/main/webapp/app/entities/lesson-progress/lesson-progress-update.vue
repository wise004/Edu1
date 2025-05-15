<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="onlineCoursePlatformApp.lessonProgress.home.createOrEditLabel"
          data-cy="LessonProgressCreateUpdateHeading"
          v-text="t$('onlineCoursePlatformApp.lessonProgress.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="lessonProgress.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="lessonProgress.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('onlineCoursePlatformApp.lessonProgress.viewed')"
              for="lesson-progress-viewed"
            ></label>
            <input
              type="checkbox"
              class="form-check"
              name="viewed"
              id="lesson-progress-viewed"
              data-cy="viewed"
              :class="{ valid: !v$.viewed.$invalid, invalid: v$.viewed.$invalid }"
              v-model="v$.viewed.$model"
              required
            />
            <div v-if="v$.viewed.$anyDirty && v$.viewed.$invalid">
              <small class="form-text text-danger" v-for="error of v$.viewed.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('onlineCoursePlatformApp.lessonProgress.viewedDate')"
              for="lesson-progress-viewedDate"
            ></label>
            <div class="d-flex">
              <input
                id="lesson-progress-viewedDate"
                data-cy="viewedDate"
                type="datetime-local"
                class="form-control"
                name="viewedDate"
                :class="{ valid: !v$.viewedDate.$invalid, invalid: v$.viewedDate.$invalid }"
                :value="convertDateTimeFromServer(v$.viewedDate.$model)"
                @change="updateInstantField('viewedDate', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('onlineCoursePlatformApp.lessonProgress.student')"
              for="lesson-progress-student"
            ></label>
            <select class="form-control" id="lesson-progress-student" data-cy="student" name="student" v-model="lessonProgress.student">
              <option :value="null"></option>
              <option
                :value="lessonProgress.student && userOption.id === lessonProgress.student.id ? lessonProgress.student : userOption"
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
              v-text="t$('onlineCoursePlatformApp.lessonProgress.courseItem')"
              for="lesson-progress-courseItem"
            ></label>
            <select
              class="form-control"
              id="lesson-progress-courseItem"
              data-cy="courseItem"
              name="courseItem"
              v-model="lessonProgress.courseItem"
            >
              <option :value="null"></option>
              <option
                :value="
                  lessonProgress.courseItem && courseItemOption.id === lessonProgress.courseItem.id
                    ? lessonProgress.courseItem
                    : courseItemOption
                "
                v-for="courseItemOption in courseItems"
                :key="courseItemOption.id"
              >
                {{ courseItemOption.title }}
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
<script lang="ts" src="./lesson-progress-update.component.ts"></script>
