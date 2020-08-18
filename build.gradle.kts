import org.jetbrains.kotlin.konan.file.File
import org.jetbrains.kotlin.konan.properties.saveToFile
import java.util.Properties

plugins {
    java
    kotlin("jvm") version "1.4.0"
    application
    id("net.nemerosa.versioning") version "2.8.2"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

group = "com.wire.bots"
version = versioning.info.lastTag + if(versioning.info.dirty) "-dirty" else ""

val mClass = "com.wire.bots.narvi.Service"

repositories {
    jcenter()

    // lithium
    maven {
        url = uri("https://packagecloud.io/dkovacevic/lithium/maven2")
    }

    // transitive dependency for the lithium
    maven {
        url = uri("https://packagecloud.io/dkovacevic/cryptobox4j/maven2")
    }
}


dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("pw.forst.tools", "katlib", "1.0.0")

    implementation("com.wire.bots", "lithium", "2.36.2") {
        // we're replacing it with newer version as the one included in Lithium has problems with JRE 11
        exclude("com.google.protobuf", "protobuf-java")
    }
    implementation("com.google.protobuf", "protobuf-java", "3.12.4")

    implementation("io.github.microutils", "kotlin-logging", "1.7.9")

    // testing
    val junitVersion = "5.6.2"
    testImplementation("org.junit.jupiter", "junit-jupiter-api", junitVersion)
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))

    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junitVersion)
}

application {
    mainClassName = mClass
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    shadowJar {
        mergeServiceFiles()

        manifest {
            attributes(
                mapOf(
                    "Main-Class" to mClass
                )
            )
        }
        exclude("META-INF/*.DSA", "META-INF/*.RSA", "META-INF/*.SF")

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveFileName.set("narvi.jar")
    }

    test {
        useJUnitPlatform()
    }

    register("resolveDependencies") {
        doLast {
            project.allprojects.forEach { subProject ->
                with(subProject) {
                    buildscript.configurations.forEach { if (it.isCanBeResolved) it.resolve() }
                    configurations.compileClasspath.get().resolve()
                    configurations.testCompileClasspath.get().resolve()
                }
            }
        }
    }
}
