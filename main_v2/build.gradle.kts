plugins {
    `java-library`
}

group = "org.ricetea"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencies {
    compileOnly(dependencyNotation = "io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    implementation(project(":api"))
    implementation(project(":main"))
}

tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(21)
        options.isDebug = false
        options.debugOptions.debugLevel = "none"
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}

tasks.register("prepareKotlinBuildScriptModel") {}