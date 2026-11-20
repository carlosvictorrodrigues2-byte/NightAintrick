plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
    namespace = "com.nightaintrick.app"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.nightaintrick"
        minSdk = 26; targetSdk = 34
        versionCode = 1; versionName = "1.0"
    }
    buildTypes { release { isMinifyEnabled = false } }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    implementation(project(":core"))
    implementation(project(":feature-boost"))
    implementation(project(":feature-config"))
    implementation(project(":feature-device"))
    implementation(project(":feature-dpi"))
    implementation(project(":feature-launcher"))
    implementation("androidx.activity:activity-compose:1.9.0")
}
