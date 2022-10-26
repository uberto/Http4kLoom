import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    application
}

tasks.wrapper {
    gradleVersion = "7.5.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
        implementation( platform("org.http4k:http4k-bom:4.33.1.0"))
        implementation ("org.http4k:http4k-core")
        implementation ("org.http4k:http4k-server-jetty")
        implementation ("org.http4k:http4k-client-apache")



    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


application {
    applicationDefaultJvmArgs = listOf("--enable-preview")
}

//tasks.withType<JavaCompile> {
//    options.compilerArgs.add( "--enable-preview")
//}
//
//tasks.withType<Test> {
//    jvmArgs.add("--enable-preview")
//}
//
//tasks.withType<JavaExec> {
//    jvmArgs.add( "--enable-preview")
//}

application {
    mainClass.set("MainKt")
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}