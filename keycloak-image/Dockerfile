FROM quay.io/keycloak/keycloak:18.0.2 as builder

COPY build/libs /opt/keycloak/providers

RUN /opt/keycloak/bin/kc.sh build --db postgres

FROM quay.io/keycloak/keycloak:latest

# image permissions are keycloak:root (1000:0)

COPY --from=builder --chown=1000:0 /opt/keycloak/lib/quarkus/ /opt/keycloak/lib/quarkus/
COPY --from=builder --chown=1000:0 /opt/keycloak/providers/ /opt/keycloak/providers/

#COPY theme/ /opt/keycloak/themes/linz
WORKDIR /opt/keycloak

ENV PATH=$PATH:/opt/keycloak/bin

# NOTE: when debugging, this env and instruction is required together with "--debug" arg when running keycloak
# and obviously exposing that port when starting the container
#ENV DEBUG_PORT=*:5005
#EXPOSE 5005/tcp

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
