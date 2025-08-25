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

// Ingress: single entrypoint for all services
export const API_BASE_URL = asNonEmpty(import.meta.env.VITE_API_BASE_URL, 'VITE_API_BASE_URL');

// Keep existing shape to avoid widespread refactors; all point to the same base
export const API_URLS = {
  user: API_BASE_URL,
  department: API_BASE_URL,
  ticket: API_BASE_URL,
} as const;


