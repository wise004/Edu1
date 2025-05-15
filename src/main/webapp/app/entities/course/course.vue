<template>
  <div>
    <h2 id="page-heading" data-cy="CourseHeading">
      <span v-text="t$('onlineCoursePlatformApp.course.home.title')" id="course-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('onlineCoursePlatformApp.course.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'CourseCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-course"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('onlineCoursePlatformApp.course.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && courses && courses.length === 0">
      <span v-text="t$('onlineCoursePlatformApp.course.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="courses && courses.length > 0">
      <table class="table table-striped" aria-describedby="courses">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.course.title')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.course.description')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.course.author')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="course in courses" :key="course.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'CourseView', params: { courseId: course.id } }">{{ course.id }}</router-link>
            </td>
            <td>{{ course.title }}</td>
            <td>{{ course.description }}</td>
            <td>
              {{ course.author ? course.author.username : '' }}
            </td>
            <td class="text-right">
              <div class="btn-group">
                <!-- Boshlash button -->
                <router-link :to="{ name: 'CourseItems', params: { courseId: course.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-success btn-sm">
                    <font-awesome-icon icon="play"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.start')"></span>
                  </button>
                </router-link>

                <!-- View button -->
                <router-link :to="{ name: 'CourseView', params: { courseId: course.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>

                <!-- Edit button -->
                <router-link :to="{ name: 'CourseEdit', params: { courseId: course.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>

                <!-- Delete button -->
                <b-button
                  @click="prepareRemove(course)"
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
          id="onlineCoursePlatformApp.course.delete.question"
          data-cy="courseDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-course-heading" v-text="t$('onlineCoursePlatformApp.course.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-course"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeCourse()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./course.component.ts"></script>
