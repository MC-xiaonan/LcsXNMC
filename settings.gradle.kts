pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "lcsxnmc"

include("lcsxnmc-api")
include("lcsxnmc-server")
include("lcsxnmc-checkstyle")

gradle.lifecycle.beforeProject {
    val mcVersion = providers.gradleProperty("mcVersion").get().trim()
    val lcsxnmcVersionChannel = providers.gradleProperty("channel").get().trim()
    val lcsxnmcBuildNumber = providers.environmentVariable("BUILD_NUMBER").orNull?.trim()?.toInt()
    val versionString = if (lcsxnmcBuildNumber == null) {
        "$mcVersion.local-SNAPSHOT"
    } else {
        "$mcVersion.build.$lcsxnmcBuildNumber-${lcsxnmcVersionChannel.lowercase()}"
    }
    version = versionString
}
