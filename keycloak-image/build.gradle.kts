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
    ports("8080:8080", "5005:5005")
    // volumes(mapOf("theme" to "/opt/keycloak/themes/linz"))
    volumes(mapOf("." to "/tmp/keycloak"))
    clean = true
    command("--debug")
}

val setupRealm = tasks.register<Exec>("setupRealm") {
    workingDir = projectDir
    // or docker exec -i landonline-auth bash < setup_realm.sh
    commandLine("docker", "exec", "landonline-auth", "/tmp/keycloak/setup_realm.sh")
}

// run setupRealm manually but first start the postgres db
// docker run --rm --name keycloak-postgres -e POSTGRES_DB=keycloak -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -d -p 5432:5432 postgres:10.21-alpine
// tasks.getByName("dockerRun").finalizedBy(setupRealm)