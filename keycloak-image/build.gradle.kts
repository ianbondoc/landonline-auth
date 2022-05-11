plugins {
    id("com.palantir.docker") version "0.33.0"
    id("com.palantir.docker-run") version "0.33.0"
}

group = "nz.govt.linz"
version = "1.0-SNAPSHOT"

docker {
    name = "725496895483.dkr.ecr.ap-southeast-2.amazonaws.com/step/enablement/landonline-auth-keycloak-poc:latest"
    files("jq", "setup_realm.sh") // will be copied into build/libs because of copySpec behavior. The last executed "into("path")" will be the path everything is copied into.
    copySpec.from(project(":user-storage-provider").getTasksByName("assembleRuntimeLibs", false))
        .into("build/libs")
    copySpec.from(project(":keycloak-theme").getTasksByName("jar", false))
        .into("build/libs")
}

dockerRun {
    name = "landonline-auth-keycloak"
    image = "725496895483.dkr.ecr.ap-southeast-2.amazonaws.com/step/enablement/landonline-auth-keycloak-poc:latest"
    ports("8080:8080", "5005:5005")
    // volumes(mapOf("theme" to "/opt/keycloak/themes/linz"))
    volumes(mapOf("." to "/tmp/keycloak"))
    clean = true
    command("--debug")
}

val setupRealm = tasks.register<Exec>("setupRealm") {
    workingDir = projectDir
    // or docker exec -i landonline-auth bash < setup_realm.sh
    commandLine("docker", "exec", "landonline-auth-keycloak", "/tmp/keycloak/setup_realm.sh")
}

tasks.getByName("dockerRun").finalizedBy(setupRealm)