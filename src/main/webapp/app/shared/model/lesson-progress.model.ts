import { type IUser } from '@/shared/model/user.model';
import { type ICourseItem } from '@/shared/model/course-item.model';

export interface ILessonProgress {
  id?: number;
  viewed?: boolean;
  viewedDate?: Date | null;
  student?: IUser | null;
  courseItem?: ICourseItem | null;
}

export class LessonProgress implements ILessonProgress {
  constructor(
    public id?: number,
    public viewed?: boolean,
    public viewedDate?: Date | null,
    public student?: IUser | null,
    public courseItem?: ICourseItem | null,
  ) {
    this.viewed = this.viewed ?? false;
  }
}
