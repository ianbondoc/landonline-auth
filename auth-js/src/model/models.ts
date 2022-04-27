// these interfaces were copied from Lui to promote backward compatibility

import {KeycloakPromise} from 'keycloak-js';
import {MutableRefObject} from 'react';

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
    login: () => KeycloakPromise<void, void>;
    logout: () => KeycloakPromise<void, void>;
    user?: IUser;
    selectedFirm?: IFirm;
    selectedFirmRef?: MutableRefObject<IFirm | undefined>
    changeFirm: (firmId: string) => void;
    isInternal: () => boolean;
    hasAnyPrivilege: (privileges: string[]) => boolean;
}