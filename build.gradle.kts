import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    `java-library`
    id("org.openapi.generator") version "6.4.0"
}

group = "io.github.lucjross"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

sourceSets {
    main {
        java.srcDirs("$buildDir/generated/src/main/java")
    }
}

java {
    withSourcesJar()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    implementation("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.15.2")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")

    testImplementation(kotlin("test"))
}

openApiGenerate {
    generatorName.set("java")
    inputSpec.set("${sourceSets.main.get().resources.srcDirs.first()}/openapi.yaml")
    outputDir.set("$buildDir/generated")
    apiPackage.set("com.sendbird.platform.v3.api")
    modelPackage.set("com.sendbird.platform.v3.model")
    configOptions.putAll(mapOf(
        "library" to "apache-httpclient",
        "serializationLibrary" to "jackson",
        "useJakartaEe" to "true",
        "documentationProvider" to "none",
        "openApiNullable" to "false",
        "annotationLibrary" to "none",
        "documentationProvider" to "none",
    ))
}

tasks {
    test {
        useJUnitPlatform()
    }

    jar {
        manifest {
            attributes(mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            ))
        }
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
        dependsOn(openApiGenerate)
    }
}
