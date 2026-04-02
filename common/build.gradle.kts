import net.modgarden.barricade.gradle.Properties
import net.modgarden.barricade.gradle.Versions

plugins {
	id("conventions.common")
	id("net.neoforged.moddev")
	id("me.modmuss50.mod-publish-plugin")
}

sourceSets {
	create("generated") {
		resources {
			srcDir("src/generated/resources")
		}
	}
}

neoForge {
	neoFormVersion = Versions.NEOFORM
	addModdingDependenciesTo(sourceSets["test"])

	val at = file("src/main/resources/${Properties.MOD_ID}.cfg")
	if (at.exists())
		setAccessTransformers(at)
	validateAccessTransformers = true
}
val mappingsAttribute = Attribute.of("net.minecraft.mappings", String::class.java)


dependencies {
	compileOnly("io.github.llamalad7:mixinextras-common:${Versions.MIXIN_EXTRAS}")
	annotationProcessor("io.github.llamalad7:mixinextras-common:${Versions.MIXIN_EXTRAS}")
	compileOnly("net.fabricmc:sponge-mixin:${Versions.FABRIC_MIXIN}")
	compileOnly("lgbt.greenhouse.silicate:silicate-xplat:${Versions.SILICATE}")
	compileOnly("lgbt.greenhouse.config:greenhouse-config-api:${Versions.GREENHOUSE_CONFIG}") {
		attributes {
			attribute(mappingsAttribute, "mojmap")
		}
	}
}

configurations {
	register("commonJava") {
		isCanBeResolved = false
		isCanBeConsumed = true
	}
	register("commonResources") {
		isCanBeResolved = false
		isCanBeConsumed = true
	}
	register("commonTestResources") {
		isCanBeResolved = false
		isCanBeConsumed = true
	}
}

artifacts {
	add("commonJava", sourceSets["main"].java.sourceDirectories.singleFile)
	add("commonResources", sourceSets["main"].resources.sourceDirectories.singleFile)
	add("commonResources", sourceSets["generated"].resources.sourceDirectories.singleFile)
	add("commonTestResources", sourceSets["test"].resources.sourceDirectories.singleFile)
}

publishMods {
	changelog = rootProject.file("CHANGELOG.md").readText()
	version = "${Versions.MOD}+${Versions.MINECRAFT}"
	type = STABLE

	github {
		accessToken = providers.environmentVariable("GITHUB_TOKEN")
		repository = Properties.GITHUB_REPO
		displayName = "${Versions.MOD} (${Versions.MINECRAFT})"
		tagName = "${Versions.MOD}+${Versions.MINECRAFT}"
		commitish = Properties.GITHUB_COMMITISH

		allowEmptyFiles = true
	}
}
