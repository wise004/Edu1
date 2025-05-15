import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import TestAttemptService from './test-attempt.service';
import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { TestAttempt } from '@/shared/model/test-attempt.model';

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
  describe('TestAttempt Service', () => {
    let service: TestAttemptService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new TestAttemptService();
      currentDate = new Date();
      elemDefault = new TestAttempt(123, 0, false, currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = { attemptDate: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };
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

      it('should create a TestAttempt', async () => {
        const returnedFromService = { id: 123, attemptDate: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };
        const expected = { attemptDate: currentDate, ...returnedFromService };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a TestAttempt', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a TestAttempt', async () => {
        const returnedFromService = { score: 1, passed: true, attemptDate: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };

        const expected = { attemptDate: currentDate, ...returnedFromService };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a TestAttempt', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a TestAttempt', async () => {
        const patchObject = { passed: true, ...new TestAttempt() };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { attemptDate: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a TestAttempt', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of TestAttempt', async () => {
        const returnedFromService = { score: 1, passed: true, attemptDate: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };
        const expected = { attemptDate: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve().then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of TestAttempt', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a TestAttempt', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a TestAttempt', async () => {
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
