/**
 * This task downloads all dependencies (with transitive dependencies) and puts them into the
 * libraries folder. Used instead of shadowJar to hopefully optimize build times. Run the
 * application jar with the downloaded library files in the classpath.
 */
tasks.register("downloadDependencies") {
    doLast {
        logger.info("===== Downloading dependencies =====")
        logger.info("  ---       Cleaning up...     ---")
        // delete superseded library jars
        // val newFiles : Set<String> = sourceSets . main . runtimeClasspath . getFiles
        // ().stream().map(File::getName).toList()
        fileTree("libraries")
            .files
            .stream()
            .map(File::getName)
            .filter {
                logger.info("  -> Deleting leftover file: ${file.getName()}...")
                !newFiles.contains(it)
            }
            .forEach { file("libraries/$it").delete() }
        logger.info("  ---     Cleanup complete.    ---")

        logger.info("\n  ---  Copying dependencies... ---")
        copy {
            from(sourceSets.main.runtimeClasspath) { include("*.jar") }
            into("libraries/")
            eachFile { logger.info("  -> Copying ${it.name}...") }
        }
        logger.info("  ---    Dependencies copied.  ---")
        logger.info("===== Dependencies downloaded ======")
    }
}
