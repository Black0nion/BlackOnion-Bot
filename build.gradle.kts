// intellij + gradle + groovy = pain

//file:noinspection GroovyUnusedAssignment
//file:noinspection GrUnresolvedAccess
plugins {
	java
	application
	jacoco // code coverage reports
}

repositories {
	maven {
		url = uri("https://m2.dv8tion.net/releases")
		name = "m2-dv8tion"
		content {
			includeGroup = "net.dv8tion"
			includeGroup = "com.sedmelluq"
		}
	}

	maven {
		url = uri("https://m2.chew.pro/releases")
		name = "m2-chew"
		content {
			includeGroup = "pw.chew"
		}
	}

	maven {
		url = uri("https://jitpack.io")
		name = "jitpack"
	}
	mavenCentral()
}

compileJava.options.encoding = "UTF-8"

sourceCompatibility = 17
targetCompatibility = 17

sourceSets {
	testShared {
		compileClasspath += sourceSets.main.output
		runtimeClasspath += sourceSets.main.output
	}
	test {
		compileClasspath += sourceSets.testShared.output
		runtimeClasspath += sourceSets.testShared.output
	}
	testIntegration {
		compileClasspath += sourceSets.testShared.output + sourceSets.main.output
		runtimeClasspath += sourceSets.testShared.output + sourceSets.main.output
	}
}

dependencies {
	val testsImplementation = {
		testImplementation it
		testIntegrationImplementation it
		testSharedImplementation = it
	}

	implementation("com.google.guava:guava:31.1-jre")

	implementation("com.google.code.gson:gson:2.9.1")
	implementation("com.github.Marcono1234:gson-record-type-adapter-factory:v0.3.0")

	implementation("net.dv8tion:JDA:5.0.0-alpha.22")
	implementation("com.github.black0nion:Pagination-Utils:3.3.0")
	implementation("pw.chew:jda-chewtils:1.24.1")

	implementation("com.github.Mokulu:discord-oauth2-api:1.0.2")

	implementation("com.github.walkyst:lavaplayer-fork:1.3.98.4")

	implementation('io.javalin:javalin-bundle:5.1.2')

	implementation("org.json:json:20220924")

	implementation("org.mongodb:mongo-java-driver:3.12.11")

	implementation("club.minnced:discord-webhooks:0.8.2")

	implementation('se.michaelthelin.spotify:spotify-web-api-java:7.2.2')

	implementation("com.vdurmont:emoji-java:5.1.1")

	implementation("org.reflections:reflections:0.10.2")

	implementation("commons-validator:commons-validator:1.7")

	implementation("com.google.zxing:core:3.5.0")
	implementation("com.google.zxing:javase:3.5.0")

	implementation('ch.qos.logback:logback-classic:1.4.4')
	implementation("uk.org.lidalia:sysout-over-slf4j:1.0.2")
	implementation("io.prometheus:simpleclient_logback:0.16.0")
	implementation("com.github.loki4j:loki-logback-appender:1.3.2")
	implementation("org.codehaus.janino:janino:3.1.8")

	implementation("io.github.classgraph:classgraph:4.8.149")

	implementation('io.prometheus:simpleclient:0.16.0')
	implementation('io.prometheus:simpleclient_hotspot:0.16.0')
	implementation('io.prometheus:simpleclient_httpserver:0.16.0')

	testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
	testImplementation("com.github.erosb:everit-json-schema:1.14.1")
	testImplementation("org.mockito:mockito-core:4.8.1")
}

tasks.test {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.testIntegration {
	description = 'Runs integration tests.'
	group = 'verification'

	testClassesDirs = sourceSets.testIntegration.output.classesDirs
	classpath = sourceSets.testIntegration.runtimeClasspath

}

test.dependsOn testIntegration // integration tests are part of the default test task

		tasks.jacocoTestReport {
			group = "Reporting"
			description = "Generate Jacoco coverage reports after running tests."
			reports {
				xml.required.set(true)
				html.required.set(true)
			}
			finalizedBy("jacocoTestCoverageVerification")
		}

configurations { all { exclude(group = "org.slf4j", module = "slf4j-log4j12") } }

configurations {
	testIntegrationImplementation.extendsFrom implementation
			testIntegrationRuntimeOnly.extendsFrom runtimeOnly

			testSharedImplementation.extendsFrom implementation
			testSharedRuntimeOnly.extendsFrom runtimeOnly
}


mainClassName = "com.github.black0nion.blackonionbot.Main"

version = System.getenv("VERSION") ?: "dev"
processResources {
	val locAndFiles = getLoc()

	filesMatching("bot.metadata.json") {
		expand(
			version: version,
			lines_of_code: locAndFiles.get(0),
			files: locAndFiles.get(1)
		)
	}
}

tasks.named<Jar>("jar") {
	archiveFileName.set("")
}

/**
 * This task downloads all dependencies (with transitive dependencies) and puts them into the libraries folder.
 * Used instead of shadowJar to hopefully optimize build times.
 * Run the application jar with the downloaded library files in the classpath.
 */
tasks.register("downloadDependencies") {
	doLast {
		logger.info("===== Downloading dependencies =====")
		logger.info("  ---       Cleaning up...     ---")
		// delete superseded library jars
		val newFiles : Set<String> = sourceSets . main . runtimeClasspath . getFiles ().stream().map(File::getName).toList()
		fileTree("libraries").files.stream().map(File::getName).filter {
		logger.info("  -> Deleting leftover file: ${file.getName()}...")
			!newFiles.contains(it) }.forEach { file("libraries/$it").delete()
		}
		logger.info("  ---     Cleanup complete.    ---")

		logger.info("\n  ---  Copying dependencies... ---")
		copy {
			from(sourceSets.main.runtimeClasspath) {
				include "*.jar"
			}
			into 'libraries/'
			eachFile {
				logger.info("  -> Copying ${it.name}...")
			}
		}
		logger.info("  ---    Dependencies copied.  ---")
		logger.info("===== Dependencies downloaded ======")
	}
}

val getLoc () {
	int linesOfCode = 0
	int filesCount = 0
	project.sourceSets.main.allSource.srcDirs.each {
		File dir ->
		if (dir.isDirectory()) {
			dir.eachFileRecurse {
				File file ->
				if (file.isFile()) {
					file.eachLine(e -> linesOfCode++)
					filesCount++
				}
			}
		}
	}
	return [linesOfCode, filesCount]
}
