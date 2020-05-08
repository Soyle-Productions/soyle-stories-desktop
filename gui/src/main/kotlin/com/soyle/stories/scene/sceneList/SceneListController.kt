package com.soyle.stories.scene.sceneList

import com.soyle.stories.gui.ThreadTransformer
import com.soyle.stories.scene.usecases.listAllScenes.ListAllScenes

class SceneListController(
  private val threadTransformer: ThreadTransformer,
  private val listAllScenes: ListAllScenes,
  private val listAllScenesOutputPort: ListAllScenes.OutputPort
) : SceneListViewListener {
	override fun getValidState() {
		threadTransformer.async {
			listAllScenes.invoke(listAllScenesOutputPort)
		}
	}

	override fun openSceneDetails(sceneId: String) {
		TODO("Not yet implemented")
	}

	override fun renameScene(sceneId: String, newName: String) {
		TODO("Not yet implemented")
	}
}