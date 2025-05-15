import { type ICourse } from '@/shared/model/course.model';

import { type ItemType } from '@/shared/model/enumerations/item-type.model';
import { type ContentType } from '@/shared/model/enumerations/content-type.model';
export interface ICourseItem {
  id?: number;
  title?: string;
  itemType?: keyof typeof ItemType;
  contentType?: keyof typeof ContentType | null;
  content?: string | null;
  passingScore?: number | null;
  course?: ICourse | null;
}

export class CourseItem implements ICourseItem {
  constructor(
    public id?: number,
    public title?: string,
    public itemType?: keyof typeof ItemType,
    public contentType?: keyof typeof ContentType | null,
    public content?: string | null,
    public passingScore?: number | null,
    public course?: ICourse | null,
  ) {}
}
