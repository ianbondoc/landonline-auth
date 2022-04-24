#!/usr/bin/env bash

#KEYCLOAK_HOME=/c/Users/ibondoc/code/tools/keycloak-17.0.1
KEYCLOAK_HOME=/opt/keycloak
KCADM=$KEYCLOAK_HOME/bin/kcadm.sh
JQ=/tmp/keycloak/jq
KC_HOSTNAME=localhost
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin
REALM=landonline
FRONTEND_URL=http://localhost:3000
AUTH_URL=http://localhost:8080
USER_PROVIDER=linz-user-provider
DB_URL="jdbc:informix-sqli://host.docker.internal:9088/landonline_dev:OPTOFC=1;IFX_LOCK_MODE_WAIT=5;IFX_ISOLATION_LEVEL=2U;IFX_LO_READONLY=1"
DB_USERNAME=informix
DB_PASSWORD=in4mix
SERVICE_CLIENT_SECRET=pp8k7aPN2poVH1ypexeF2CDq2SpZ34WH

# login
while : ; do
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

# update frontend client
#$KCADM update clients/$CID -r $REALM -s ...

# create service client
CID=$($KCADM create clients -r $REALM -s clientId=search-service \
    -s bearerOnly=true -s secret=$SERVICE_CLIENT_SECRET -i)

#$KCADM update clients/$CID -r $REALM -s secret=$SERVICE_CLIENT_SECRET

# create user storage provider
$KCADM create components -r $REALM -s name=$USER_PROVIDER \
    -s providerId=$USER_PROVIDER \
    -s providerType=org.keycloak.storage.UserStorageProvider \
    -s parentId=$REALM_ID \
    -s 'config."informix.url"=["'$DB_URL'"]' \
    -s 'config."informix.username"=["'$DB_USERNAME'"]' \
    -s 'config."informix.password"=["'$DB_PASSWORD'"]'


echo "Done"