plugins {
    `java-library`
    id("org.openapi.generator") version "6.4.0"
    `maven-publish`
}

group = "com.github.lucjross"
version = "1.0"

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
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.1")
    implementation("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.14.1")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")
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

    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
        dependsOn(openApiGenerate)
    }
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
