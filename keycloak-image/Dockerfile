FROM quay.io/keycloak/keycloak:18.0.2 as builder

COPY build/libs /opt/keycloak/providers

RUN /opt/keycloak/bin/kc.sh build
#RUN /opt/keycloak/bin/kc.sh build --db postgres

FROM quay.io/keycloak/keycloak:latest

# image permissions are keycloak:root (1000:0)

COPY --from=builder --chown=1000:0 /opt/keycloak/lib/quarkus/ /opt/keycloak/lib/quarkus/
COPY --from=builder --chown=1000:0 /opt/keycloak/providers/ /opt/keycloak/providers/

#COPY theme/ /opt/keycloak/themes/linz
WORKDIR /opt/keycloak

ENV PATH=$PATH:/opt/keycloak/bin
ENV KEYCLOAK_ADMIN=admin
ENV KEYCLOAK_ADMIN_PASSWORD=admin
ENV KC_HOSTNAME=localhost
ENV DEBUG_PORT=*:5005

EXPOSE 5005/tcp
#ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev"]
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev", "--db", "postgres", "--db-url", "jdbc:postgresql://host.docker.internal:5432/keycloak", "--db-username", "postgres", "--db-password", "postgres"]

# then run $ docker exec -i landonline-auth bash < setup_realm.sh