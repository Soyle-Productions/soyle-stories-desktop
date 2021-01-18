package com.soyle.stories.layout.openTool

import com.soyle.stories.entities.Prose

interface OpenToolController {

	fun openLocationDetailsTool(locationId: String)
	fun openBaseStoryStructureTool(themeId: String, characterId: String)
	fun openCharacterValueComparison(themeId: String)
	fun openStoryEventDetailsTool(storyEventId: String)
	fun openDeleteSceneRamificationsTool(sceneId: String)
	fun openReorderSceneRamificationsTool(sceneId: String, newIndex: Int)
	fun openSceneDetailsTool(sceneId: String)
	fun openValueOppositionWeb(themeId: String)
	fun openCentralConflict(themeId: String, characterId: String?)
	fun openMoralArgument(themeId: String)
	fun openSceneEditor(sceneId: String, proseId: Prose.Id)

}