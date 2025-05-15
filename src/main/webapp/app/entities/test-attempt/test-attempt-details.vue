<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="testAttempt">
        <h2 class="jh-entity-heading" data-cy="testAttemptDetailsHeading">
          <span v-text="t$('onlineCoursePlatformApp.testAttempt.detail.title')"></span> {{ testAttempt.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.testAttempt.score')"></span>
          </dt>
          <dd>
            <span>{{ testAttempt.score }}</span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.testAttempt.passed')"></span>
          </dt>
          <dd>
            <span>{{ testAttempt.passed }}</span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.testAttempt.attemptDate')"></span>
          </dt>
          <dd>
            <span v-if="testAttempt.attemptDate">{{ formatDateLong(testAttempt.attemptDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.testAttempt.student')"></span>
          </dt>
          <dd>
            {{ testAttempt.student ? testAttempt.student.username : '' }}
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.testAttempt.courseItem')"></span>
          </dt>
          <dd>
            <div v-if="testAttempt.courseItem">
              <router-link :to="{ name: 'CourseItemView', params: { courseItemId: testAttempt.courseItem.id } }">{{
                testAttempt.courseItem.title
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="testAttempt.id"
          :to="{ name: 'TestAttemptEdit', params: { testAttemptId: testAttempt.id } }"
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

<script lang="ts" src="./test-attempt-details.component.ts"></script>
