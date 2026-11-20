import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.gracekim.saju"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.gracekim.saju"
        minSdk = 26
        targetSdk = 36
        versionCode = 14
        versionName = "1.9.2"
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
        // Google Play 출시용 업로드 키. 비밀번호는 저장소에 없고
        // GitHub Secret(SAJU_UPLOAD_PASSWORD)으로만 전달됩니다.
        create("upload") {
            storeFile = file("upload.keystore")
            storePassword = System.getenv("SAJU_UPLOAD_PASSWORD") ?: ""
            keyAlias = "saju-upload"
            keyPassword = System.getenv("SAJU_UPLOAD_PASSWORD") ?: ""
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
            // 코드 최적화·난독화(R8): Play Console 'DEX 코드 최적화' 요구사항
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // 업로드 키 비밀번호가 있으면(=GitHub 빌드) 출시용 키로, 없으면 테스트 키로 서명합니다.
            signingConfig = if (System.getenv("SAJU_UPLOAD_PASSWORD").isNullOrEmpty())
                signingConfigs.getByName("debug") else signingConfigs.getByName("upload")
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
    implementation("androidx.work:work-runtime:2.10.0")
    // 광고 SDK가 끌어오는 오래된 fragment(1.1.0)를 최신 버전으로 올림
    implementation("androidx.fragment:fragment:1.8.6")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
