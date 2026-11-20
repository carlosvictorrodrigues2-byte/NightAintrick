pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "NightAintrick"
include(":app", ":core", ":feature-boost", ":feature-config", ":feature-device", ":feature-dpi", ":feature-launcher")
