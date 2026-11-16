import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.gracekim.saju"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gracekim.saju"
        minSdk = 26
        targetSdk = 35
        versionCode = 6
        versionName = "1.3.2"
    }

    signingConfigs {
        // 테스트 배포용 고정 서명 키 (빌드할 때마다 키가 바뀌면 업데이트 설치가 안 되기 때문).
        // Google Play 출시용 키는 절대 저장소에 올리지 말고 따로 보관해야 합니다.
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    // ── AdMob 광고 ID ─────────────────────────────────────────────
    // debug(테스트 설치용): Google 공식 테스트 광고 → 눌러도 안전, 수익 없음
    // release(Play 스토어 출시용): 실제 광고 → 수익 발생
    //   ※ 실제 광고는 본인·가족이 일부러 누르면 계정이 정지될 수 있습니다.
    buildTypes {
        debug {
            manifestPlaceholders["admobAppId"] = "ca-app-pub-3940256099942544~3347511713"
            buildConfigField("String", "ADMOB_BANNER_ID", "\"ca-app-pub-3940256099942544/9214589741\"")
            buildConfigField("String", "ADMOB_REWARDED_ID", "\"ca-app-pub-3940256099942544/5224354917\"")
        }
        release {
            manifestPlaceholders["admobAppId"] = "ca-app-pub-7777902414096561~4028093423"
            buildConfigField("String", "ADMOB_BANNER_ID", "\"ca-app-pub-7777902414096561/6047752108\"")
            buildConfigField("String", "ADMOB_REWARDED_ID", "\"ca-app-pub-7777902414096561/3375301391\"")
            isMinifyEnabled = false
            // 스토어 배포 전에는 별도의 업로드 키로 서명해야 합니다.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":saju-core"))

    val composeBom = platform("androidx.compose:compose-bom:2025.04.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("com.google.android.gms:play-services-ads:24.2.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
