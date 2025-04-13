plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    // 重要：编译时加上签名
    signingConfigs {
        create("release") {
            storeFile = file("E:\\APK\\GenerateAPK.jks")
            storePassword = "314159"
            keyAlias = "GenerateAPK"
            keyPassword = "314159"
        }
        getByName("debug") {
            storeFile = file("${System.getProperty("user.home")}/.android/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
//            storeFile = file("E:\\APK\\GenerateAPK.jks")
//            storePassword = "314159"
//            keyAlias = "GenerateAPK"
//            keyPassword = "314159"
        }
    }
    namespace = "com.example.olderperson"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.olderperson"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-core:1.6.1")
    implementation("androidx.compose.material:material-icons-extended:1.6.1")
    implementation("androidx.compose.material:material:1.6.1")
    implementation("androidx.compose.runtime:runtime:1.6.1")
    implementation("androidx.compose.foundation:foundation:1.6.1")
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // 添加 Material Design 和 ConstraintLayout 依赖
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // 视频通话相关
    implementation("io.agora.rtc:full-sdk:3.7.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // 百度地图依赖
    implementation("com.baidu.lbsyun:BaiduMapSDK_Map:7.5.0")
    implementation("com.baidu.lbsyun:BaiduMapSDK_Search:7.5.0")
    implementation("com.baidu.lbsyun:BaiduMapSDK_Util:7.5.0")
    implementation("com.baidu.lbsyun:BaiduMapSDK_Location:9.3.7")
    
    // Google地图依赖（保留以备需要）
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.maps.android:maps-compose:4.3.0")
    
    // 阿里云通义千问SDK相关依赖
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.alibaba.fastjson2:fastjson2:2.0.43")
    
    // 添加Coil图片加载库依赖
    implementation("io.coil-kt:coil:2.5.0") 
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}