import java.util.*

plugins {
    `java-library`
}

version = "1.0-SNAPSHOT"

dependencies {
    project(":api")
    project(":api_v2")
    project(":main")
    project(":main_v2")
    project(":nms:v1_20_R1")
    project(":nms:v1_20_R2")
    project(":nms:v1_20_R3")
    project(":nms:v1_20_R4")
    project(":nms:v1_21_R1")
}

val allProjects = listOf(project(":api"),
        project(":api_v2"),
        project(":main"),
        project(":main_v2"),
        project(":nms:v1_20_R1"),
        project(":nms:v1_20_R2"),
        project(":nms:v1_20_R3"),
        project(":nms:v1_20_R4"),
        project(":nms:v1_21_R1"))

val allJar = project.tasks.register("allJar")
val mojangMappingJar = project.tasks.register("mojangMappingJar", Jar::class)

tasks {
    allJar {
        dependsOn(jar, mojangMappingJar)
    }
    jar {
        logger.lifecycle("[jar] Building JAR")
        duplicatesStrategy = DuplicatesStrategy.WARN
        val layoutDir = File(layout.buildDirectory.asFile.get().path, "libs")
        if (!layoutDir.exists())
            layoutDir.mkdir()
        logger.lifecycle("[jar] " + File(layoutDir, archiveFileName.get()).path)
        allProjects.stream().map {
            it.tasks["assemble"]
        }.filter(Objects::nonNull).forEach {
            dependsOn(it)
            it.doLast {
                val projectLayoutDir = it.project.layout.buildDirectory.asFile.get()
                val projectBuildDir = File(projectLayoutDir.path, "libs")
                val projectPath = it.project.path
                logger.lifecycle("[jar] Include $projectPath")
                projectBuildDir.listFiles()?.forEach {
                    if (it.isFile && (!projectPath.startsWith(":nms:") ||
                                    !(it.name.endsWith("dev.jar") || !it.name.endsWith("reobf.jar")))) {
                        logger.lifecycle("[jar] Include ${it.path}")
                        from(zipTree(it))
                    }
                }
            }
        }
    }
    mojangMappingJar {
        mustRunAfter(jar)
        logger.lifecycle("[mojangMappingJar] Building Mojang mapped JAR")
        duplicatesStrategy = DuplicatesStrategy.WARN
        val filename = archiveFileName.get()
        val lastDotIndex = filename.lastIndexOf('.')
        if (lastDotIndex != -1)
            archiveFileName = filename.substring(0, lastDotIndex) + "-mojang-mapped.jar"
        val layoutDir = File(layout.buildDirectory.asFile.get().path, "libs")
        if (!layoutDir.exists())
            layoutDir.mkdir()
        logger.lifecycle("[mojangMappingJar] " + File(layoutDir, archiveFileName.get()).path)
        allProjects.stream().map {
            it.tasks["assemble"]
        }.filter(Objects::nonNull).forEach {
            dependsOn(it)
            it.doLast {
                val projectLayoutDir = it.project.layout.buildDirectory.asFile.get()
                val projectBuildDir = File(projectLayoutDir.path, "libs")
                val projectPath = it.project.path
                logger.lifecycle("[mojangMappingJar] Include $projectPath")
                projectBuildDir.listFiles()?.forEach {
                    if (it.isFile && (!projectPath.startsWith(":nms:") || !it.name.endsWith("reobf.jar") ||
                                    it.name.endsWith("dev.jar"))) {
                        logger.lifecycle("[mojangMappingJar] Include ${it.path}")
                        from(zipTree(it))
                    }
                }
            }
        }
    }
}