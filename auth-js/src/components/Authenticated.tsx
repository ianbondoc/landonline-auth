import React, { PropsWithChildren } from "react";
import { AuthService } from "../services";

export const Authenticated: React.FC<PropsWithChildren<{}>> = ({
  children,
}) => {
  if (AuthService.isAuthenticated()) {
    return <>{children}</>;
  } else {
    return null;
  }
};