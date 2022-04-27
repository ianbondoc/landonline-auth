import React, { PropsWithChildren } from 'react';
import { CachePolicies, IncomingOptions, Provider } from 'use-http';
import { useUserContext } from '../components';
import { AuthService } from '../services';

export const FetchProvider: React.FC<PropsWithChildren<{
  baseUrl?: string;
  commonOptions?: IncomingOptions;
}>> = ({ baseUrl, commonOptions = {}, children }) => {
  const { isLoading, isAuthenticated, selectedFirmRef } = useUserContext();

  const url = baseUrl || window.location.origin;
  const options: IncomingOptions = {
    ...commonOptions,
    // we need this so the XSRF-TOKEN would be included in request
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
          // this doesn't mean we update the token every time, under the hood it tries to see if token
          // is nearing its expiry and updates it when needed
          try {
            const x = await AuthService.updateToken();
            console.log(`Refresh result: ${x}`);
            headers = {
              ...headers,
              Authorization: `Bearer ${AuthService.getToken()}`,
            };
          } catch (e) {
            // this could mean refresh token expired or we were logged out by server (or auth restarted)
            console.log(e);
            AuthService.login();
          }

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
        // any 401 response should
        if (AuthService.isAuthenticated() && response.status == 401) {
          AuthService.login();
        }
        return response;
      },
    },
  };

  return (
    <>
      {!isLoading && (
        <Provider url={url} options={options}>
          {children}
        </Provider>
      )}
    </>
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