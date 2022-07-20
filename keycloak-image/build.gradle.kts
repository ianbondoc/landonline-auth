plugins {
    id("com.palantir.docker") version "0.33.0"
    id("com.palantir.docker-run") version "0.33.0"
}

group = "nz.govt.linz"
version = "1.0-SNAPSHOT"

docker {
    name = "landonline-auth:latest"
    copySpec.from(project(":user-storage-provider").getTasksByName("assembleRuntimeLibs", false))
        .into("build/libs")
    copySpec.from(project(":keycloak-theme").getTasksByName("jar", false)).into("build/libs")
}

dockerRun {
    name = "landonline-auth"
    image = "landonline-auth:latest"
    // although keycloak would be listening at 8080, we map this to 80 to simulate that it's behind a proxy since it
    // will be generating urls based on KC_HOSTNAME and assumes standard port (no port in url)
    ports("80:8080")
    // volumes(mapOf("theme" to "/opt/keycloak/themes/linz"))
    volumes(mapOf("." to "/tmp/keycloak"))
    env(
        mapOf(
            // if keycloak is to be pointed to an empty database then it should be started with admin credentials
            // "KEYCLOAK_ADMIN" to "admin",
            // "KEYCLOAK_ADMIN_PASSWORD" to "changeme",

            // it is important that the hostname matches what the clients will be connecting to (considering internal
            // vs external url) while the admin hostname is different
            "KC_HOSTNAME" to "keycloak.public",
            "KC_HOSTNAME_ADMIN" to "keycloak.admin",
            "KC_HOSTNAME_STRICT_HTTPS" to "false",
            "KC_PROXY" to "edge",
            "KC_DB_URL" to "jdbc:postgresql://host.docker.internal:5432/keycloak",
            "KC_DB_USERNAME" to "postgres",
            "KC_DB_PASSWORD" to "postgres"
        )
    )
    clean = true
    // command("start", "--debug")
    command("start")
}

val setupRealm = tasks.register<Exec>("setupRealm") {
    workingDir = projectDir
    // or docker exec -i landonline-auth bash < setup_realm.sh
    commandLine("docker", "exec", "landonline-auth", "/tmp/keycloak/setup_realm.sh")
}

// run setupRealm manually but first start the postgres db
// docker run --rm --name keycloak-postgres -e POSTGRES_DB=keycloak -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -d -p 5432:5432 postgres:10.21-alpine
// tasks.getByName("dockerRun").finalizedBy(setupRealm)