package net.tactware.kwire.gradle

import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import java.io.File

/**
 * Extension for configuring the Obfuscated RPC plugin.
 */
abstract class RpcGeneratorExtension {

    /** Path (relative or absolute) to where @RpcService sources live */
    var apiSourcePath: String = ""

    /** Path (relative or absolute) to where @RpcClient sources live */
    var clientSourcePath: String = ""

    var serverSourcePath: String = ""


    /**
     * Whether to enable obfuscation for generated code.
     */
    abstract val obfuscationEnabled: Property<Boolean>
    
    /**
     * Whether to generate client stubs.
     */
    abstract val generateClient: Property<Boolean>
    
    /**
     * Whether to generate server stubs.
     */
    abstract val generateServer: Property<Boolean>
    
    /**
     * File to store method ID mappings for obfuscation.
     */
    abstract val methodMappingFile: Property<File>
    
    /**
     * Package names to include in code generation.
     */
    abstract val includePackages: SetProperty<String>
    
    /**
     * Package names to exclude from code generation.
     */
    abstract val excludePackages: SetProperty<String>
    
    /**
     * Whether to generate debug information in stubs.
     */
    abstract val generateDebugInfo: Property<Boolean>
    
    /**
     * Custom serialization modules to use.
     */
    abstract val serializationModules: SetProperty<String>
    
    init {
        // Set default values
        obfuscationEnabled.convention(true)
        generateClient.convention(true)
        generateServer.convention(true)
        generateDebugInfo.convention(false)
    }
    
    /**
     * Configure packages to include.
     */
    fun includePackages(vararg packages: String) {
        includePackages.addAll(*packages)
    }
    
    /**
     * Configure packages to exclude.
     */
    fun excludePackages(vararg packages: String) {
        excludePackages.addAll(*packages)
    }
    
    /**
     * Configure serialization modules.
     */
    fun serializationModules(vararg modules: String) {
        serializationModules.addAll(*modules)
    }
}

