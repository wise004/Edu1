<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="onlineCoursePlatformApp.testAttempt.home.createOrEditLabel"
          data-cy="TestAttemptCreateUpdateHeading"
          v-text="t$('onlineCoursePlatformApp.testAttempt.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="testAttempt.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="testAttempt.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('onlineCoursePlatformApp.testAttempt.score')" for="test-attempt-score"></label>
            <input
              type="number"
              class="form-control"
              name="score"
              id="test-attempt-score"
              data-cy="score"
              :class="{ valid: !v$.score.$invalid, invalid: v$.score.$invalid }"
              v-model.number="v$.score.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('onlineCoursePlatformApp.testAttempt.passed')" for="test-attempt-passed"></label>
            <input
              type="checkbox"
              class="form-check"
              name="passed"
              id="test-attempt-passed"
              data-cy="passed"
              :class="{ valid: !v$.passed.$invalid, invalid: v$.passed.$invalid }"
              v-model="v$.passed.$model"
            />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('onlineCoursePlatformApp.testAttempt.attemptDate')"
              for="test-attempt-attemptDate"
            ></label>
            <div class="d-flex">
              <input
                id="test-attempt-attemptDate"
                data-cy="attemptDate"
                type="datetime-local"
                class="form-control"
                name="attemptDate"
                :class="{ valid: !v$.attemptDate.$invalid, invalid: v$.attemptDate.$invalid }"
                :value="convertDateTimeFromServer(v$.attemptDate.$model)"
                @change="updateInstantField('attemptDate', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('onlineCoursePlatformApp.testAttempt.student')" for="test-attempt-student"></label>
            <select class="form-control" id="test-attempt-student" data-cy="student" name="student" v-model="testAttempt.student">
              <option :value="null"></option>
              <option
                :value="testAttempt.student && userOption.id === testAttempt.student.id ? testAttempt.student : userOption"
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
              v-text="t$('onlineCoursePlatformApp.testAttempt.courseItem')"
              for="test-attempt-courseItem"
            ></label>
            <select
              class="form-control"
              id="test-attempt-courseItem"
              data-cy="courseItem"
              name="courseItem"
              v-model="testAttempt.courseItem"
            >
              <option :value="null"></option>
              <option
                :value="
                  testAttempt.courseItem && courseItemOption.id === testAttempt.courseItem.id ? testAttempt.courseItem : courseItemOption
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
<script lang="ts" src="./test-attempt-update.component.ts"></script>
