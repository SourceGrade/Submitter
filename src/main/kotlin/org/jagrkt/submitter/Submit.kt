package org.jagrkt.submitter

import org.gradle.api.Project

interface SubmitConfiguration {
  var assignmentId: String?
  var studentId: String?
  var firstName: String?
  var lastName: String?
}

private data class SubmitConfigurationImpl(
  override var assignmentId: String? = null,
  override var studentId: String? = null,
  override var firstName: String? = null,
  override var lastName: String? = null,
) : SubmitConfiguration

fun Project.submit(configure: SubmitConfiguration.() -> Unit) {
  createPrepareSubmissionTask(SubmitConfigurationImpl().apply(configure))
}
