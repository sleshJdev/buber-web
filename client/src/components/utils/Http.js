const HEADER_STRING = 'X-Authorization';
const AUTH_TOKEN_KEY = 'buber.auth.token';
const CONTENT_TYPE_HEADER = 'Content-Type';
const CONTENT_LENGTH_HEADER = 'Content-Length';
const MEDIA_TYPE_JSON_UTF8 = 'application/json;charset=utf-8';

// eslint-disable-next-line
let userInfo = null;
// eslint-disable-next-line
let router = null;

function getHeader(jsonContentType = true) {
  const headers = new Headers();
  const authToken = localStorage.getItem(AUTH_TOKEN_KEY);
  if (authToken) {
    headers.append(HEADER_STRING, authToken);
  }
  if (jsonContentType) {
    headers.append(CONTENT_TYPE_HEADER, MEDIA_TYPE_JSON_UTF8);
  }
  return headers;
}

function toSignForm(response) {
  switch (response.status) {
    case 401:
    case 403:
      router.push({ name: 'sign-in' });
      break;
    default:
      router.push('/');
  }
  return Promise.reject(response);
}

function toJson(response) {
  if (!response.ok) {
    return toSignForm(response);
  }
  const headers = response.headers;
  const contentType = headers.get(CONTENT_TYPE_HEADER) || '';
  const contentLength = headers.get(CONTENT_LENGTH_HEADER) || 0;
  const isJson = contentType.toLowerCase() === MEDIA_TYPE_JSON_UTF8;
  const notEmpty = contentLength > 0;
  if (isJson || notEmpty) {
    return response.json();
  }
  return response;
}

export default class Auth {
  static setRouter(therouter) {
    router = therouter;
  }

  static userInfo() {
    return userInfo;
  }

  static isSignedIn() {
    return !!userInfo;
  }

  static signOut() {
    userInfo = null;
    localStorage.removeItem(AUTH_TOKEN_KEY);
  }

  static signIn(credentials) {
    return this.doPost(
      '/api/auth/sign-in',
      // eslint-disable-next-line
      JSON.stringify(credentials)
    ).then((response) => {
      const headers = response.headers;
      if (headers.has(HEADER_STRING)) {
        localStorage.setItem(AUTH_TOKEN_KEY, headers.get(HEADER_STRING));
        userInfo = {
          username: credentials.username,
        };
      }
    });
  }

  static signUp(credentials) {
    return this.doPost(
      '/api/auth/sign-up',
      // eslint-disable-next-line
      JSON.stringify(credentials)
    );
  }

  static doGet(url) {
    return fetch(url, {
      method: 'GET',
      headers: getHeader(),
    }).then(toJson)
      .catch(toSignForm);
  }

  static doPost(url, data, jsonContentType = true) {
    return fetch(url, {
      method: 'POST',
      body: data,
      headers: getHeader(jsonContentType),
    }).then(toJson)
      .catch(toSignForm);
  }

  static fetchAds(params) {
    const url = this.buildUrl('/api/ads', params);
    return this.doGet(url);
  }

  static buildUrl(baseUrl, params) {
    let first = true;
    let url = baseUrl;
    Object.entries(params).forEach((pair) => {
      const param = pair[0];
      const value = pair[1];
      if (value !== null &&
          value !== undefined &&
          value.toString().trim() !== '') {
        url += `${first ? '?' : '&'}${param}=${value}`;
        first = false;
      }
    });
    return url;
  }
}
