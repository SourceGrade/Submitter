import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("com.gradle.plugin-publish").version("0.14.0")
  `java-gradle-plugin`
  val kotlinVersion = "1.5.0"
  kotlin("jvm").version(kotlinVersion)
  kotlin("plugin.serialization").version(kotlinVersion)
}

group = "org.jagrkt"
version = "0.3.0"

repositories {
  mavenCentral()
}

dependencies {
  implementation(gradleKotlinDsl())
  val kotlinxSerializationVersion = "1.2.1"
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:$kotlinxSerializationVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
}

tasks {
  withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
  }
  withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "11"
    targetCompatibility = "11"
  }
}

gradlePlugin {
  plugins {
    create("submitter") {
      id = "org.jagrkt.submitter"
      displayName = "JagrKt Submitter"
      description = "Gradle plugin for submitting source code for the JagrKt AutoGrader"
      implementationClass = "org.jagrkt.submitter.SubmitterPlugin"
    }
  }
}

pluginBundle {
  website = "https://jagrkt.org"
  vcsUrl = "https://github.com/JagrKt/Submitter"
  (plugins) {
    "submitter" {
      tags = listOf("jagrkt", "assignment", "submission", "grading")
    }
  }
  mavenCoordinates {
    groupId = project.group.toString()
    artifactId = "plugin-submitter"
  }
}
