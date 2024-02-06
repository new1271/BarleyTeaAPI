plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.8.0")
}

rootProject.name = "BarleyTeaAPI"

include(":api", ":main", ":nms:v1_20_R1", ":nms:v1_20_R2", ":nms:v1_20_R3")