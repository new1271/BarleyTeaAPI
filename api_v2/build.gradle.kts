plugins {
    `java-library`
    `maven-publish`
}

group = "org.ricetea"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(dependencyNotation = "io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    implementation(project(":api"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    assemble {
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(21)
        options.isDebug = true
        options.debugOptions.debugLevel = "vars"
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                groupId = project.group.toString()
                description = "a multi-functional API for Paper-based server software"
                artifactId = "barleyteaapi"
                url = "https://github.com/new1271/BarleyTeaAPI"
                version = project.version.toString()
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://raw.githubusercontent.com/new1271/BarleyTeaAPI/main/LICENSE"
                    }
                }
                developers {
                    developer {
                        id = "new1271"
                        name = "Rice Tea"
                        email = "new1271@outlook.com"
                    }
                }
            }
        }
    }
}

tasks.register("prepareKotlinBuildScriptModel") {}