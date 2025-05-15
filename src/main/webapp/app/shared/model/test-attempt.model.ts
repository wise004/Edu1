import { type IUser } from '@/shared/model/user.model';
import { type ICourseItem } from '@/shared/model/course-item.model';

export interface ITestAttempt {
  id?: number;
  score?: number | null;
  passed?: boolean | null;
  attemptDate?: Date | null;
  student?: IUser | null;
  courseItem?: ICourseItem | null;
}

export class TestAttempt implements ITestAttempt {
  constructor(
    public id?: number,
    public score?: number | null,
    public passed?: boolean | null,
    public attemptDate?: Date | null,
    public student?: IUser | null,
    public courseItem?: ICourseItem | null,
  ) {
    this.passed = this.passed ?? false;
  }
}
