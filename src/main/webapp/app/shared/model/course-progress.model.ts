import { type IUser } from '@/shared/model/user.model';
import { type ICourse } from '@/shared/model/course.model';

export interface ICourseProgress {
  id?: number;
  completedItems?: number | null;
  isCompleted?: boolean | null;
  student?: IUser | null;
  course?: ICourse | null;
}

export class CourseProgress implements ICourseProgress {
  constructor(
    public id?: number,
    public completedItems?: number | null,
    public isCompleted?: boolean | null,
    public student?: IUser | null,
    public course?: ICourse | null,
  ) {
    this.isCompleted = this.isCompleted ?? false;
  }
}
