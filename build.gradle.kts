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
val clean: TaskProvider<Task> = tasks.register("clean") {}

tasks {
    clean {
        val layoutDir = File(project.buildDir.path, "libs")
        val publishDir = File(project.buildDir.path, "publications")
        if (layoutDir.exists())
            layoutDir.delete()
        if (publishDir.exists())
            publishDir.delete()
        allProjects.parallelStream().map {
            it.tasks["clean"]
        }.filter(Objects::nonNull).forEach {
            dependsOn(it)
        }
    }

    publish {
        val layoutDir = File(project.buildDir.path, "libs")
        val publishDir = File(project.buildDir.path, "publications")
        if (!layoutDir.exists())
            layoutDir.mkdir()
        if (!publishDir.exists())
            publishDir.mkdir()
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
                        it.copyTo(dest)
                        logger.lifecycle("Copied ${it.name} into ${dest.path}")
                    }
                }
            }
        }
        val mainProject = allProjects.first()
        val task = mainProject.tasks["publishToMavenLocal"]
        dependsOn(task)
        task.doLast {
            val projectPublishDir = File(mainProject.buildDir.path, "publications")
            projectPublishDir.listFiles()?.forEach {
                val dest = File(publishDir, it.name)
                it.copyRecursively(dest, overwrite = true)
            }
        }
    }
}
