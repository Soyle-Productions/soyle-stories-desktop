package com.soyle.stories.entities

import java.util.*

class Scene(
  val id: Id,
  val projectId: Project.Id,
  val name: String
) {

	class Id(val uuid: UUID = UUID.randomUUID())
}