import React from 'react';
import { useAuthStore } from '../shared/stores/auth.store';
import { HomePage } from './HomePage';
import { CreatePublicTicketPage } from '../features/public/pages/CreatePublicTicketPage';

export const LandingPage: React.FC = () => {
  const isLoggedIn = useAuthStore((s) => s.isLoggedIn());
  return isLoggedIn ? <HomePage /> : <CreatePublicTicketPage />;
};


