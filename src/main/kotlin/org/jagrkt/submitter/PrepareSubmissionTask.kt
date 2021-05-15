/*
 *   JagrKt Submitter - JagrKt.org
 *   Copyright (C) 2021 Alexander Staeding
 *   Copyright (C) 2021 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.jagrkt.submitter

import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.named

internal fun Project.createPrepareSubmissionTask(configuration: SubmitConfiguration) {
  tasks.create<Jar>("prepareSubmission") {
    outputs.upToDateWhen { false }
    group = "submit"
    doFirst {
      val errors = StringBuilder().apply {
        with(configuration) {
          if (assignmentId == null) appendLine("assignmentId")
          if (studentId == null) appendLine("studentId")
          if (firstName == null) appendLine("firstName")
          if (lastName == null) appendLine("lastName")
        }
      }
      if (errors.isNotEmpty()) {
        throw GradleException(
          """
There were some errors preparing your submission. The following required properties were not set:
$errors
"""
        )
      }
    }
    val main: SourceSet
    val test: SourceSet
    with(project.extensions.getByName("sourceSets") as SourceSetContainer) {
      main = named<SourceSet>("main").get()
      test = named<SourceSet>("test").get()
    }
    from(main.allSource, test.allSource)
    with(configuration) {
      archiveFileName.set("$assignmentId-$lastName-$firstName-submission.jar")
    }
    with(test.allSource) {
      val testClasses = buildJsonArray {
        for (file in files) {
          srcDirs.asSequence()
            .map(file::relativeTo)
            .reduce { a, b -> if (a.path.length < b.path.length) a else b }
            .path
            .run(::JsonPrimitive)
            .also(::add)
        }
      }
      val testMeta = buildJsonObject {
        put("testClasses", testClasses)
      }
      project.buildDir.resolve("resources/main/test-meta.json").apply {
        parentFile.mkdirs()
        writeText(testMeta.toString())
        from(path)
      }
    }
  }
}
