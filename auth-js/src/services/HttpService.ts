import { UserService } from "./UserService";

const baseUrl = "http://localhost:8081";

const checkStatus = (response: Response) => {
  if (!response.status.toString().startsWith("2")) {
    throw Error(
      `${response.url} result: ${response.status}(${response.statusText})`
    );
  }
};

const post = async <REQ, RSP>(path: string, body: REQ): Promise<RSP> => {
  const headers: HeadersInit = {
    Accept: "application/json",
    "Content-Type": "application/json",
  };
  if (UserService.isAuthenticated()) {
    await UserService.updateToken()
    headers.Authorization = `Bearer ${UserService.getToken()}`;
  }
  const response = await fetch(`${baseUrl}${path}`, {
    method: "POST",
    headers,
    body: JSON.stringify(body),
  });

  checkStatus(response);

  return await response.json();
};

const get = async <RSP>(path: string): Promise<RSP> => {
  const headers: HeadersInit = {
    Accept: "application/json",
  };
  if (UserService.isAuthenticated()) {
    await UserService.updateToken()
    headers.Authorization = `Bearer ${UserService.getToken()}`;
  }
  const response = await fetch(`${baseUrl}${path}`, {
    method: "GET",
    headers,
  });

  checkStatus(response);

  return await response.json();
};

export const HttpService = {
  post,
  get,
};
