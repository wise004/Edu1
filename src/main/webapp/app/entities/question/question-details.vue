<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="question">
        <h2 class="jh-entity-heading" data-cy="questionDetailsHeading">
          <span v-text="t$('onlineCoursePlatformApp.question.detail.title')"></span> {{ question.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.question.text')"></span>
          </dt>
          <dd>
            <span>{{ question.text }}</span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.question.type')"></span>
          </dt>
          <dd>
            <span v-text="t$('onlineCoursePlatformApp.QuestionType.' + question.type)"></span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.question.options')"></span>
          </dt>
          <dd>
            <span>{{ question.options }}</span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.question.courseItem')"></span>
          </dt>
          <dd>
            <div v-if="question.courseItem">
              <router-link :to="{ name: 'CourseItemView', params: { courseItemId: question.courseItem.id } }">{{
                question.courseItem.title
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="question.id" :to="{ name: 'QuestionEdit', params: { questionId: question.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./question-details.component.ts"></script>
