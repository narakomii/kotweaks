plugins {
	id("net.fabricmc.fabric-loom")
	`maven-publish`
}

version = providers.gradleProperty("mod_version").get()
group = providers.gradleProperty("maven_group").get()
val jdk = providers.gradleProperty("jdk_version").get()

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.
	maven { url = uri("https://maven.nucleoid.xyz") }
	maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
	maven { url = uri("https://maven.codedsakura.dev/releases") }
}

fabricApi {
	configureDataGeneration {
		client = true
	}
}

val jij: Configuration by configurations.creating

configurations {
	implementation.configure {
		extendsFrom(jij)
	}
	include.configure {
		extendsFrom(jij)
	}
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")
	
	implementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")

	// Fabric API. This is technically optional, but you probably want it anyway.
	implementation("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}")

	implementation("dev.codedsakura.blossom:blossom-lib:${providers.gradleProperty("blossomlib_version").get()}+${providers.gradleProperty("blossomlib_minecraft_version").get()}")

	jij("org.reflections:reflections:0.10.2")
}

tasks.processResources {
	val propertyMap = mapOf(
		"version" to project.version,
		"jdk_version" to jdk,
		"minecraft_version" to providers.gradleProperty("minecraft_version").get(),
		"loader_version" to providers.gradleProperty("loader_version").get(),
		"blossomlib_version" to providers.gradleProperty("blossomlib_version").get(),
	)

	inputs.properties(propertyMap)
	filesMatching("fabric.mod.json") {
		expand(propertyMap)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release = jdk.toInt()
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	//withSourcesJar()

	//sourceCompatibility = JavaVersion.VERSION_25
	//targetCompatibility = JavaVersion.VERSION_25

	toolchain {
		languageVersion.set(JavaLanguageVersion.of(jdk.toInt()))
	}
}

tasks.jar {
	val projectName = project.name
	inputs.property("projectName", projectName)

	from("LICENSE") {
		rename { "${it}_$projectName" }
	}
}

// configure the maven publication
publishing {
	publications {
		register<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}

val jijExcluded = setOf("org.slf4j", "jsr305")
listOf("api", "implementation", "include").forEach { configName ->
	configurations.named(configName).configure {
		defaultDependencies {
			configurations.getByName("jij").incoming.resolutionResult.allComponents
				.mapNotNull { it.id as? ModuleComponentIdentifier }
				.forEach { id ->
					val notation = "${id.group}:${id.module}:${id.version}"
					if (jijExcluded.none { notation.contains(it) }) {
						add(project.dependencies.create(notation) {
							isTransitive = false
						})
					}
				}
		}
	}
}

loom {
	accessWidenerPath = file("src/main/resources/kotweaks.classtweaker")
}