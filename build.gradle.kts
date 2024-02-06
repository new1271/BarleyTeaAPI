import java.util.*

plugins {
    `java-library`
}

version = "1.0-SNAPSHOT"

dependencies {
    project(":api")
    project(":main")
    project(":nms:v1_20_R1")
    project(":nms:v1_20_R2")
    project(":nms:v1_20_R3")
}

val allProjects = listOf(project(":api"),
        project(":main"),
        project(":nms:v1_20_R1"),
        project(":nms:v1_20_R2"),
        project(":nms:v1_20_R3"))

tasks {
    jar {
        duplicatesStrategy = DuplicatesStrategy.WARN
        val layoutDir = File(project.buildDir.path, "libs")
        if (!layoutDir.exists())
            layoutDir.mkdir()
        logger.lifecycle(layoutDir.path)
        allProjects.parallelStream().map {
            it.tasks["assemble"]
        }.filter(Objects::nonNull).forEach {
            dependsOn(it)
            it.doLast {
                val projectBuildDir = File(it.project.buildDir.path, "libs")
                projectBuildDir.listFiles()?.forEach {
                    if (it.isFile && !it.name.endsWith("dev.jar")) {
                        logger.lifecycle("Include ${it.path}")
                        from(zipTree(it))
                    }
                }
            }
        }

    }
}
