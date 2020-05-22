package com.soyle.stories.layout.openTool

interface OpenToolController {

	fun openLocationDetailsTool(locationId: String)
	fun openBaseStoryStructureTool(themeId: String, characterId: String)
	fun openStoryEventDetailsTool(storyEventId: String)

}