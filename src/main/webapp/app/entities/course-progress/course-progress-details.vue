<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="courseProgress">
        <h2 class="jh-entity-heading" data-cy="courseProgressDetailsHeading">
          <span v-text="t$('onlineCoursePlatformApp.courseProgress.detail.title')"></span> {{ courseProgress.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.courseProgress.completedItems')"></span>
          </dt>
          <dd>
            <span>{{ courseProgress.completedItems }}</span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.courseProgress.isCompleted')"></span>
          </dt>
          <dd>
            <span>{{ courseProgress.isCompleted }}</span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.courseProgress.student')"></span>
          </dt>
          <dd>
            {{ courseProgress.student ? courseProgress.student.username : '' }}
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.courseProgress.course')"></span>
          </dt>
          <dd>
            <div v-if="courseProgress.course">
              <router-link :to="{ name: 'CourseView', params: { courseId: courseProgress.course.id } }">{{
                courseProgress.course.title
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="courseProgress.id"
          :to="{ name: 'CourseProgressEdit', params: { courseProgressId: courseProgress.id } }"
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

<script lang="ts" src="./course-progress-details.component.ts"></script>
