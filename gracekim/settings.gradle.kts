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
    plugins {
        id("com.android.application") version "8.9.1"
        id("org.jetbrains.kotlin.android") version "2.1.20"
        id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
        id("org.jetbrains.kotlin.jvm") version "2.1.20"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "SajuApp"

include(":saju-core")

// Android SDK가 없는 환경에서는 -PskipApp=true 로 사주 계산 엔진만 빌드/테스트할 수 있습니다.
if (providers.gradleProperty("skipApp").orNull != "true") {
    include(":app")
}
