<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save">
        <h2 v-text="t$('onlineCoursePlatformApp.courseItem.home.createOrEditLabel')"></h2>

        <!-- ID maydoni, faqat update rejimida -->
        <div class="form-group" v-if="courseItem.id">
          <label for="id" v-text="t$('global.field.id')"></label>
          <input type="text" class="form-control" id="id" v-model="courseItem.id" readonly />
        </div>

        <!-- Title -->
        <div class="form-group">
          <label :for="titleId" v-text="t$('onlineCoursePlatformApp.courseItem.title')"></label>
          <input :id="titleId" type="text" class="form-control" v-model="courseItem.title" required />
        </div>

        <!-- ItemType (LESSON yoki TEST) -->
        <div class="form-group">
          <label :for="itemTypeId" v-text="t$('onlineCoursePlatformApp.courseItem.itemType')"></label>
          <select :id="itemTypeId" class="form-control" v-model="courseItem.itemType" required>
            <option v-for="it in itemTypeValues" :key="it" :value="it">
              {{ it }}
            </option>
          </select>
        </div>

        <!-- ContentType (UPLOADED_VIDEO, YOUTUBE_VIDEO, TEXT) -->
        <div class="form-group">
          <label :for="contentTypeId" v-text="t$('onlineCoursePlatformApp.courseItem.contentType')"></label>
          <select :id="contentTypeId" class="form-control" v-model="courseItem.contentType">
            <option v-for="ct in contentTypeValues" :key="ct" :value="ct">
              {{ ct }}
            </option>
          </select>
        </div>

        <!-- Agar contentType = UPLOADED_VIDEO bo'lsa, fayl yuklash inputi -->
        <div class="form-group" v-if="courseItem.contentType === 'UPLOADED_VIDEO'">
          <label for="videoFile">Video fayl</label>
          <input type="file" id="videoFile" class="form-control" @change="handleFileUpload" accept="video/*" />
        </div>

        <!-- Agar contentType = YOUTUBE_VIDEO bo'lsa, YouTube link maydoni -->
        <div class="form-group" v-else-if="courseItem.contentType === 'YOUTUBE_VIDEO'">
          <label for="youtubeContent">YouTube link</label>
          <input type="text" id="youtubeContent" class="form-control" v-model="courseItem.content" />
        </div>

        <!-- Agar contentType = TEXT bo'lsa, matn maydoni -->
        <div class="form-group" v-else-if="courseItem.contentType === 'TEXT'">
          <label for="textContent">Matn</label>
          <textarea id="textContent" class="form-control" rows="4" v-model="courseItem.content"></textarea>
        </div>

        <!-- Passing Score faqat TEST bo'lsa -->
        <div class="form-group" v-if="courseItem.itemType === 'TEST'">
          <label :for="passingScoreId" v-text="t$('onlineCoursePlatformApp.courseItem.passingScore')"></label>
          <input type="number" :id="passingScoreId" class="form-control" v-model.number="courseItem.passingScore" />
        </div>

        <!-- Kurs tanlash (ManyToOne) -->
        <div class="form-group">
          <label :for="courseSelectId" v-text="t$('onlineCoursePlatformApp.courseItem.course')"></label>
          <select :id="courseSelectId" class="form-control" v-model="courseItem.course">
            <option :value="null"></option>
            <option v-for="c in courses" :key="c.id" :value="c">
              {{ c.title }}
            </option>
          </select>
        </div>

        <!-- Tugmalar -->
        <button type="button" class="btn btn-secondary" @click="previousState()">
          {{ t$('entity.action.cancel') }}
        </button>
        <button type="submit" class="btn btn-primary" :disabled="isSaving">
          {{ t$('entity.action.save') }}
        </button>
      </form>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { ICourseItem } from '@/shared/model/course-item.model';
import CourseItemService from './course-item.service';
import CourseService from '@/entities/course/course.service';
import { ICourse } from '@/shared/model/course.model';

@Component
export default class CourseItemUpdate extends Vue {
  public courseItem: ICourseItem = {};
  public isSaving = false;

  // Faylni saqlash uchun
  public file: File | null = null;

  // Enum values
  public itemTypeValues: string[] = ['LESSON', 'TEST'];
  public contentTypeValues: string[] = ['UPLOADED_VIDEO', 'YOUTUBE_VIDEO', 'TEXT'];

  // Kurs ro'yxati
  public courses: ICourse[] = [];

  // ID-lar (ixtiyoriy, accessibility uchun)
  get titleId() {
    return 'course-item-title';
  }
  get itemTypeId() {
    return 'course-item-itemType';
  }
  get contentTypeId() {
    return 'course-item-contentType';
  }
  get passingScoreId() {
    return 'course-item-passingScore';
  }
  get courseSelectId() {
    return 'course-item-course';
  }

  // Servislar
  private courseItemService = new CourseItemService();
  private courseService = new CourseService();

  // Lifecycle hook
  created(): void {
    // Agar route param orqali item ID kelsa, uni yuklab olish
    const courseItemId = this.$route.params.courseItemId;
    if (courseItemId) {
      this.retrieveCourseItem(courseItemId);
    }
    this.initCourses();
  }

  public retrieveCourseItem(courseItemId: number) {
    this.courseItemService()
      .find(courseItemId)
      .then(res => {
        this.courseItem = res;
      });
  }

  public initCourses() {
    this.courseService()
      .retrieve()
      .then(res => {
        this.courses = res.data;
      });
  }

  // Fayl tanlanganida
  public handleFileUpload(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      this.file = target.files[0];
    }
  }

  // Form submit
  public save(): void {
    this.isSaving = true;
    // Agar contentType = UPLOADED_VIDEO bo'lsa, fayl yuklash uchun FormData ishlatamiz
    if (this.courseItem.contentType === 'UPLOADED_VIDEO' && this.file) {
      const formData = new FormData();
      formData.append('title', this.courseItem.title ?? '');
      formData.append('itemType', this.courseItem.itemType ?? 'LESSON');
      formData.append('contentType', this.courseItem.contentType ?? '');
      formData.append('file', this.file);

      // Agar course bo'lsa, masalan, course.id ni ham jo'natish mumkin
      if (this.courseItem.course && this.courseItem.course.id) {
        formData.append('courseId', this.courseItem.course.id.toString());
      }

      // Maxsus endpoint:
      this.courseItemService()
        .uploadLesson(formData)
        .then(() => {
          this.isSaving = false;
          this.previousState();
        })
        .catch(err => {
          this.isSaving = false;
          alert('Error: ' + err);
        });
    } else {
      // Oddiy CRUD update
      this.courseItemService()
        .update(this.courseItem)
        .then(param => {
          this.isSaving = false;
          this.previousState();
        })
        .catch(err => {
          this.isSaving = false;
          alert('Error: ' + err);
        });
    }
  }

  public previousState(): void {
    this.$router.go(-1);
  }
}
</script>

<style scoped>
/* Qo'shimcha style xohlasa */
</style>
