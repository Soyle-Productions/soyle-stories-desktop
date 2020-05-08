package com.soyle.stories.repositories

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.repositories.SceneRepository

class SceneRepositoryImpl : SceneRepository {

	private val scenes = mutableMapOf<Scene.Id, Scene>()

	override suspend fun createNewScene(scene: Scene) {
		scenes[scene.id] = scene
	}

	override suspend fun listAllScenesInProject(projectId: Project.Id): List<Scene> =
	  scenes.values.toList()

	override suspend fun getSceneById(sceneId: Scene.Id): Scene? =
	  scenes[sceneId]

	override suspend fun updateScene(scene: Scene) {
		scenes[scene.id] = scene
	}

	override suspend fun removeScene(scene: Scene) {
		scenes.remove(scene.id)
	}
}