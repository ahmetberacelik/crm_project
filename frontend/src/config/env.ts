/**
 * Centralized environment configuration for frontend API URLs.
 * Reads Vite-provided variables and validates their presence at startup.
 */

type NonEmptyString = string & { __brand: 'NonEmptyString' };

function asNonEmpty(value: unknown, varName: string): NonEmptyString {
  if (typeof value !== 'string' || value.trim().length === 0) {
    const message = `Missing required env variable: ${varName}. Please define it in frontend/.env`;
    if (import.meta.env.DEV) {
      // Fail fast in development to avoid silent misconfiguration.
      throw new Error(message);
    }
    // In production, still return an obvious placeholder to reduce null checks.
    return (`__MISSING_${varName}__` as unknown) as NonEmptyString;
  }
  return value as NonEmptyString;
}

export const USER_API_URL = asNonEmpty(import.meta.env.VITE_USER_API_URL, 'VITE_USER_API_URL');
export const DEPARTMENT_API_URL = asNonEmpty(import.meta.env.VITE_DEPARTMENT_API_URL, 'VITE_DEPARTMENT_API_URL');
export const TICKET_API_URL = asNonEmpty(import.meta.env.VITE_TICKET_API_URL, 'VITE_TICKET_API_URL');

export const API_URLS = {
  user: USER_API_URL,
  department: DEPARTMENT_API_URL,
  ticket: TICKET_API_URL,
} as const;


