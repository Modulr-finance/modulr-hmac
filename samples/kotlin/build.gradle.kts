plugins {
    kotlin("jvm") version "1.4.0"
}

group = "com.modulr"
version = "1.0"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("junit:junit:4.12")
    testImplementation("org.mockito:mockito-core:2.8.47")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
}
