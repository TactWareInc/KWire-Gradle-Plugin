package net.tactware.kwire.gradle

import net.tactware.kwire.gradle.impl.AnnotationParser
import net.tactware.kwire.gradle.impl.ServiceClientGenerator
import net.tactware.kwire.gradle.impl.ServiceServerGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File

/**
 * Gradle task for generating RPC client and server stubs using annotation parsing.
 */
@CacheableTask
abstract class GenerateRpcStubsTask : DefaultTask() {

    @get:Input abstract val apiSourcePath: Property<String>
    @get:Input abstract val clientSourcePath: Property<String>

    @get:Input abstract val serverSourcePath: Property<String>


    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val obfuscationEnabled: Property<Boolean>

    @get:Input
    abstract val generateClient: Property<Boolean>

    @get:Input
    abstract val generateServer: Property<Boolean>

    @TaskAction
    fun generateStubs() {
        val outputDirectory = outputDir.get().asFile
        outputDirectory.deleteRecursively()
        outputDirectory.mkdirs()

        logger.lifecycle("Generating RPC stubs in: ${outputDirectory.absolutePath}")
        logger.lifecycle("Obfuscation enabled: ${obfuscationEnabled.get()}")
        logger.lifecycle("Generate client: ${generateClient.get()}")
        logger.lifecycle("Generate server: ${generateServer.get()}")

        // Debug: List all source files being processed
        val apiFiles = collectSourceFiles(File(apiSourcePath.get()))
        logger.lifecycle("Processing ${apiFiles.files.size} source files:")
        apiFiles.files.forEach { file ->
            logger.lifecycle("  - ${file.absolutePath}")
        }

        val clientFiles = collectSourceFiles(File(clientSourcePath.get()))
        logger.lifecycle("Processing ${clientFiles.files.size} client source files:")
        clientFiles.files.forEach { file ->
            logger.lifecycle("  - ${file.absolutePath}")
        }

        val serverFiles = collectSourceFiles(File(serverSourcePath.get()))
        logger.lifecycle("Processing ${serverFiles.files.size} server source files:")
        serverFiles.files.forEach { file ->
            logger.lifecycle("  - ${file.absolutePath}")
        }

        try {
            // Parse source files to find @RpcService annotated interfaces
            val parser = AnnotationParser()
            val services = parser.parseServices(apiFiles)
            val servicesByInterface = services.associateBy { "${it.packageName}.${it.interfaceName}" }

// 2) Parse client anchors from client module
            val anchors =if (!clientFiles.isEmpty()) {
              parser.parseClientAnchors(clientFiles)
            } else {
                logger.warn("No client source files found at ${clientSourcePath.get()}. Skipping client anchor parsing.")
                emptyList()
            }

            val serverAnchors =  if (!serverFiles.isEmpty()) {
                parser.parseServerAnchors(serverFiles)
            } else{
                logger.warn("No server source files found at ${serverSourcePath.get()}. Skipping server anchor parsing.")
                emptyList()
            }

            if (services.isEmpty()) {
                logger.warn("No @RpcService annotated interfaces found in source files")
                logger.warn("Checked files:")
                apiFiles.files.forEach { file ->
                    logger.warn("  - ${file.name}: ${if (file.readText().contains("@RpcService")) "HAS @RpcService" else "no @RpcService"}")
                }
            }

            if (anchors.isEmpty()){
                logger.warn("No client anchors found in source files")
                logger.warn("Checked files:")
                clientFiles.files.forEach { file ->
                    logger.warn("  - ${file.name}: ${if (file.readText().contains("@RpcClient")) "HAS @RpcClient" else "no @RpcClient"}")
                }
            }

            if (serverAnchors.isEmpty()){
                logger.warn("No server anchors found in source files")
                logger.warn("Checked files:")
                serverFiles.files.forEach { file ->
                    logger.warn("  - ${file.name}: ${if (file.readText().contains("@RpcServer")) "HAS @RpcServer" else "no @RpcServer"}")
                }
            }

            logger.lifecycle("Found ${services.size} RPC services: ${services.map { "${it.interfaceName} (${it.serviceName})" }}")
            logger.lifecycle("Found ${anchors.size} client anchors: ${anchors.map { "${it.abstractClassName} (${it.interfaceFqn})" }}")
            val generator = ServiceClientGenerator()
            anchors.forEach { anchor ->
                val svc = servicesByInterface[anchor.interfaceFqn]
                    ?: error("No @RpcService found for interface ${anchor.interfaceFqn} used by ${anchor.abstractClassName}")
                val adjusted = svc

                val src = generator.generate(adjusted, anchor)

                val outputDir = outputDir.get().asFile
                val packageDir = File(outputDir, anchor.packageName.replace('.', '/'))
                packageDir.mkdirs()
                val outputFile = File(packageDir, "${anchor.abstractClassName}Impl.kt")
                outputFile.writeText(src)
            }

            val serverGenerator = ServiceServerGenerator()
            serverAnchors.forEach { anchor ->
                val svc = servicesByInterface[anchor.interfaceFqn]
                    ?: error("No @RpcService found for interface ${anchor.interfaceFqn} used by ${anchor.abstractClassName}")
                val adjusted = svc

                val src = serverGenerator.generate(adjusted, anchor)

                val outputDir = outputDir.get().asFile
                val packageDir = File(outputDir, anchor.packageName.replace('.', '/'))
                packageDir.mkdirs()
                val outputFile = File(packageDir, "${anchor.abstractClassName}Impl.kt")
                outputFile.writeText(src)
            }
        } catch (e: Exception) {
            logger.error("Error generating RPC stubs: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    private fun collectSourceFiles(dir: File): FileCollection {
        if (!dir.exists()) {
            logger.warn("Source path does not exist: $dir")
            return project.files()
        }
        return project.fileTree(dir) { it.include("**/*.kt") }
    }
}
