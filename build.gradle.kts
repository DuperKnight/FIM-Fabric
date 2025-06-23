plugins {
    java
    id("fabric-loom") version "1.10-SNAPSHOT"
    alias(libs.plugins.blossom)
    alias(libs.plugins.shadow)
    id(libs.plugins.preprocessor.get().pluginId)
}

buildscript {
    // Set loom to the correct platform
    project.extra.set("loom.platform", project.name.substringAfter("-"))
}

val modPlatform = Platform.of(project)

val mod_version: String by project
val maven_group: String by project
val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val fabric_version: String by project

val mod_name = "Fish's Integrated Minecraft"

version = mod_version
group = maven_group

preprocess {
    vars.put("MC", modPlatform.mcVersion)
    vars.put("FABRIC", if (modPlatform.isFabric) 1 else 0)
    vars.put("FORGE", if (modPlatform.isForge) 1 else 0)
    vars.put("NEOFORGE", if (modPlatform.isNeoForge) 1 else 0)
    vars.put("FORGELIKE", if (modPlatform.isForgeLike) 1 else 0)
}

sourceSets.forEach {
    val dir = layout.buildDirectory.dir("sourcesSets/$it.name")
    it.output.setResourcesDir(dir)
    it.java.destinationDirectory = dir
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
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings("net.fabricmc:yarn:${yarn_mappings}:v2")
    modImplementation("net.fabricmc:fabric-loader:${loader_version}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")


    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")
}

// Function to get the range of mc versions supported by a version we are building for.
// First value is start of range, second value is end of range or null to leave the range open
val supportedVersionRange: Pair<String, String?> = when (modPlatform.mcVersion) {
    12104 -> "1.21.4" to "1.21.4"
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

base.archivesName = "$mod_name ($prettyVersionRange-${modPlatform.loaderStr})"

tasks {
    processResources {
        val properties = mapOf(
            "version" to mod_version,
            "minecraft_version" to minecraft_version,
            "loader_version" to loader_version
        )
        inputs.properties(properties)
        filesMatching(listOf("fabric.mod.json", "META-INF/mods.toml")) {
            expand(properties)
        }
        exclude("META-INF/mods.toml", "pack.mcmeta")
    }
    shadowJar {
        archiveClassifier.set("dev")
        configurations = listOf(shade)
    }
    remapJar {
        input.set(shadowJar.get().archiveFile)
        archiveClassifier.set("")
        finalizedBy("copyJar")
    }
    register<Copy>("copyJar") {
        File("${project.rootDir}/jars").mkdir()
        from(remapJar.get().archiveFile)
        into("${project.rootDir}/jars")
    }
    clean { delete("${project.rootDir}/jars") }
}


/*def targetJavaVersion = 21
tasks.withType(JavaCompile).configureEach {
    it.options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible()) {
        it.options.release.set(targetJavaVersion)
    }
}



java {
    def javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
    withSourcesJar()
}

jar {
    from("LICENSE") {
        rename { "${it}_${project.archivesBaseName}" }
    }
}*/

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