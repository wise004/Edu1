<template>
  <div>
    <b-dropdown-item to="/certificate">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.certificate')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/course">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.course')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/course-item">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.courseItem')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/course-progress">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.courseProgress')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/lesson-progress">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.lessonProgress')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/question">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.question')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/test-attempt">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.testAttempt')"></span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>
