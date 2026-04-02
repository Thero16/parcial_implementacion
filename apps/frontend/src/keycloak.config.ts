// ─────────────────────────────────────────────────────────────────
//  Keycloak Configuration
//  Modify these values to match your Keycloak server setup.
// ─────────────────────────────────────────────────────────────────

export interface KeycloakConfig {
  url: string;         // Keycloak server URL 
  realm: string;       // Your realm name
  clientId: string;    // Your client ID
}

const keycloakConfig: KeycloakConfig = {
  url: import.meta.env.VITE_KEYCLOAK_URL ,
  realm: import.meta.env.VITE_KEYCLOAK_REALM ,
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID ,
};

export default keycloakConfig;
