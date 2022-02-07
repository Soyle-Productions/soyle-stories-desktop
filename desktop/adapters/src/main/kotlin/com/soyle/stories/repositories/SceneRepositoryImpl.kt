package com.soyle.stories.repositories

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.usecase.scene.SceneRepository

class SceneRepositoryImpl : SceneRepository {

	private val scenes = mutableMapOf<Scene.Id, Scene>()
	private val sceneOrder = mutableMapOf<Project.Id, SceneOrder>()

	// indexes
	private val scenesByProseId = mutableMapOf<Prose.Id, Scene.Id>()

	override suspend fun createNewScene(scene: Scene) {
		scenes[scene.id] = scene
		scenesByProseId[scene.proseId] = scene.id
	}

	override suspend fun updateSceneOrder(sceneOrder: SceneOrder) {
		this.sceneOrder[sceneOrder.projectId] = sceneOrder
	}

	override suspend fun listAllScenesInProject(projectId: Project.Id, exclude: Set<Scene.Id>): List<Scene> =
		scenes.values.filterNot { it.id in exclude }

	override suspend fun getScenesTrackingSymbol(symbolId: Symbol.Id): List<Scene> {
		return scenes.values.filter { it.trackedSymbols.isSymbolTracked(symbolId) }
	}

	override suspend fun getScenesUsingLocation(locationId: Location.Id): List<Scene> {
		return  scenes.values.filter { it.settings.containsEntityWithId(locationId) }
	}

	override suspend fun getScenesIncludingCharacter(characterId: Character.Id): List<Scene> {
		return scenes.values.filter { it.includesCharacter(characterId) }
	}

	override suspend fun getSceneIdsInOrder(projectId: Project.Id): SceneOrder? {
		return sceneOrder[projectId]
	}

	override suspend fun getSceneById(sceneId: Scene.Id): Scene? =
	  scenes[sceneId]

	override suspend fun updateScene(scene: Scene) {
		val oldScene = scenes[scene.id]
		scenes[scene.id] = scene
		scenesByProseId[scene.proseId] = scene.id
	}

	override suspend fun removeScene(sceneId: Scene.Id) {
		val scene = scenes.remove(sceneId) ?: return
		scenesByProseId.remove(scene.proseId)
	}

	override suspend fun getSceneThatOwnsProse(proseId: Prose.Id): Scene? {
		return scenesByProseId[proseId]?.let { scenes[it] }
	}
}