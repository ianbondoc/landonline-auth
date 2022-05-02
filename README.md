# Keycloak Integration

## Concerns

- Keycloak image and configuration
- User Storage Provider
- Roles/Permission Mapping
- Claims customization (make it lean)
- User Info Endpoint
- Theming
- Frontend Client Integration (auth-js)
- React Components (UserContext, Fetch, Auth/Noauth)
- CORS (Frontend facing endpoints only)
- CSRF/XSRF (Frontend facing endpoints only)
- Service Integration
- Spring Components (Configuration, Exception Handler)
- Frontend -> Service Security (public -> bearer only/confidential)
- Service -> Service (requester should be confidential to get client_credentials grant https://www.appsdeveloperblog.com/keycloak-client-credentials-grant-example/)
- Authorization
- Token Verification (signature and expiry)
- Token Refresh ()
- Firm Information
- Password Change (via admin)
- Temporary Password Change (via admin) (Required Action)
- Required action via external (database)
- Forgot Password
- Remember Me