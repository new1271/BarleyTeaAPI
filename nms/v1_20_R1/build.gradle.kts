plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.11"
}

group = "org.ricetea"
version = "1.0"


java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    // paperweight.foliaDevBundle("1.20.4-R0.1-SNAPSHOT")
    // paperweight.devBundle("com.example.paperfork", "1.20.4-R0.1-SNAPSHOT")
    implementation(project(":api"))
    implementation(project(":main"))
}

tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}

tasks.register("prepareKotlinBuildScriptModel") {}