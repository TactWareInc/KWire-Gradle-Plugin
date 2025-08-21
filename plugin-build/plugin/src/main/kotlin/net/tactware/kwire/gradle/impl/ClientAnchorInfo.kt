package net.tactware.kwire.gradle.impl

internal data class ClientAnchorInfo(
    val packageName: String,
    val abstractClassName: String,
    val interfaceFqn: String,
    val explicitServiceName: String?,
    val generateFactory: Boolean
)
