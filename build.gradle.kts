import com.replaymod.gradle.preprocess.PreprocessTask

plugins {
    java
    alias(libs.plugins.blossom)
    alias(libs.plugins.shadow)
    id("fabric-loom") version "1.10-SNAPSHOT"
    id(libs.plugins.preprocessor.get().pluginId)
}

val modPlatform = Platform.of(project)

val mod_version: String by project
val maven_group: String by project
val minecraft_version: String by project
val loader_version: String by project

val mod_name = "Fish's Integrated Minecraft"

version = mod_version
group = maven_group

val targetJavaVersion = 21

blossom {
    replaceToken("@NAME@", mod_name)
    replaceToken("@ID@", "fim")
    replaceToken("@VERSION@", mod_version)
}

preprocess {
    vars.put("MC", modPlatform.mcVersion)
    vars.put("FABRIC", if (modPlatform.isFabric) 1 else 0)
    vars.put("FORGE", if (modPlatform.isForge) 1 else 0)
    vars.put("NEOFORGE", if (modPlatform.isNeoForge) 1 else 0)
    vars.put("FORGELIKE", if (modPlatform.isForgeLike) 1 else 0)

    keywords.set(mapOf(
        ".java" to PreprocessTask.DEFAULT_KEYWORDS
    ))
}

loom {
    splitEnvironmentSourceSets()
    mods {
        create("fim") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.getByName("client"))
        }
    }
}

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }

    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    //withSourcesJar()
}


fabricApi {
    configureDataGeneration {
        client = true
    }
}

repositories {
    maven("https://maven.neoforged.net/releases")
}

dependencies {
    /*minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings("net.fabricmc:yarn:${yarn_mappings}:v2")
    modImplementation("net.fabricmc:fabric-loader:${loader_version}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")*/

    minecraft("com.mojang:minecraft:${modPlatform.mcVersionStr}")

    val (yarnVersion, fabricVersion) = when (modPlatform.mcVersion) {
        12104 -> "1.21.4+build.8" to "0.119.2+1.21.4"
        12105 -> "1.21.5+build.1" to "0.128.0+1.21.5"
        else -> error("No mappings defined for ${modPlatform.mcVersion}")
    }

    mappings("net.fabricmc:yarn:${yarnVersion}:v2")

    modImplementation("net.fabricmc:fabric-loader:${loader_version}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")
}

// Function to get the range of mc versions supported by a version we are building for.
// First value is start of range, second value is end of range or null to leave the range open
val supportedVersionRange: Pair<String, String?> = when (modPlatform.mcVersion) {
    12104 -> "1.21.4" to "1.21.4"
    12105 -> "1.21.5" to "1.21.5"
    else -> error("Undefined version range for ${modPlatform.mcVersion}")
}

val prettyVersionRange: String =
    if (supportedVersionRange.first == supportedVersionRange.second) supportedVersionRange.first
    else "${supportedVersionRange.first}${supportedVersionRange.second?.let { "-$it" } ?: "+"}"

val fabricMcVersionRange: String =
    ">=${supportedVersionRange.first}${supportedVersionRange.second?.let { " <=$it" } ?: ""}"

val forgeMcVersionRange: String =
    "[${supportedVersionRange.first},${supportedVersionRange.second?.let { "$it]" } ?: ")"}"

val shade: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

val archives_base_name: String by project
base.archivesName = "$archives_base_name ($prettyVersionRange)"

tasks {
    processResources {
        val properties = mapOf(
            "version" to mod_version,
            "minecraft_version" to minecraft_version,
            "loader_version" to loader_version
        )

        filesMatching(listOf("fabric.mod.json", "META-INF/mods.toml")) {
            expand(properties)
        }

        inputs.properties(properties)
        exclude("META-INF/mods.toml", "pack.mcmeta")
    }
    shadowJar {
        archiveClassifier.set("dev")
        configurations = listOf(shade)
    }
    remapJar {
        //input.set(shadowJar.get().archiveFile)
        //archiveClassifier.set("")
        finalizedBy("copyJar")
    }
    register<Copy>("copyJar") {
        File("${project.rootDir}/jars").mkdir()
        from(remapJar.get().archiveFile)
        into("${project.rootDir}/jars")
        delete("${project.rootDir}/versions/{$archives_base_name}/build/preprocessed")
    }
    clean { delete("${project.rootDir}/jars") }
}

data class Platform(
    val mcMajor: Int,
    val mcMinor: Int,
    val mcPatch: Int,
    val loader: Loader
) {
    val mcVersion = mcMajor * 10000 + mcMinor * 100 + mcPatch
    val mcVersionStr = listOf(mcMajor, mcMinor, mcPatch).dropLastWhile { it == 0 }.joinToString(".")
    val loaderStr = loader.toString().lowercase()

    val isFabric = loader == Loader.Fabric
    val isForge = loader == Loader.Forge
    val isNeoForge = loader == Loader.NeoForge
    val isForgeLike = loader == Loader.Forge || loader == Loader.NeoForge
    val isLegacy = mcVersion <= 11202

    override fun toString(): String {
        return "$mcVersionStr-$loaderStr"
    }

    enum class Loader {
        Fabric,
        Forge,
        NeoForge
    }

    companion object {
        fun of(project: Project): Platform {
            val (versionStr, loaderStr) = project.name.split("-", limit = 2)
            val (major, minor, patch) = versionStr.split('.').map { it.toInt() } + listOf(0)
            val loader = Loader.values().first { it.name.lowercase() == loaderStr.lowercase() }
            return Platform(major, minor, patch, loader)
        }
    }
}