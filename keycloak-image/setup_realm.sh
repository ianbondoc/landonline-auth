#!/usr/bin/env bash

#KEYCLOAK_HOME=/c/Users/ibondoc/code/tools/keycloak-17.0.1
KEYCLOAK_HOME=/opt/keycloak
KCADM=kcadm.sh
JQ=/tmp/keycloak/jq
KC_HOSTNAME=${AUTH_DOMAIN:=localhost}
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin
REALM=landonline
FRONTEND_URL=${WEB_URL:=http://localhost:3000}
AUTH_URL=${SERVER_URL:=http://localhost:8080}
USER_PROVIDER=linz-user-provider
DB_URL="jdbc:informix-sqli://informix-db:9088/landonline_dev:OPTOFC=1;IFX_LOCK_MODE_WAIT=5;IFX_ISOLATION_LEVEL=2U;IFX_LO_READONLY=1"
DB_USERNAME=informix
DB_PASSWORD=in4mix
SERVICE_CLIENT_SECRET_A=pp8k7aPN2poVH1ypexeF2CDq2SpZ34WH
SERVICE_CLIENT_SECRET_B=another_secret

# login
while :; do
  $KCADM config credentials --server $AUTH_URL \
    --realm master --user $KEYCLOAK_ADMIN \
    --password $KEYCLOAK_ADMIN_PASSWORD
  if [ $? -eq 0 ]; then
    break
  else
    sleep 5
  fi
done

set -x

# create realm and grab the id for creating components (id here is different from realm name)
REALM_ID=$($KCADM create realms -s realm=$REALM \
  -s enabled=true \
  -s loginWithEmailAllowed=false \
  -s loginTheme=landonline \
  -s browserSecurityHeaders.contentSecurityPolicy="frame-src 'self'; frame-ancestors 'self' $FRONTEND_URL; object-src 'none';" \
  -o | $JQ -r '.id')

# first delete all default-default-client scopes (so all clients created will have empty default scopes)
# this is to minimize the size of the tokens
$KCADM get default-default-client-scopes -r landonline | $JQ .[].id |
  xargs -I % sh -c "$KCADM delete default-default-client-scopes/% -r landonline"

# grab admin's user id
USER_ID=$($KCADM get users -r master | $JQ -r '.[0].id')

# update admin user with email (and name)
$KCADM update users/$USER_ID -r master \
  -s firstName=Ian \
  -s lastName=Bondoc \
  -s email=ibondoc@linz.govt.nz

# update realm with smtp server (for password resets
$KCADM update realms/$REALM \
  -s smtpServer.host=host.docker.internal \
  -s smtpServer.port=2525 \
  -s smtpServer.from=admin@linz.govt.nz \
  -s smtpServer.replyTo=support@linz.govt.nz

# create frontend client
CID=$($KCADM create clients -r $REALM -s clientId=search-app \
  -s 'redirectUris=["'$FRONTEND_URL'/*"]' \
  -s 'webOrigins=["'$FRONTEND_URL'/*","'$FRONTEND_URL'"]' \
  -s directAccessGrantsEnabled=true \
  -s 'attributes."backchannel.logout.session.required"=true' \
  -s publicClient=true -i)

# create protocol mapper for groups as firms (if we want to represent firms as groups - not really)
# but leaving the code for future reference
#$KCADM create clients/$CID/protocol-mappers/models -r $REALM \
#    -s 'name=firms' \
#    -s 'protocol=openid-connect' \
#    -s 'protocolMapper=oidc-group-membership-mapper' \
#    -s 'config."full.path"=false' \
#    -s 'config."claim.name"=firms' \
#    -s 'config."id.token.claim"=false' \
#    -s 'config."access.token.claim"=false' \
#    -s 'config."userinfo.token.claim"=true'

# create userInfo protocolMappers
$KCADM create clients/$CID/protocol-mappers/models -r $REALM \
  -s 'name=username' \
  -s 'protocol=openid-connect' \
  -s 'protocolMapper=oidc-usermodel-property-mapper' \
  -s 'config."user.attribute"=username' \
  -s 'config."claim.name"=id' \
  -s 'config."id.token.claim"=false' \
  -s 'config."access.token.claim"=false' \
  -s 'config."userinfo.token.claim"=true' \
  -s 'config."jsonType.label"=String'

$KCADM create clients/$CID/protocol-mappers/models -r $REALM \
  -s 'name=firstName' \
  -s 'protocol=openid-connect' \
  -s 'protocolMapper=oidc-usermodel-property-mapper' \
  -s 'config."user.attribute"=firstName' \
  -s 'config."claim.name"=givenNames' \
  -s 'config."id.token.claim"=false' \
  -s 'config."access.token.claim"=false' \
  -s 'config."userinfo.token.claim"=true' \
  -s 'config."jsonType.label"=String'

$KCADM create clients/$CID/protocol-mappers/models -r $REALM \
  -s 'name=lastName' \
  -s 'protocol=openid-connect' \
  -s 'protocolMapper=oidc-usermodel-property-mapper' \
  -s 'config."user.attribute"=lastName' \
  -s 'config."claim.name"=surname' \
  -s 'config."id.token.claim"=false' \
  -s 'config."access.token.claim"=false' \
  -s 'config."userinfo.token.claim"=true' \
  -s 'config."jsonType.label"=String'

$KCADM create clients/$CID/protocol-mappers/models -r $REALM \
  -s 'name=email' \
  -s 'protocol=openid-connect' \
  -s 'protocolMapper=oidc-usermodel-property-mapper' \
  -s 'config."user.attribute"=email' \
  -s 'config."claim.name"=email' \
  -s 'config."id.token.claim"=false' \
  -s 'config."access.token.claim"=false' \
  -s 'config."userinfo.token.claim"=true' \
  -s 'config."jsonType.label"=String'

$KCADM create clients/$CID/protocol-mappers/models -r $REALM \
  -s 'name=preferredName' \
  -s 'protocol=openid-connect' \
  -s 'protocolMapper=oidc-usermodel-attribute-mapper' \
  -s 'config."user.attribute"=preferredName' \
  -s 'config."claim.name"=preferredName' \
  -s 'config."id.token.claim"=false' \
  -s 'config."access.token.claim"=false' \
  -s 'config."userinfo.token.claim"=true' \
  -s 'config."jsonType.label"=String'

$KCADM create clients/$CID/protocol-mappers/models -r $REALM \
  -s 'name=firms' \
  -s 'protocol=openid-connect' \
  -s 'protocolMapper=oidc-usermodel-attribute-mapper' \
  -s 'config."user.attribute"=firms' \
  -s 'config."claim.name"=firms' \
  -s 'config."id.token.claim"=false' \
  -s 'config."access.token.claim"=false' \
  -s 'config."userinfo.token.claim"=true' \
  -s 'config."jsonType.label"=JSON'

$KCADM create clients/$CID/protocol-mappers/models -r $REALM \
  -s 'name=loginType' \
  -s 'protocol=openid-connect' \
  -s 'protocolMapper=oidc-usermodel-attribute-mapper' \
  -s 'config."user.attribute"=loginType' \
  -s 'config."claim.name"=loginType' \
  -s 'config."id.token.claim"=false' \
  -s 'config."access.token.claim"=false' \
  -s 'config."userinfo.token.claim"=true' \
  -s 'config."jsonType.label"=String'

$KCADM create clients/$CID/protocol-mappers/models -r $REALM \
  -s 'name=roles' \
  -s 'protocol=openid-connect' \
  -s 'protocolMapper=oidc-usermodel-attribute-mapper' \
  -s 'config."user.attribute"=roles' \
  -s 'config."claim.name"=roles' \
  -s 'config."id.token.claim"=false' \
  -s 'config."access.token.claim"=false' \
  -s 'config."userinfo.token.claim"=true' \
  -s 'config."jsonType.label"=JSON'

$KCADM create clients/$CID/protocol-mappers/models -r $REALM \
  -s 'name=profiles' \
  -s 'protocol=openid-connect' \
  -s 'protocolMapper=oidc-usermodel-attribute-mapper' \
  -s 'config."user.attribute"=profiles' \
  -s 'config."claim.name"=profiles' \
  -s 'config."id.token.claim"=false' \
  -s 'config."access.token.claim"=false' \
  -s 'config."userinfo.token.claim"=true' \
  -s 'config."jsonType.label"=JSON'

$KCADM create clients/$CID/protocol-mappers/models -r $REALM \
  -s 'name=lastLogin' \
  -s 'protocol=openid-connect' \
  -s 'protocolMapper=oidc-usermodel-attribute-mapper' \
  -s 'config."user.attribute"=lastLogin' \
  -s 'config."claim.name"=lastLogin' \
  -s 'config."id.token.claim"=false' \
  -s 'config."access.token.claim"=false' \
  -s 'config."userinfo.token.claim"=true' \
  -s 'config."jsonType.label"=String'

# update frontend client
#$KCADM update clients/$CID -r $REALM -s ...

# create service client a (bearer-only)
CID=$($KCADM create clients -r $REALM -s clientId=search-service-a \
  -s bearerOnly=true -s secret=$SERVICE_CLIENT_SECRET_A -i)

# create service client b (confidential)
CID=$($KCADM create clients -r $REALM -s clientId=search-service-b \
  -s serviceAccountsEnabled=true -s secret=$SERVICE_CLIENT_SECRET_B -i)

#$KCADM update clients/$CID -r $REALM -s secret=$SERVICE_CLIENT_SECRET

# use LINZ user storage provider
$KCADM create components -r $REALM -s name=$USER_PROVIDER \
  -s providerId=$USER_PROVIDER \
  -s providerType=org.keycloak.storage.UserStorageProvider \
  -s parentId=$REALM_ID \
  -s 'config."informix.url"=["'$DB_URL'"]' \
  -s 'config."informix.username"=["'$DB_USERNAME'"]' \
  -s 'config."informix.password"=["'$DB_PASSWORD'"]'

echo "Done"
