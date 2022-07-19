import React, { PropsWithChildren } from 'react';
import { useUserContext } from './UserContext';

export const Authenticated: React.FC<PropsWithChildren<{}>> = ({
  children,
}) => {
  const { isAuthenticated } = useUserContext();
  if (isAuthenticated) {
    return <>{children}</>;
  } else {
    return null;
  }
};