plugins {
    kotlin("jvm") version "1.6.20"
    id("io.ebean") version "13.3.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compileOnly(platform("org.keycloak.bom:keycloak-spi-bom:17.0.1"))
    compileOnly("org.keycloak:keycloak-core")
    compileOnly("org.keycloak:keycloak-server-spi")
    compileOnly("org.keycloak:keycloak-server-spi-private:17.0.1")
    // this was added for debugging purpose only
    compileOnly("org.keycloak:keycloak-services:17.0.1")
    compileOnly("org.keycloak:keycloak-common:17.0.1")
    implementation("io.ebean:ebean:13.3.0")
    implementation("commons-codec:commons-codec:1.15")
    runtimeOnly("com.ibm.informix:jdbc:4.50.7.1")
}

tasks {
    register<Copy>("assembleRuntimeLibs") {
        dependsOn(jar)
        destinationDir = file("$buildDir/libs")
        from(configurations["runtimeClasspath"])
    }
}