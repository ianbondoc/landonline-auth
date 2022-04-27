import React, {
  PropsWithChildren,
  useCallback,
  useContext,
  useEffect,
  useRef,
  useState,
} from 'react';
import { IFirm, IUser, IUserContext } from '../model';
import { AuthService } from '../services';

const UserContext = React.createContext<IUserContext>({
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

export const UserContextProvider: React.FC<PropsWithChildren<{}>> = ({
  children,
}) => {
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(
    AuthService.isAuthenticated()
  );
  const [user, setUser] = useState<IUser>();
  const [selectedFirm, setSelectedFirm] = useState<IFirm>();
  const selectedFirmRef = useRef<IFirm>();
  const [error, setError] = useState<any>();

  useEffect(() => {
    if (AuthService.isAuthenticated()) {
      setIsAuthenticated(true);
      setIsLoading(false);
    } else {
      AuthService.initKeycloak()
        .then(authenticated => {
          setIsAuthenticated(authenticated);
          setIsLoading(false);
        })
        .catch(error => setError(error));
    }
  }, []);

  useEffect(() => {
    if (isAuthenticated) {
      const fetchUserInfo = async () => AuthService.getUserInfo();
      fetchUserInfo()
        .then(userInfo => setUser(userInfo))
        .catch(error => setError(error));
    } else {
      setUser(undefined);
    }
  }, [isAuthenticated]);

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
    isLoading,
    isAuthenticated,
    error,
    login: AuthService.login,
    logout: AuthService.logout,
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
