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
}

dockerRun {
    name = "landonline-auth"
    image = "landonline-auth:latest"
    ports("8080:8080", "5005:5005")
    volumes(mapOf("theme" to "/opt/keycloak/themes/linz"))
    clean = true
    command("--debug")
}