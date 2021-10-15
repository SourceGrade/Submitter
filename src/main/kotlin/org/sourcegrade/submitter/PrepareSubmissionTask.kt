/*
 *   Jagr Submitter - SourceGrade.org
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

package org.sourcegrade.submitter

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType

internal fun Project.createPrepareSubmissionTask(configuration: SubmitConfigurationImpl) {

  val mainResourcesFile = project.buildDir.resolve("resources/submit")
  val submissionInfoFile = mainResourcesFile.resolve("submission-info.json")

  tasks.create<Jar>("prepareSubmission") {
    if (configuration.requireTests) {
      dependsOn(tasks.withType<Test>())
    }
    outputs.upToDateWhen { false }
    group = "submit"
    val sourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer
    from(*sourceSets.map { it.allSource }.toTypedArray())
    with(configuration) {
      archiveFileName.set("$assignmentId-$lastName-$firstName-submission.jar")
    }
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
      val submissionInfo = configuration.toSubmissionInfo(sourceSets.map { it.toInfo() })
      submissionInfoFile.apply {
        parentFile.mkdirs()
        writeText(Json.encodeToString(submissionInfo))
        from(path)
      }
    }
  }
}
