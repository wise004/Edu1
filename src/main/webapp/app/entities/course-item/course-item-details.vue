<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="courseItem">
        <h2 class="jh-entity-heading" data-cy="courseItemDetailsHeading">
          <span v-text="t$('onlineCoursePlatformApp.courseItem.detail.title')"></span> {{ courseItem.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.courseItem.title')"></span>
          </dt>
          <dd>
            <span>{{ courseItem.title }}</span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.courseItem.itemType')"></span>
          </dt>
          <dd>
            <span v-text="t$('onlineCoursePlatformApp.ItemType.' + courseItem.itemType)"></span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.courseItem.contentType')"></span>
          </dt>
          <dd>
            <span v-text="t$('onlineCoursePlatformApp.ContentType.' + courseItem.contentType)"></span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.courseItem.content')"></span>
          </dt>
          <dd>
            <span>{{ courseItem.content }}</span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.courseItem.passingScore')"></span>
          </dt>
          <dd>
            <span>{{ courseItem.passingScore }}</span>
          </dd>
          <dt>
            <span v-text="t$('onlineCoursePlatformApp.courseItem.course')"></span>
          </dt>
          <dd>
            <div v-if="courseItem.course">
              <router-link :to="{ name: 'CourseView', params: { courseId: courseItem.course.id } }">{{
                courseItem.course.title
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="courseItem.id"
          :to="{ name: 'CourseItemEdit', params: { courseItemId: courseItem.id } }"
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

<script lang="ts" src="./course-item-details.component.ts"></script>
