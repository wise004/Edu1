<template>
  <div>
    <h2 id="page-heading" data-cy="LessonProgressHeading">
      <span v-text="t$('onlineCoursePlatformApp.lessonProgress.home.title')" id="lesson-progress-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('onlineCoursePlatformApp.lessonProgress.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'LessonProgressCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-lesson-progress"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('onlineCoursePlatformApp.lessonProgress.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && lessonProgresses && lessonProgresses.length === 0">
      <span v-text="t$('onlineCoursePlatformApp.lessonProgress.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="lessonProgresses && lessonProgresses.length > 0">
      <table class="table table-striped" aria-describedby="lessonProgresses">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.lessonProgress.viewed')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.lessonProgress.viewedDate')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.lessonProgress.student')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.lessonProgress.courseItem')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="lessonProgress in lessonProgresses" :key="lessonProgress.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'LessonProgressView', params: { lessonProgressId: lessonProgress.id } }">{{
                lessonProgress.id
              }}</router-link>
            </td>
            <td>{{ lessonProgress.viewed }}</td>
            <td>{{ formatDateShort(lessonProgress.viewedDate) || '' }}</td>
            <td>
              {{ lessonProgress.student ? lessonProgress.student.username : '' }}
            </td>
            <td>
              <div v-if="lessonProgress.courseItem">
                <router-link :to="{ name: 'CourseItemView', params: { courseItemId: lessonProgress.courseItem.id } }">{{
                  lessonProgress.courseItem.title
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'LessonProgressView', params: { lessonProgressId: lessonProgress.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'LessonProgressEdit', params: { lessonProgressId: lessonProgress.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(lessonProgress)"
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
          id="onlineCoursePlatformApp.lessonProgress.delete.question"
          data-cy="lessonProgressDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p
          id="jhi-delete-lessonProgress-heading"
          v-text="t$('onlineCoursePlatformApp.lessonProgress.delete.question', { id: removeId })"
        ></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-lessonProgress"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeLessonProgress()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./lesson-progress.component.ts"></script>
