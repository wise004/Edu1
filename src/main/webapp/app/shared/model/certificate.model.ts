import { type ICourseProgress } from '@/shared/model/course-progress.model';

export interface ICertificate {
  id?: number;
  issueDate?: Date;
  certificateUrl?: string | null;
  courseProgress?: ICourseProgress | null;
}

export class Certificate implements ICertificate {
  constructor(
    public id?: number,
    public issueDate?: Date,
    public certificateUrl?: string | null,
    public courseProgress?: ICourseProgress | null,
  ) {}
}
