plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.8.0")
}

rootProject.name = "BarleyTeaAPI"

include(":api",":api_v2",
    ":main", ":main_v2",
    ":nms:v1_20_R1", ":nms:v1_20_R2", ":nms:v1_20_R3", ":nms:v1_20_R4", ":nms:v1_21_R1")