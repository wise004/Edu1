<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="course">
        <h2 class="jh-entity-heading" data-cy="courseDetailsHeading">
          <span v-text="t$('onlineCoursePlatformApp.course.detail.title')"></span> {{ course.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.course.title')"></span>
          </dt>
          <dd>
            <span>{{ course.title }}</span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.course.description')"></span>
          </dt>
          <dd>
            <span>{{ course.description }}</span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.course.author')"></span>
          </dt>
          <dd>
            {{ course.author ? course.author.username : '' }}
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="course.id" :to="{ name: 'CourseEdit', params: { courseId: course.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./course-details.component.ts"></script>
