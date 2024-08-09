import net.modgarden.barricade.gradle.Properties
import net.modgarden.barricade.gradle.Versions
import org.gradle.jvm.tasks.Jar

plugins {
    id("conventions.loader")
    id("fabric-loom")
    id("me.modmuss50.mod-publish-plugin")
}

repositories {
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }
}

sourceSets {
    create("datagen") {
        compileClasspath += main.get().compileClasspath
        runtimeClasspath += main.get().runtimeClasspath
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${Versions.MINECRAFT}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${Versions.PARCHMENT_MINECRAFT}:${Versions.PARCHMENT}@zip")
    })

    modImplementation("net.fabricmc:fabric-loader:${Versions.FABRIC_LOADER}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${Versions.FABRIC_API}")
    modLocalRuntime("com.terraformersmc:modmenu:${Versions.MOD_MENU}")
}

loom {
    val aw = file("src/main/resources/${Properties.MOD_ID}.accesswidener");
    if (aw.exists())
        accessWidenerPath.set(aw)
    mixin {
        defaultRefmapName.set("${Properties.MOD_ID}.refmap.json")
    }
    mods {
        register(Properties.MOD_ID) {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["test"])
        }
    }
    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            setSource(sourceSets["test"])
            ideConfigGenerated(true)
            vmArgs("-Dmixin.debug.verbose=true", "-Dmixin.debug.export=true")
        }
        named("server") {
            server()
            configName = "Fabric Server"
            setSource(sourceSets["test"])
            ideConfigGenerated(true)
            vmArgs("-Dmixin.debug.verbose=true", "-Dmixin.debug.export=true")
        }
        register("datagen") {
            server()
            configName = "Fabric Datagen"
            setSource(sourceSets["datagen"])
            ideConfigGenerated(true)
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("../common/src/generated/resources")}")
            vmArg("-Dfabric-api.datagen.modid=${Properties.MOD_ID}_datagen")
            runDir("build/datagen")
        }
    }
}

tasks {
    named<ProcessResources>("processResources").configure {
        exclude("${Properties.MOD_ID}.cfg")
    }
}

publishMods {
    file.set(tasks.named<Jar>("remapJar").get().archiveFile)
    modLoaders.add("fabric")
    changelog = rootProject.file("CHANGELOG.md").readText()
    version = "${Versions.MOD}+${Versions.MINECRAFT}-fabric"
    type = STABLE

    modrinth {
        projectId = Properties.MODRINTH_PROJECT_ID
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")

        minecraftVersions.add(Versions.MINECRAFT)
    }

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        parent(project(":common").tasks.named("publishGithub"))
    }
}