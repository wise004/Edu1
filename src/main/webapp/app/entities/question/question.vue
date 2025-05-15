<template>
  <div>
    <h2 id="page-heading" data-cy="QuestionHeading">
      <span v-text="t$('onlineCoursePlatformApp.question.home.title')" id="question-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('onlineCoursePlatformApp.question.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'QuestionCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-question"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('onlineCoursePlatformApp.question.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && questions && questions.length === 0">
      <span v-text="t$('onlineCoursePlatformApp.question.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="questions && questions.length > 0">
      <table class="table table-striped" aria-describedby="questions">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.question.text')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.question.type')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.question.options')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.question.courseItem')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="question in questions" :key="question.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'QuestionView', params: { questionId: question.id } }">{{ question.id }}</router-link>
            </td>
            <td>{{ question.text }}</td>
            <td v-text="t$('onlineCoursePlatformApp.QuestionType.' + question.type)"></td>
            <td>{{ question.options }}</td>
            <td>
              <div v-if="question.courseItem">
                <router-link :to="{ name: 'CourseItemView', params: { courseItemId: question.courseItem.id } }">{{
                  question.courseItem.title
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'QuestionView', params: { questionId: question.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'QuestionEdit', params: { questionId: question.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(question)"
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
          id="onlineCoursePlatformApp.question.delete.question"
          data-cy="questionDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-question-heading" v-text="t$('onlineCoursePlatformApp.question.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-question"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeQuestion()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./question.component.ts"></script>
