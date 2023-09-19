plugins {
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
dependencies {

    implementation(project(":api"))

// implementation of ksp, ksp api, and kotlin poet
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.10-1.0.13")
    implementation("com.google.devtools.ksp:symbol-processing:1.9.10-1.0.13")
    implementation("com.squareup:kotlinpoet:1.14.2")
}
