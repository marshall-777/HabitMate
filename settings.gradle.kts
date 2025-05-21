pluginManagement {
    plugins {
        id("com.android.application") version "8.4.0" apply false
        id("org.jetbrains.kotlin.android") version "1.9.23" apply false
        id("com.google.gms.google-services") version "4.4.0" apply false
    }
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        jcenter()

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "HabitMate"
include(":app")