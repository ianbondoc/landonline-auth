import React, { PropsWithChildren } from 'react';

import { CachePolicies, IncomingOptions, Provider } from 'use-http';
import { useUserContext } from './UserContext';

export const FetchProvider: React.FC<PropsWithChildren<{
  baseUrl?: string;
  commonOptions?: IncomingOptions;
}>> = ({ baseUrl, commonOptions = {}, children }) => {
  const {
    isLoading,
    isAuthenticated,
    user,
    login,
    selectedFirmRef,
  } = useUserContext();

  const url = baseUrl || window.location.origin;
  const options: IncomingOptions = {
    ...commonOptions,
    // we need this so the XSRF-TOKEN would be included in request
    // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Allow-Credentials
    credentials: 'include',
    // by default useFetch caches results we don't want that, let's just enable it when we need it
    cachePolicy: CachePolicies.NO_CACHE,
    interceptors: {
      request: async ({ options }) => {
        let headers: HeadersInit = options.headers || {};

        const xsrfToken = getCookie('XSRF-TOKEN');

        if (!!xsrfToken) {
          headers = { ...headers, 'X-XSRF-TOKEN': xsrfToken };
        }

        if (!isLoading && isAuthenticated) {
          // we always assume token is valid (we rely on automaticSilentRenew)
          headers = {
            ...headers,
            Authorization: `Bearer ${user?.accessToken}`,
          };
          if (selectedFirmRef?.current) {
            headers = {
              ...headers,
              'x-linz-selected-firm': selectedFirmRef.current.id,
            };
          }
        }

        return { ...options, headers };
      },
      response: async ({ response }) => {
        // any 401 response should just forward us to the login page
        if (isAuthenticated && response.status == 401) {
          await login();
        }
        return response;
      },
    },
  };

  return (
    <Provider url={url} options={options}>
      {children}
    </Provider>
  );
};

const getCookie = (name: string): string => {
  const nameEQ = name + '=';
  const ca = document.cookie.split(';');
  for (const item of ca) {
    let c = item;
    while (c.charAt(0) === ' ') c = c.substring(1, c.length);
    if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
  }
  return '';
};