plugins {
    java
    application
    jacoco // code coverage reports
    id("com.diffplug.spotless") version "6.11.0"
}

repositories {
    maven {
        url = uri("https://m2.dv8tion.net/releases")
        name = "m2-dv8tion"
        content {
            includeGroup("net.dv8tion")
            includeGroup("com.sedmelluq")
        }
    }

    maven {
        url = uri("https://m2.chew.pro/releases")
        name = "m2-chew"
        content { includeGroup("pw.chew") }
    }

    maven {
        url = uri("https://jitpack.io")
        name = "jitpack"
    }
    mavenCentral()
}

sourceSets {
    create("testShared") {
        compileClasspath += sourceSets["main"].output
        runtimeClasspath += sourceSets["main"].output
    }
    named("test") {
        compileClasspath += sourceSets["testShared"].output
        runtimeClasspath += sourceSets["testShared"].output
    }
    create("testIntegration") {
        compileClasspath += sourceSets["testShared"].output + sourceSets["main"].output
        runtimeClasspath += sourceSets["testShared"].output + sourceSets["main"].output
    }
}

dependencies {
    val testsImplementation = { dependencyNotation: Any ->
        "testImplementation"(dependencyNotation)
        "testIntegrationImplementation"(dependencyNotation)
        "testSharedImplementation"(dependencyNotation)
    }

    implementation("com.google.guava:guava:31.1-jre")

    implementation("com.google.code.gson:gson:2.9.1")
    implementation("com.github.Marcono1234:gson-record-type-adapter-factory:v0.3.0")

    implementation("net.dv8tion:JDA:5.0.0-alpha.22")
    implementation("com.github.black0nion:Pagination-Utils:3.3.0")
    implementation("pw.chew:jda-chewtils:1.24.1")

    implementation("com.github.Mokulu:discord-oauth2-api:1.0.2")

    implementation("com.github.walkyst:lavaplayer-fork:1.3.98.4")

    implementation("io.javalin:javalin-bundle:5.1.3")

    implementation("org.json:json:20220924")

    implementation("org.mongodb:mongo-java-driver:3.12.11")

    implementation("club.minnced:discord-webhooks:0.8.2")

    implementation("se.michaelthelin.spotify:spotify-web-api-java:7.2.2")

    implementation("com.vdurmont:emoji-java:5.1.1")

    implementation("org.reflections:reflections:0.10.2")

    implementation("commons-validator:commons-validator:1.7")

    implementation("com.google.zxing:core:3.5.1")
    implementation("com.google.zxing:javase:3.5.1")

    implementation("ch.qos.logback:logback-classic:1.4.4")
    implementation("uk.org.lidalia:sysout-over-slf4j:1.0.2")
    implementation("io.prometheus:simpleclient_logback:0.16.0")
    implementation("com.github.loki4j:loki-logback-appender:1.3.2")
    implementation("org.codehaus.janino:janino:3.1.8")

    implementation("io.github.classgraph:classgraph:4.8.149")

    implementation("io.prometheus:simpleclient:0.16.0")
    implementation("io.prometheus:simpleclient_hotspot:0.16.0")
    implementation("io.prometheus:simpleclient_httpserver:0.16.0")

    testsImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
    testsImplementation("com.github.erosb:everit-json-schema:1.14.1")
    testsImplementation("org.mockito:mockito-core:4.8.1")
}

configurations { all { exclude(group = "org.slf4j", module = "slf4j-log4j12") } }

application { mainClass.set("com.github.black0nion.blackonionbot.Main") }

version = System.getenv("VERSION") ?: "dev"

tasks.named<Jar>("jar") { archiveVersion.set("") }

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
    // dependsOn testIntegration
    dependsOn(testIntegration)
}

val testIntegration by
    tasks.registering(Test::class) {
        description = "Runs integration tests."
        group = "verification"

        testClassesDirs = sourceSets["testIntegration"].output.classesDirs
        classpath = sourceSets["testIntegration"].runtimeClasspath
        shouldRunAfter(tasks.test)
        useJUnitPlatform()
    }

spotless {
    kotlinGradle {
        target("**/*.gradle.kts")
        ktfmt("0.39").dropboxStyle()
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
}

tasks.jacocoTestReport {
    group = "Reporting"
    description = "Generate Jacoco coverage reports after running tests."
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    finalizedBy("jacocoTestCoverageVerification")
}

configurations {
    getByName("testIntegrationImplementation").extendsFrom(implementation.get())
    getByName("testIntegrationRuntimeOnly").extendsFrom(runtimeOnly.get())

    getByName("testSharedImplementation").extendsFrom(implementation.get())
    getByName("testSharedRuntimeOnly").extendsFrom(runtimeOnly.get())
}

/**
 * This task downloads all dependencies (with transitive dependencies) and puts them into the
 * libraries folder. Used instead of shadowJar to hopefully optimize build times. Run the
 * application jar with the downloaded library files in the classpath.
 */
tasks.register("downloadDependencies") {
    description = "Downloads all dependencies and puts them into the libraries folder."
    group = "build"

    doLast {
        logger.lifecycle("Downloading dependencies...")
        val dependencies =
            configurations["runtimeClasspath"].resolvedConfiguration.resolvedArtifacts
        val librariesFolder = File("libraries")
        librariesFolder.mkdirs()
        dependencies.forEach { artifact ->
            logger.lifecycle("Downloading ${artifact.file.name}...")
            val file = artifact.file
            val targetFile = File(librariesFolder, file.name)
            if (!targetFile.exists()) {
                file.copyTo(targetFile)
            }
        }

        logger.lifecycle("Done downloading dependencies.")
    }
}

tasks.processResources {
    val locAndFiles = getLoc()
    filesMatching("bot.metadata.json") {
        expand("version" to version, "lines_of_code" to locAndFiles[0], "files" to locAndFiles[1])
    }
}

fun getLoc(): List<Int> {
    var linesOfCode = 0
    var filesCount = 0
    project.sourceSets["main"].allSource.srcDirs.forEach { dir ->
        if (dir.isDirectory) {
            dir.listFiles()?.forEach { file ->
                if (file.isFile) {
                    file.forEachLine { linesOfCode++ }
                    filesCount++
                }
            }
        }
    }
    return listOf(linesOfCode, filesCount)
}
