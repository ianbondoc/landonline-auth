import React, {
  PropsWithChildren,
  useCallback,
  useContext,
  useEffect,
  useRef,
  useState,
} from 'react';
import { AuthProvider, useAuth } from 'react-oidc-context';
import { IFirm, IUser, IUserContext } from '../model';

export const UserContext = React.createContext<IUserContext>({
  isLoading: true,
  isAuthenticated: false,
  user: undefined,
  error: undefined,
  login: () => {
    throw Error();
  },
  logout: () => {
    throw Error();
  },
  selectedFirm: undefined,
  selectedFirmRef: undefined,
  changeFirm: () => {
    throw Error();
  },
  isInternal: () => {
    throw Error();
  },
  hasAnyPrivilege: () => {
    throw Error();
  },
});

export function useUserContext(): IUserContext {
  return useContext(UserContext);
}

export interface OidcConfig {
  issuerUri: string;
  clientId: string;
  postLoginUri: string;
  postLogoutUri: string;
}

export const UserContextProvider: React.FC<PropsWithChildren<OidcConfig>> = ({
  issuerUri,
  clientId,
  postLoginUri,
  postLogoutUri,
  children,
}) => {
  const oidcConfig = {
    authority: issuerUri,
    client_id: clientId,
    redirect_uri: postLoginUri,
    post_logout_redirect_uri: postLogoutUri,
    // we'll just automatically renew token to be simple
    // automaticSilentRenew: false,
    loadUserInfo: true,
    // revokeTokensOnSignout: true,
    onSigninCallback: async () => {
      // this is to remove the query params of on callback
      window.history.replaceState({}, document.title, window.location.pathname);
    },
  };

  return (
    <AuthProvider {...oidcConfig}>
      <UserContextAdapter>{children}</UserContextAdapter>
    </AuthProvider>
  );
};

// unfortunately the oidc-client-ts library converts a single element array in user profile into an object
// we handle that behavior to be consistent and always produce an array
const normalizeArray = (something: any) =>
  Array.isArray(something) ? something : [something];

const UserContextAdapter: React.FC<PropsWithChildren<{}>> = ({ children }) => {
  const auth = useAuth();

  const [user, setUser] = useState<IUser>();
  const [selectedFirm, setSelectedFirm] = useState<IFirm>();
  const selectedFirmRef = useRef<IFirm>();

  useEffect(() => {
    const userProfile = auth.user?.profile;
    if (auth.isAuthenticated && userProfile) {
      setUser({
        id: userProfile.id,
        givenNames: userProfile.givenNames,
        surname: userProfile.surname,
        email: userProfile.email,
        preferredName: userProfile.preferredName,
        firms: normalizeArray(userProfile.firms),
        loginType: userProfile.loginType,
        roles: normalizeArray(userProfile.roles),
        profiles: normalizeArray(userProfile.profiles),
        lastLogin: userProfile.lastLogin,
        accessToken: auth.user?.access_token,
      } as IUser);
    } else {
      setUser(undefined);
    }
  }, [auth.isAuthenticated, auth.user]);

  // for now no need to implement this notification - just a nice to have
  // useEffect(() => {
  //   // the `return` is important - addAccessTokenExpiring() returns a cleanup function
  //   return auth.events.addAccessTokenExpiring(async () => {
  //     if (window.confirm("You're about to be signed out due to inactivity. Press continue to stay signed in.")) {
  //       try {
  //         await auth.signinSilent();
  //       } catch (e) {
  //         console.warn('Failed to refresh token', e);
  //         await auth.signinRedirect();
  //       }
  //     }
  //   })
  // }, [auth.events, auth.signinSilent])

  useEffect(() => {
    // User data is still loading
    if (!user) {
      return;
    }

    // Clear firm value if there are no firms
    if (user.firms.length === 0) {
      setSelectedFirmCache(null, null);
      selectedFirmRef.current = undefined;
      return;
    }

    if (
      user.firms.length > 0 &&
      (!getSelectedFirmIdCache() ||
        !user.firms.find(firm => firm.id === getSelectedFirmIdCache()))
    ) {
      const firstFirm = user.firms[0];

      setSelectedFirmCache(firstFirm.id, firstFirm.name);
      setSelectedFirm(firstFirm);
      selectedFirmRef.current = firstFirm;
    } else {
      // pull firm from cache and use it
      const selectedFirmIdFromCache = getSelectedFirmIdCache();
      const selectedFirmDerivedFromCache = user.firms.find(
        firm => firm.id === selectedFirmIdFromCache
      );
      setSelectedFirm(selectedFirmDerivedFromCache || undefined);
      selectedFirmRef.current = selectedFirmDerivedFromCache || undefined;
    }
  }, [user]);

  const changeFirm = useCallback(
    (firmId: string) => {
      const firm = user?.firms.find(f => {
        return f.id === firmId;
      });

      if (firm === undefined) {
        // tslint:disable-next-line: no-console
        console.error('Looks like firm not in userContext');
        return;
      }
      setSelectedFirmCache(firm.id, firm.name);
      setSelectedFirm(firm);
      selectedFirmRef.current = firm;
    },
    [user]
  );

  const isInternal = useCallback(() => userIsInternal(user), [user]);

  const hasAnyPrivilege = useCallback(
    (privileges: string[]): boolean =>
      userHasAnyPrivilege(privileges, selectedFirm),
    [selectedFirm]
  );

  const userContext: IUserContext = {
    isLoading: auth.isLoading,
    isAuthenticated: auth.isAuthenticated,
    error: auth.error,
    login: auth.signinRedirect,
    logout: auth.signoutRedirect,
    user,
    selectedFirm,
    selectedFirmRef,
    changeFirm,
    isInternal,
    hasAnyPrivilege,
  };
  return (
    <UserContext.Provider value={userContext}>{children}</UserContext.Provider>
  );
};

const FIRM_KEY = 'firmId';
const FIRM_NAME_KEY = 'firmName';

const getSelectedFirmIdCache = (): string | null => {
  return window.localStorage.getItem(FIRM_KEY);
};

const setSelectedFirmCache = (
  firmId: string | null,
  firmName: string | null
) => {
  if (firmId === null || firmName === null) {
    window.localStorage.removeItem(FIRM_KEY);
    window.localStorage.removeItem(FIRM_NAME_KEY);
  } else {
    window.localStorage.setItem(FIRM_KEY, firmId);
    window.localStorage.setItem(FIRM_NAME_KEY, firmName);
  }
};

const userHasAnyPrivilege = (
  privileges: string[],
  selectedFirm: IFirm | undefined
): boolean => {
  return (
    privileges.length === 0 ||
    (selectedFirm !== undefined &&
      selectedFirm.privileges.filter(value => privileges.indexOf(value) > -1)
        .length > 0)
  );
};

const userIsInternal = (user: IUser | null | undefined): boolean => {
  const loginType =
    ((user?.loginType as any).code as string) || (user?.loginType as string);
  return loginType === 'INTN';
};
