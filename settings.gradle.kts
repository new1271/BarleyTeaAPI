plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.7.0")
}

rootProject.name = "BarleyTeaAPI"

include(":main", ":nms:v1_20_R1")

project(":main").name = "BarleyTeaAPI"
project(":nms:v1_20_R1").name = "BarleyTeaAPI-NMS-Supports"