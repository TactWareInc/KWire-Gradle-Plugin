package net.tactware.kwire.gradle.impl

import com.squareup.kotlinpoet.ClassName

data class GenerationTarget(
    val packageName: String,
    val superType: ClassName,
    val returnType: ClassName,
    val clientSimpleName: String,
    val emitFactory: Boolean
)
