@file:Suppress("UnstableApiUsage")

plugins {
    id(libs.plugins.loom.get().pluginId) version libs.plugins.loom.get().version.toString()
}

dependencies {
    // Minecraft
    minecraft(libs.minecraft.v20)
    mappings(loom.layered {
        officialMojangMappings()
        parchment(libs.parchment.mappings)
    })

    modCompileOnly(libs.fabric.loader)
    modCompileOnly(libs.fabric.api)

    // Project
    api(project(":command-manager-common"))

    // Dependencies
    api(libs.luckperms)
    modApi(libs.kyori.adventure.fabric)
}

tasks {
    jar {
        archiveFileName = "FabricCommandManager-$version.jar"
    }

    processResources {
        inputs.property("version", version)

        filesMatching("fabric.mod.json") {
            expand("version" to version)
        }
    }

    remapJar {
        archiveFileName = "FabricCommandManager-$version.jar"
    }
}