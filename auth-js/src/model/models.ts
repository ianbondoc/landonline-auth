// these interfaces were copied from Lui to promote backward compatibility

export interface IUser {
  id: string;
  givenNames: string;
  surname: string;
  email: string;
  preferredName: string;
  firms: IFirm[];
  loginType: 'INTN' | 'EXTN';
  roles: string[];
  profiles: string[];
  lastLogin?: string;
}

export interface IFirm {
    id: string;
    name: string;
    privileges: string[];
}

export interface IUserContext {
  isLoading: boolean;
  isAuthenticated: boolean;
  error?: any;
  login: () => Promise<void>;
  logout: () => Promise<void>;
  accessToken?: string,
  user?: IUser;
  selectedFirm?: IFirm;
  changeFirm: (firmId: string) => void;
  isInternal: () => boolean;
  hasAnyPrivilege: (privileges: string[]) => boolean;
}