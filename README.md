# KWire Gradle Plugin

Gradle plugin for autogenerating Kotlin Wire RPC client and server stubs from annotated interfaces and anchor classes.

Version: 1.0.0

Repository: https://github.com/TactWareInc/KWire-Gradle-Plugin

## Installation

Add the plugin to your Gradle build (Kotlin DSL):

```kotlin
plugins {
    id("net.tactware.kwire.gradle") version "1.0.0"
}
```

Using version catalogs (libs.versions.toml):

```toml
[versions]
  kwire-gradle = "1.0.0"

[plugins]
kwire = { id = "net.tactware.kwire.gradle", version.ref = "kwire-gradle" }
```

## Configuration

Apply and configure the `rpcGenerator` extension to point at your source roots. At minimum, set the API source path; set client and/or server paths depending on what you want to generate.

```kotlin
rpcGenerator {
    // Required: where interfaces annotated with @RpcService live
    apiSourcePath = "api/src/main/kotlin"

    // Optional: where abstract client anchors annotated with @RpcClient live
    clientSourcePath = "client/src/main/kotlin"

    // Optional: where abstract server anchors annotated with @RpcServer live
    serverSourcePath = "server/src/main/kotlin"

    // Optional flags (defaults shown)
    obfuscationEnabled.set(true)
    generateClient.set(true)
    generateServer.set(true)
}
```

Notes:
- The plugin exposes a `generateRpcStubs` task and wires it to run before `compileKotlin` automatically.
- Generated sources are written to `build/generated/rpc` and added to the `main` source set.

## Usage

- Run generation explicitly:
  ```bash
  ./gradlew generateRpcStubs
  ```
- Or just build your project and the stubs will be generated automatically before compilation:
  ```bash
  ./gradlew build
  ```

## Expected Annotations (overview)

- `@RpcService` on Kotlin interfaces in your API module.
- `@RpcClient` on abstract client anchor classes that reference a service interface by FQN.
- `@RpcServer` on abstract server anchor classes that reference a service interface by FQN.

The plugin parses annotated sources and generates concrete `Impl` classes into the configured output directory.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
