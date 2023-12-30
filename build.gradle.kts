import java.util.*

plugins {
}

version = "1.0"

dependencies {
    project(":BarleyTeaAPI")
    project(":nms:BarleyTeaAPI-NMS-Supports")
}

val allProjects = listOf(project(":BarleyTeaAPI"), project(":nms:BarleyTeaAPI-NMS-Supports"))

val publish: TaskProvider<Task> = tasks.register("publish") {}

tasks {
    publish {
        val layoutDir = File(project.buildDir.path, "libs")
        logger.lifecycle(layoutDir.path)
        allProjects.parallelStream().map {
            it.tasks["assemble"]
        }.filter(Objects::nonNull).forEach {
            dependsOn(it)
            it.doLast {
                val projectBuildDir = File(it.project.buildDir.path, "libs")
                projectBuildDir.listFiles()?.forEach {
                    if (it.isFile) {
                        val dest = File(layoutDir.path, it.name)
                        dest.delete()
                        it.renameTo(dest)
                        logger.lifecycle("Copied ${it.name} into ${dest.path}")
                    }
                }
            }
        }
    }
}
