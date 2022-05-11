FROM quay.io/keycloak/keycloak:latest as builder

COPY build/libs/jq /tmp/keycloak/jq
COPY build/libs/setup_realm.sh /tmp/keycloak/setup_realm.sh
COPY build/libs /opt/keycloak/providers
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true
#COPY realm-export.json /tmp/realm-export.json

RUN /opt/keycloak/bin/kc.sh build
#RUN /opt/keycloak/bin/kc.sh import --file /tmp/realm-export.json --override false

FROM quay.io/keycloak/keycloak:latest

# image permissions are keycloak:root (1000:0)

COPY --from=builder --chown=1000:0 /opt/keycloak/lib/quarkus/ /opt/keycloak/lib/quarkus/
#COPY --from=builder --chown=1000:0 /opt/keycloak/data/ /opt/keycloak/data/
COPY --from=builder --chown=1000:0 /opt/keycloak/providers/ /opt/keycloak/providers/
COPY --from=builder --chown=1000:0 /tmp/keycloak/jq /tmp/keycloak/jq
COPY --from=builder --chown=1000:0 /tmp/keycloak/setup_realm.sh /tmp/keycloak/setup_realm.sh
#COPY theme/ /opt/keycloak/themes/linz
WORKDIR /opt/keycloak

ENV PATH=$PATH:/opt/keycloak/bin
ENV KEYCLOAK_ADMIN=admin
ENV KEYCLOAK_ADMIN_PASSWORD=admin
ENV KC_HOSTNAME=${R53_KC_HOSTNAME:-localhost}
ENV DEBUG_PORT=*:5005

EXPOSE 5005/tcp
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev"]

# then run $ docker exec -i landonline-auth bash < setup_realm.sh