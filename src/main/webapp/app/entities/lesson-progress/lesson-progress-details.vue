<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="lessonProgress">
        <h2 class="jh-entity-heading" data-cy="lessonProgressDetailsHeading">
          <span v-text="t$('onlineCoursePlatformApp.lessonProgress.detail.title')"></span> {{ lessonProgress.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.lessonProgress.viewed')"></span>
          </dt>
          <dd>
            <span>{{ lessonProgress.viewed }}</span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.lessonProgress.viewedDate')"></span>
          </dt>
          <dd>
            <span v-if="lessonProgress.viewedDate">{{ formatDateLong(lessonProgress.viewedDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.lessonProgress.student')"></span>
          </dt>
          <dd>
            {{ lessonProgress.student ? lessonProgress.student.username : '' }}
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.lessonProgress.courseItem')"></span>
          </dt>
          <dd>
            <div v-if="lessonProgress.courseItem">
              <router-link :to="{ name: 'CourseItemView', params: { courseItemId: lessonProgress.courseItem.id } }">{{
                lessonProgress.courseItem.title
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="lessonProgress.id"
          :to="{ name: 'LessonProgressEdit', params: { lessonProgressId: lessonProgress.id } }"
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

<script lang="ts" src="./lesson-progress-details.component.ts"></script>
