<template>
  <div>
    <h2 id="page-heading" data-cy="TestAttemptHeading">
      <span v-text="t$('onlineCoursePlatformApp.testAttempt.home.title')" id="test-attempt-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('onlineCoursePlatformApp.testAttempt.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'TestAttemptCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-test-attempt"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('onlineCoursePlatformApp.testAttempt.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && testAttempts && testAttempts.length === 0">
      <span v-text="t$('onlineCoursePlatformApp.testAttempt.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="testAttempts && testAttempts.length > 0">
      <table class="table table-striped" aria-describedby="testAttempts">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.testAttempt.score')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.testAttempt.passed')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.testAttempt.attemptDate')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.testAttempt.student')"></span></th>
            <th scope="row"><span v-text="t$('onlineCoursePlatformApp.testAttempt.courseItem')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="testAttempt in testAttempts" :key="testAttempt.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'TestAttemptView', params: { testAttemptId: testAttempt.id } }">{{ testAttempt.id }}</router-link>
            </td>
            <td>{{ testAttempt.score }}</td>
            <td>{{ testAttempt.passed }}</td>
            <td>{{ formatDateShort(testAttempt.attemptDate) || '' }}</td>
            <td>
              {{ testAttempt.student ? testAttempt.student.username : '' }}
            </td>
            <td>
              <div v-if="testAttempt.courseItem">
                <router-link :to="{ name: 'CourseItemView', params: { courseItemId: testAttempt.courseItem.id } }">{{
                  testAttempt.courseItem.title
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'TestAttemptView', params: { testAttemptId: testAttempt.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'TestAttemptEdit', params: { testAttemptId: testAttempt.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(testAttempt)"
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
          id="onlineCoursePlatformApp.testAttempt.delete.question"
          data-cy="testAttemptDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-testAttempt-heading" v-text="t$('onlineCoursePlatformApp.testAttempt.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-testAttempt"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeTestAttempt()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./test-attempt.component.ts"></script>
