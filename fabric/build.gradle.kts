import me.modmuss50.mpp.ReleaseType
import net.modgarden.barricade.gradle.Properties
import net.modgarden.barricade.gradle.Versions

plugins {
	id("conventions.loader")
	id("net.fabricmc.fabric-loom")
	id("me.modmuss50.mod-publish-plugin")
}

repositories {
	maven("https://api.modrinth.com/maven") {
		name = "Modrinth"
	}
	maven("https://maven.terraformersmc.com/") {
		name = "TerraformersMC"
	}
	maven("https://maven.caffeinemc.net/releases") {
		name = "CaffeineMC"
	}
	mavenCentral()
}

sourceSets {
	create("datagen") {
		compileClasspath += main.get().compileClasspath
		runtimeClasspath += main.get().runtimeClasspath
		compileClasspath += main.get().output
		runtimeClasspath += main.get().output
	}

	register("generated")

	getByName("main") {
		compileClasspath += getByName("generated").compileClasspath
		runtimeClasspath += getByName("generated").runtimeClasspath
		resources.srcDirs(getByName("generated").resources.srcDirs)
	}

	getByName("test") {
		compileClasspath += getByName("generated").compileClasspath
		runtimeClasspath += getByName("generated").runtimeClasspath
		resources.srcDirs(getByName("generated").resources.srcDirs)
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${Versions.MINECRAFT}")

	implementation("net.fabricmc:fabric-loader:${Versions.FABRIC_LOADER}")
	implementation("net.fabricmc.fabric-api:fabric-api:${Versions.FABRIC_API}")

	runtimeOnly("com.terraformersmc:modmenu:${Versions.MOD_MENU}")
	runtimeOnly("maven.modrinth:sodium:${Versions.SODIUM}-fabric")
	compileOnly("net.caffeinemc:sodium-fabric:${Versions.SODIUM_MAVEN}")

	api("lgbt.greenhouse.silicate:silicate-fabric:${Versions.SILICATE}")
	implementation("lgbt.greenhouse.silicate:silicate-fabric:${Versions.SILICATE}")
	include("lgbt.greenhouse.silicate:silicate-fabric:${Versions.SILICATE}")

	compileOnly("lgbt.greenhouse.config:greenhouse-config-api:${Versions.GREENHOUSE_CONFIG}")

	runtimeOnly("lgbt.greenhouse.config:greenhouse-config-fabric:${Versions.GREENHOUSE_CONFIG}")
	include("lgbt.greenhouse.config:greenhouse-config-fabric:${Versions.GREENHOUSE_CONFIG}")
	runtimeOnly("lgbt.greenhouse.config:greenhouse-config-fabric:${Versions.GREENHOUSE_CONFIG}")
	include("lgbt.greenhouse.config:greenhouse-config-fabric:${Versions.GREENHOUSE_CONFIG}")

	runtimeOnly("lgbt.greenhouse.polyamory:polyamory:${Versions.POLYAMORY}")
	include("lgbt.greenhouse.polyamory:polyamory:${Versions.POLYAMORY}")

	runtimeOnly("lgbt.greenhouse.polyamory.lang.jsonc:polyamory-lang-jsonc:${Versions.POLYAMORY_JSONC}")
	include("lgbt.greenhouse.polyamory.lang.jsonc:polyamory-lang-jsonc:${Versions.POLYAMORY_JSONC}")

	api("gay.sylv.frappe:frappe-ext-terrain-material:${Versions.FRAPPE}")
//	include("gay.sylv.frappe:frappe:${Versions.FRAPPE}")
	runtimeOnly("gay.sylv.frappe:mocha:${Versions.MOCHA}")

	compileOnly("dev.lukebemish:codecextras:3.0.0")
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
			vmArgs("-Dmixin.debug.export=true")
		}
		named("client") {
			client()
			configName = "Fabric Client (Renderdoc)"
			setSource(sourceSets["test"])
			ideConfigGenerated(true)
			vmArgs("-Dmixin.debug.export=true")
			programArgs("--renderDebugLabels")
			val os = System.getProperty("os.name")
			when (os) {
				"Linux", "FreeBSD" -> {
					environmentVariable("LD_PRELOAD", "/usr/lib/librenderdoc.so")
				}
			}
		}
		named("server") {
			server()
			configName = "Fabric Server"
			setSource(sourceSets["test"])
			ideConfigGenerated(true)
			vmArgs("-Dmixin.debug.export=true")
		}
		register("datagen") {
			client()
			configName = "Fabric Datagen"
			setSource(sourceSets["datagen"])
			ideConfigGenerated(true)
			vmArg("-Dfabric-api.datagen")
			vmArg("-Dfabric-api.datagen.output-dir=${file("../fabric/src/generated/resources")}")
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
	modLoaders.add("fabric")
	changelog = rootProject.file("CHANGELOG.md").readText()
	version = "${Versions.MOD}+${Versions.MINECRAFT}-fabric"
	type = ReleaseType.valueOf(Versions.MOD_STABILITY.uppercase())

	modrinth {
		projectId = Properties.MODRINTH_PROJECT_ID
		accessToken = providers.environmentVariable("MODRINTH_TOKEN")

		displayName = "${Versions.MOD} (Fabric ${Versions.MINECRAFT})"
		minecraftVersions.add(Versions.MINECRAFT)
	}

	github {
		accessToken = providers.environmentVariable("GITHUB_TOKEN")
	}
}
