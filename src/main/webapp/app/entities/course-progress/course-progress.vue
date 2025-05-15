<template>
  <div>
    <h2 id="page-heading" data-cy="CourseProgressHeading">
      <span v-text="t$('onlineCoursePlatformApp.courseProgress.home.title')" id="course-progress-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('onlineCoursePlatformApp.courseProgress.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'CourseProgressCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-course-progress"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('onlineCoursePlatformApp.courseProgress.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && courseProgresses && courseProgresses.length === 0">
      <span v-text="t$('onlineCoursePlatformApp.courseProgress.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="courseProgresses && courseProgresses.length > 0">
      <table class="table table-striped" aria-describedby="courseProgresses">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.courseProgress.completedItems')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.courseProgress.isCompleted')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.courseProgress.student')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.courseProgress.course')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="courseProgress in courseProgresses" :key="courseProgress.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'CourseProgressView', params: { courseProgressId: courseProgress.id } }">{{
                courseProgress.id
              }}</router-link>
            </td>
            <td>{{ courseProgress.completedItems }}</td>
            <td>{{ courseProgress.isCompleted }}</td>
            <td>
              {{ courseProgress.student ? courseProgress.student.username : '' }}
            </td>
            <td>
              <div v-if="courseProgress.course">
                <router-link :to="{ name: 'CourseView', params: { courseId: courseProgress.course.id } }">{{
                  courseProgress.course.title
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'CourseProgressView', params: { courseProgressId: courseProgress.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'CourseProgressEdit', params: { courseProgressId: courseProgress.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(courseProgress)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span
          id="onlineCoursePlatformApp.courseProgress.delete.question"
          data-cy="courseProgressDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p
          id="jhi-delete-courseProgress-heading"
          v-text="t$('onlineCoursePlatformApp.courseProgress.delete.question', { id: removeId })"
        ></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-courseProgress"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeCourseProgress()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./course-progress.component.ts"></script>
