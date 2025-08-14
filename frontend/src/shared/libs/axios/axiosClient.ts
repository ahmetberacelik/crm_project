import axios from 'axios';
import { API_URLS } from '../../../config/env';

export const userApi = axios.create({ baseURL: API_URLS.user });
export const departmentApi = axios.create({ baseURL: API_URLS.department });
export const ticketApi = axios.create({ baseURL: API_URLS.ticket });

// A bare client without interceptors for internal calls like refresh
export const bareUserApi = axios.create({ baseURL: API_URLS.user });


