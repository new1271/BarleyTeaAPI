plugins {
    `java-library`
    `maven-publish`
}

group = "org.ricetea"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.2.0-SNAPSHOT")
    implementation(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    assemble {
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
        options.debugOptions.debugLevel = "vars"
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
        val props = mapOf(
                "version" to project.version,
                "apiVersion" to "1.20"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
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