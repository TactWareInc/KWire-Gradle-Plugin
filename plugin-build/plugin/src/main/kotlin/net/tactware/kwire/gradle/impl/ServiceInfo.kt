package net.tactware.kwire.gradle.impl

/** Data models used by the generator. Keep in sync with ServiceInfo.kt in your project. */
data class ServiceInfo(
    val serviceName: String,
    val packageName: String,
    val interfaceName: String,
    val methods: List<MethodInfo>
)

data class MethodInfo(
    val rpcMethodId: String,
    val methodName: String,
    val parameters: List<ParamInfo>,
    /** Declared return type verbatim, e.g. "User", "User?", "List<User>", "Flow<List<User>>" */
    val returnType: String,
    val isStreaming: Boolean
)

data class ParamInfo(
    val name: String,
    /** Declared parameter type verbatim */
    val type: String
)

