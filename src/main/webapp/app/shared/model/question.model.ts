import { type ICourseItem } from '@/shared/model/course-item.model';

import { type QuestionType } from '@/shared/model/enumerations/question-type.model';
export interface IQuestion {
  id?: number;
  text?: string;
  type?: keyof typeof QuestionType;
  options?: string;
  courseItem?: ICourseItem | null;
}

export class Question implements IQuestion {
  constructor(
    public id?: number,
    public text?: string,
    public type?: keyof typeof QuestionType,
    public options?: string,
    public courseItem?: ICourseItem | null,
  ) {}
}
