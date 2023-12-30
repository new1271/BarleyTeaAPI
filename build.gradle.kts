import java.util.*

plugins {
}

version = "1.0"

dependencies {
    project(":BarleyTeaAPI")
    project(":nms:BarleyTeaAPI-NMS-Supports")
}

val allProjects = listOf(project(":BarleyTeaAPI"), project(":nms:BarleyTeaAPI-NMS-Supports"))

val publish: TaskProvider<Task> = tasks.register("publish") {
    val layoutDir = File(project.buildDir.path, "libs")

    logger.lifecycle(layoutDir.path)

    allProjects.forEach {
        val projectBuildDir = File(it.buildDir.path, "libs")
        projectBuildDir.listFiles()?.forEach {
            if (it.isFile) {
                val dest = File(layoutDir.path, it.name)
                logger.lifecycle("Copy ${it.name} into ${dest.path}")
                it.copyTo(dest, overwrite = true)
            }
        }
    }
}

tasks {
    publish {
        allProjects.parallelStream().map {
            it.tasks["assemble"]
        }.filter(Objects::nonNull).forEach {
            dependsOn(it)
        }
    }
}
