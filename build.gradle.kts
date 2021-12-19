plugins {
    application
    `maven-publish`
}

group = "com.github.firmwehr"
version = "SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

application {
    mainClass.set("com.github.firmwehr.fiascii.GentleCompiler")
    applicationDefaultJvmArgs = listOf("--enable-preview")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation("com.github.Firmwehr:jFirm:62c1a55f72")

    // https://mvnrepository.com/artifact/com.google.auto.service/auto-service
    implementation("com.google.auto.service:auto-service:1.0.1")
    annotationProcessor("com.google.auto.service:auto-service:1.0.1")
}

// set encoding for all compilation passes
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("--enable-preview")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    jvmArgs("--enable-preview")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = groupId
            artifactId = artifactId
            version = "1.0"

            from(components["java"])
        }
    }
}
