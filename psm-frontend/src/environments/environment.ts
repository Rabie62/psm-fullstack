const isLocalDevServer = typeof window !== 'undefined' && window.location.port === '4200';

export const environment = {
  production: true,
  apiUrl: isLocalDevServer ? 'http://127.0.0.1:8081' : ''
};

