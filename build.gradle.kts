import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java // TODO java launcher tasks
    id("io.papermc.paperweight.patcher") version "2.0.0-beta.21"
}

paperweight {
    filterPatches = false
    upstreams.paper {
        ref = providers.gradleProperty("paperRef")

        patchFile {
            path = "paper-server/build.gradle.kts"
            outputFile = file("lcsxnmc-server/build.gradle.kts")
            patchFile = file("lcsxnmc-server/build.gradle.kts.patch")
        }
        patchFile {
            path = "paper-api/build.gradle.kts"
            outputFile = file("lcsxnmc-api/build.gradle.kts")
            patchFile = file("lcsxnmc-api/build.gradle.kts.patch")
        }
        patchFile {
            path = "paper-checkstyle/build.gradle.kts"
            outputFile = file("lcsxnmc-checkstyle/build.gradle.kts")
            patchFile = file("lcsxnmc-checkstyle/build.gradle.kts.patch")
        }
        patchDir("paperApi") {
            upstreamPath = "paper-api"
            excludes = setOf("build.gradle.kts")
            patchesDir = file("lcsxnmc-api/paper-patches")
            outputDir = file("paper-api")
        }
        patchDir("paperCheckstyle") {
            upstreamPath = "paper-checkstyle"
            excludes = setOf("build.gradle.kts")
            patchesDir = file("lcsxnmc-checkstyle/paper-patches")
            outputDir = file("paper-checkstyle")
        }
        patchDir("paperCheckstyleConfig") {
            upstreamPath = ".checkstyle"
            patchesDir = file("lcsxnmc-checkstyle/config-patches")
            outputDir = file(".checkstyle")
        }
    }
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }

    repositories {
        maven("https://maven.aliyun.com/repository/public/")
        mavenCentral()
        maven(paperMavenPublicUrl)
    }

    dependencies {
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
    tasks.withType<JavaCompile>().configureEach  {
        options.encoding = Charsets.UTF_8.name()
        options.release = 25
        options.isFork = true
    }
    tasks.withType<Javadoc>().configureEach  {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources>().configureEach  {
        filteringCharset = Charsets.UTF_8.name()
    }
    tasks.withType<Test>().configureEach  {
        testLogging {
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
            events(TestLogEvent.STANDARD_OUT)
        }
    }

    extensions.configure<PublishingExtension> {
        repositories {
            maven("https://artifactory.papermc.io/artifactory/releases/") {
                name = "paperReleases"
                credentials(PasswordCredentials::class)
            }
        }
    }
}

tasks.register("printMinecraftVersion") {
    doLast {
        println(providers.gradleProperty("mcVersion").get().trim())
    }
}

tasks.register("printLcsxnmcVersion") {
    doLast {
        println(project.version)
    }
}
