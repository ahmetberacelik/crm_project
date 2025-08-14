import React from 'react';
import ReactDOM from 'react-dom/client';
import { setupAuthInterceptors } from './shared/libs/axios/authInterceptor';
import { AppRouter } from './app/router';
import { QueryProvider } from './app/providers/QueryProvider';
import { ThemeProvider } from './app/providers/ThemeProvider';

const Root: React.FC = () => (
  <ThemeProvider>
    <QueryProvider>
      <AppRouter />
    </QueryProvider>
  </ThemeProvider>
);

// Install interceptors once on startup
setupAuthInterceptors();

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <Root />
  </React.StrictMode>
);



