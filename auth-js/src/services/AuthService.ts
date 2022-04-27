import Keycloak, { KeycloakError } from 'keycloak-js';
import { IUser } from '../model';

const keycloak = new Keycloak('/keycloak.json');

const initKeycloak = async (): Promise<boolean> => {
  return new Promise<boolean>((resolve, reject) => {
    keycloak
      .init({
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: `${window.location.origin}/silent-check-sso.html`,
        pkceMethod: 'S256',
      })
      .then(authenticated => {
        resolve(authenticated);
      })
      .catch(reason => reject(reason));
  });
};

const isAuthenticated = () => !!keycloak.authenticated;
const login = () => keycloak.login();
const logout = () => keycloak.logout();
const onAuthSuccess = (handler: () => void) =>
  (keycloak.onAuthSuccess = handler);
const onAuthLogout = (handler: () => void) => (keycloak.onAuthLogout = handler);
const onAuthError = (handler: (error: KeycloakError) => void) =>
  (keycloak.onAuthError = handler);

const getToken = () => keycloak.token;
const updateToken = async () => keycloak.updateToken(5);

const getUserInfo = async (): Promise<IUser> =>
  (keycloak.userInfo || (await keycloak.loadUserInfo())) as IUser;

export const AuthService = {
  initKeycloak,
  isAuthenticated,
  login,
  logout,
  onAuthSuccess,
  onAuthLogout,
  onAuthError,
  getToken,
  updateToken,
  getUserInfo,
};
