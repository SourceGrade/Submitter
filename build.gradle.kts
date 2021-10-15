import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("com.gradle.plugin-publish").version("0.14.0")
  `java-gradle-plugin`
  kotlin("jvm")
  kotlin("plugin.serialization")
}

group = "org.sourcegrade"
version = "0.4.0-SNAPSHOT"

val kotlinxSerializationVersion: String by project

repositories {
  mavenCentral()
}

dependencies {
  implementation(gradleKotlinDsl())
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
      id = "org.sourcegrade.submitter"
      displayName = "Jagr Submitter"
      description = "Gradle plugin for submitting source code for the Jagr AutoGrader"
      implementationClass = "org.sourcegrade.submitter.SubmitterPlugin"
    }
  }
}

pluginBundle {
  website = "https://www.sourcegrade.org"
  vcsUrl = "https://github.com/SourceGrade/Submitter"
  (plugins) {
    "submitter" {
      tags = listOf("jagr", "assignment", "submission", "grading")
    }
  }
  mavenCoordinates {
    groupId = project.group.toString()
    artifactId = "submitter"
  }
}
