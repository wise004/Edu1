import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import LessonProgressService from './lesson-progress.service';
import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { LessonProgress } from '@/shared/model/lesson-progress.model';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('LessonProgress Service', () => {
    let service: LessonProgressService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new LessonProgressService();
      currentDate = new Date();
      elemDefault = new LessonProgress(123, false, currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = { viewedDate: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };
        axiosStub.get.resolves({ data: returnedFromService });

        return service.find(123).then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a LessonProgress', async () => {
        const returnedFromService = { id: 123, viewedDate: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };
        const expected = { viewedDate: currentDate, ...returnedFromService };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a LessonProgress', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a LessonProgress', async () => {
        const returnedFromService = { viewed: true, viewedDate: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };

        const expected = { viewedDate: currentDate, ...returnedFromService };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a LessonProgress', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a LessonProgress', async () => {
        const patchObject = { viewed: true, ...new LessonProgress() };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { viewedDate: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a LessonProgress', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of LessonProgress', async () => {
        const returnedFromService = { viewed: true, viewedDate: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };
        const expected = { viewedDate: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve().then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of LessonProgress', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a LessonProgress', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a LessonProgress', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
