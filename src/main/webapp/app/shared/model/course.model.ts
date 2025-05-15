import { type IUser } from '@/shared/model/user.model';

export interface ICourse {
  id?: number;
  title?: string;
  description?: string | null;
  author?: IUser | null;
}

export class Course implements ICourse {
  constructor(
    public id?: number,
    public title?: string,
    public description?: string | null,
    public author?: IUser | null,
  ) {}
}
