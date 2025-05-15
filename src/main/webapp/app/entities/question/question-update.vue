<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="onlineCoursePlatformApp.question.home.createOrEditLabel"
          data-cy="QuestionCreateUpdateHeading"
          v-text="t$('onlineCoursePlatformApp.question.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="question.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="question.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('onlineCoursePlatformApp.question.text')" for="question-text"></label>
            <input
              type="text"
              class="form-control"
              name="text"
              id="question-text"
              data-cy="text"
              :class="{ valid: !v$.text.$invalid, invalid: v$.text.$invalid }"
              v-model="v$.text.$model"
              required
            />
            <div v-if="v$.text.$anyDirty && v$.text.$invalid">
              <small class="form-text text-danger" v-for="error of v$.text.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('onlineCoursePlatformApp.question.type')" for="question-type"></label>
            <select
              class="form-control"
              name="type"
              :class="{ valid: !v$.type.$invalid, invalid: v$.type.$invalid }"
              v-model="v$.type.$model"
              id="question-type"
              data-cy="type"
              required
            >
              <option
                v-for="questionType in questionTypeValues"
                :key="questionType"
                :value="questionType"
                :label="t$('onlineCoursePlatformApp.QuestionType.' + questionType)"
              >
                {{ questionType }}
              </option>
            </select>
            <div v-if="v$.type.$anyDirty && v$.type.$invalid">
              <small class="form-text text-danger" v-for="error of v$.type.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('onlineCoursePlatformApp.question.options')" for="question-options"></label>
            <input
              type="text"
              class="form-control"
              name="options"
              id="question-options"
              data-cy="options"
              :class="{ valid: !v$.options.$invalid, invalid: v$.options.$invalid }"
              v-model="v$.options.$model"
              required
            />
            <div v-if="v$.options.$anyDirty && v$.options.$invalid">
              <small class="form-text text-danger" v-for="error of v$.options.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('onlineCoursePlatformApp.question.courseItem')" for="question-courseItem"></label>
            <select class="form-control" id="question-courseItem" data-cy="courseItem" name="courseItem" v-model="question.courseItem">
              <option :value="null"></option>
              <option
                :value="question.courseItem && courseItemOption.id === question.courseItem.id ? question.courseItem : courseItemOption"
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
<script lang="ts" src="./question-update.component.ts"></script>
