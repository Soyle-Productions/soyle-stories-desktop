package com.soyle.stories.scene.doubles

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.repositories.SceneRepository

class SceneRepositoryDouble(
  initialScenes: List<Scene> = emptyList(),

  private val onAddNewScene: (Scene) -> Unit = {},
  private val onUpdateScene: (Scene) -> Unit = {},
  private val onRemoveScene: (Scene) -> Unit = {}
) : SceneRepository {

	private val scenes = initialScenes.associateBy { it.id }.toMutableMap()

	private val _persistedItems = mutableListOf<PersistenceLog>()
	val persistedItems: List<PersistenceLog>
		get() = _persistedItems

	private fun log(data: Any) {
		val type = Thread.currentThread().stackTrace.find {
			it.methodName != "log" && it.methodName != "getStackTrace"
		}?.methodName!!
		_persistedItems += PersistenceLog(type, data)
	}

	override suspend fun createNewScene(scene: Scene) {
		log(scene)
		onAddNewScene.invoke(scene)
		scenes[scene.id] = scene
	}

	override suspend fun listAllScenesInProject(projectId: Project.Id): List<Scene> {
		return scenes.values.filter { it.projectId == projectId }
	}
}