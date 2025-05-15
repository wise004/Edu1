<template>
  <div>
    <h2 id="page-heading" data-cy="CourseItemHeading">
      <span v-text="t$('onlineCoursePlatformApp.courseItem.home.title')" id="course-item-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('onlineCoursePlatformApp.courseItem.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'CourseItemCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-course-item"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('onlineCoursePlatformApp.courseItem.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && courseItems && courseItems.length === 0">
      <span v-text="t$('onlineCoursePlatformApp.courseItem.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="courseItems && courseItems.length > 0">
      <table class="table table-striped" aria-describedby="courseItems">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.courseItem.title')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.courseItem.itemType')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.courseItem.contentType')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.courseItem.content')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.courseItem.passingScore')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.courseItem.course')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="courseItem in courseItems" :key="courseItem.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'CourseItemView', params: { courseItemId: courseItem.id } }">{{ courseItem.id }}</router-link>
            </td>
            <td>{{ courseItem.title }}</td>
            <td v-text="t$('onlineCoursePlatformApp.ItemType.' + courseItem.itemType)"></td>
            <td v-text="t$('onlineCoursePlatformApp.ContentType.' + courseItem.contentType)"></td>
            <td>{{ courseItem.content }}</td>
            <td>{{ courseItem.passingScore }}</td>
            <td>
              <div v-if="courseItem.course">
                <router-link :to="{ name: 'CourseView', params: { courseId: courseItem.course.id } }">{{
                  courseItem.course.title
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'CourseItemView', params: { courseItemId: courseItem.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'CourseItemEdit', params: { courseItemId: courseItem.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(courseItem)"
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
          id="onlineCoursePlatformApp.courseItem.delete.question"
          data-cy="courseItemDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-courseItem-heading" v-text="t$('onlineCoursePlatformApp.courseItem.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-courseItem"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeCourseItem()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./course-item.component.ts"></script>
