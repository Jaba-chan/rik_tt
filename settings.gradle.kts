pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "rik_tt"
include(":app")
include(":core")
include(":feature")
include(":core:common")
include(":core:data")
include(":core:domain")
include(":feature:statistic")
include(":core:network")
include(":core:designsystem")
include(":core:ui")
