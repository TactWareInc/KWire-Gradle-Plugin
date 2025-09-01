package net.tactware.kwire.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Main Gradle plugin for Obfuscated RPC code generation.
 */
class RpcGeneratorPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // Create extension for plugin configuration
        val extension = project.extensions.create("rpcGenerator", RpcGeneratorExtension::class.java)

        // Register code generation tasks
        registerCodeGenerationTasks(project, extension)

        // Configure dependencies
        configureDependencies(project)
    }

    private fun registerCodeGenerationTasks(project: Project, extension: RpcGeneratorExtension) {
        // Register the main code generation task
        val generateStubsTask = project.tasks.register("generateRpcStubs", GenerateRpcStubsTask::class.java) { task ->
            task.group = "rpcGenerator"
            task.description = "Generate RPC client and server stubs"

            task.apiSourcePath.set(extension.apiSourcePath)
            task.clientSourcePath.set(extension.clientSourcePath)
            task.serverSourcePath.set(extension.serverSourcePath)

            // Configure task properties directly (no afterEvaluate needed)
            task.obfuscationEnabled.convention(extension.obfuscationEnabled)
            task.generateClient.convention(extension.generateClient)
            task.generateServer.convention(extension.generateServer)

            // Set input and output directories
            task.outputDir.set(project.layout.buildDirectory.dir("generated/rpc"))
        }

        // Make compile tasks depend on code generation
        project.tasks.named("compileKotlin") { task ->
            task.dependsOn(generateStubsTask)
        }

        // Add generated sources to source sets
        project.afterEvaluate {
            val sourceSets = project.extensions.getByName("sourceSets") as org.gradle.api.tasks.SourceSetContainer
            sourceSets.getByName("main").java.srcDir(project.layout.buildDirectory.dir("generated/rpc"))
        }
    }

    private fun configureDependencies(project: Project) {
        // Configure dependencies immediately (no afterEvaluate needed)
        project.dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
        project.dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
        project.dependencies.add("implementation", "org.slf4j:slf4j-api:2.0.9")
    }
}
