plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "io.kl3jvi.director"
            artifactId = "director-api"
            version = "0.0.1"
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
