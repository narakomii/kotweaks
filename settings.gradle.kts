import java.net.URI

pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
		}
		mavenCentral()
		gradlePluginPortal()
	}

	plugins {
		id("net.fabricmc.fabric-loom") version providers.gradleProperty("loom_version")
	}
}
plugins {
	id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
}

// Should match your modid
rootProject.name = "kotweaks"

// https://stackoverflow.com/a/56787382
/*sourceControl {
	gitRepository(URI("https://github.com/RelativityMC/VMP-fabric.git")) {
		producesModule("com.ishland.vmp:vmp-fabric")
	}
}*/