# Semantic Versioning Gradle Plugin 🐘

[![Publish Plugin to Portal](https://github.com/kmbisset89/SemVer/actions/workflows/publish-plugin.yaml/badge.svg)](https://github.com/kmbisset89/SemVer/actions/workflows/publish-plugin.yaml) [![License](https://img.shields.io/github/license/cortinico/kotlin-android-template.svg)](LICENSE) ![Language](https://img.shields.io/github/languages/top/cortinico/kotlin-android-template?color=blue&logo=kotlin)

This repository contains a Gradle plugin designed to automate the management of project versions
following [Semantic Versioning (SemVer)](https://semver.org/) principles. The plugin provides tasks to bump major,
minor, patch, and release candidate versions based on the project's current version and its Git history.

## How to use 👣

### Installation

To use the plugin, add the following to your project's `build.gradle.kts` file:

```kotlin
plugins {
    id("io.github.kmbisset89.semver.plugin") version "1.1.1"
}
```

For libs.toml:

```toml
[versions]
  semver-plugin = "1.1.1"

[plugins]
semver-plugin = { id = "io.github.kmbisset89.semver.plugin", version.ref = "semver-plugin" }
```

### Configuration

By default the plugin attempts to read configuration from Gradle properties, environment variables and a
`local.properties` file if one exists. This means you can simply apply the plugin and it will work with those values.
If you need to override any defaults you can still use the `semVerConfig` extension:


***I recommend storing these in local.properties and not in your build.gradle file. This will prevent your sensitive
information from being checked into source control.***

```properties
gitPat=yourPersonalAccessToken
gitDir=path/to/your/cloned/repo
gitEmail=yourGitEmail
```

Then in your build.gradle file you can override specific values as needed. The `local.properties` file is not checked
into source control by default, keeping your sensitive information private. The gitDirectory is here because sometimes
the git directory is not in the root of the project, so you can specify it here.

```kotlin
semVerConfig {
    val properties = Properties()
    properties.load(FileInputStream(rootProject.file("local.properties")))

    gitDirectory.set(properties.getProperty("gitDir"))
    baseBranchName.set("root")
    gitEmail.set(properties.getProperty("gitEmail"))
    gitPat.set(properties.getProperty("gitPat"))
    considerLocalPropertiesFile.set(true)
}
```

#### Extension Properties

- gitDirectory: Directory of the Git repository.
    - Why: Sometimes the Git directory is not in the root of the project, so you can specify it here.
- baseBranchName: The base branch for versioning.
    - Why: Sometimes the base branch is not the main branch, so you can specify it here. Such as needing to control a
      specific release branch.
- gitEmail: Email to use for Git operations.
    - Why: Sometimes the email is not set in the Git configuration, so you can specify it here. Also, reading from the
      local properties file is a good way to keep sensitive information out of the build.gradle file.
- gitPat: Personal access token for Git operations.
    - Why: The Git repository requires authentication for pushing tags, so you need specify a personal access token
      here. I wanted to make it easy to control the token without exposing it.
- considerLocalPropertiesFile: Flag to consider a local properties file for configuration.

All of these properties may also be supplied via Gradle properties, environment variables or a `local.properties` file
without defining the extension block.

### Usage

The main task provided by the plugin is `bumpVersion`, which increments the project's version based on the specified
bump level. The following bump levels are supported:

- `major` or `m` : Increments the major version.
- `minor` or `n` : Increments the minor version.
- `patch` or `p` : Increments the patch version.
- `rc` : Increments the release candidate version. **This is the default and is not required to be set**

Example:

```bash
./gradlew bumpVersion # Bumps the rc version
./gradlew bumpVersion -PbumpLevel=major  # Bumps the major version
```

### Behavior

Default Behavior
If no parameters are passed, the plugin defaults to incrementing the release candidate version and does not finalize the
version:

```bash
./gradlew bumpVersion #This command will increment the release candidate number
# (e.g., from 1.2.3-rc.1 to 1.2.3-rc.2).
```

#### Specifying Bump Level and Finalization

You can control the version bump level (major, minor, patch, or rc) and whether to finalize the version through Gradle
properties.

Incrementing Release Candidate Version
To increment the release candidate version without finalizing:

```bash
./gradlew bumpVersion -PbumpLevel="rc" #This command will increment the release candidate number
# (e.g., from 1.2.3-rc.1 to 1.2.3-rc.2).
```

If you then bump the patch version, the release candidate number will be reset to 1:

```bash
./gradlew bumpVersion -PbumpLevel="patch" #This command will increment the patch number
# (e.g., from 1.2.3-rc.2 to 1.2.4-rc.1).
```

If you want to finalize the version, you can do so by passing the isFinal property:

```bash
./gradlew bumpVersion  -PisFinal=true #This command will increment the release candidate number
# (e.g., from 1.2.3-rc.2 to 1.2.3).
```

If you specify a bump level of major, minor, or patch and finalize the version, the level you specify will be
incremented.

```bash
./gradlew bumpVersion -PbumpLevel="major" -PisFinal=true #This command will increment the major number
# (e.g., from 1.2.3-rc.2 to 2.0.0).
```

### Recommendations for Getting Started

If you are starting with no version history, then I recommend tagging your main branch with:

```bash
git tag "v0.1.0"
git push --tags
```

Once you have done that, you can use the gradle task from then on out.

### How It Works

1. Determine Current Version: The plugin identifies the current version based on the latest Git tag following SemVer
   principles.
2. Version Bumping: Based on the executed task, the plugin calculates the next version number.
3. Tagging and Updating: The new version is both set as the project's version and tagged in the Git repository.

## Features 🎨

- **Automatic Version Detection**: Determines the project's current version based on Git tags.
- **Version Bumping**: Supports incrementing major, minor, patch versions, and creating or incrementing release
  candidates.
- **Git Tagging**: Automatically tags the repository with the new version after bumping.
- **Customizable Git Configuration**: Allows specifying the Git directory, branch, user, and personal access token (PAT)
  for operations requiring authentication.

## Contributing 🤝

Feel free to open an issue or submit a pull request for any bugs/improvements.

## License 📄

This template is licensed under the MIT License - see the [License](License) file for details.
Please note that the generated template is offering to start with a MIT license but you can change it to whatever you
wish, as long as you attribute under the MIT terms that you're using the template.
