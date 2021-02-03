package com.soyle.stories.scene.listSymbolsInScene

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.usecases.listSymbolsInScene.ListSymbolsInScene
import kotlinx.coroutines.Job

class ListSymbolsInSceneControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val listSymbolsInScene: ListSymbolsInScene
) : ListSymbolsInSceneController {

    override fun listSymbolsInScene(sceneId: Scene.Id, output: ListSymbolsInScene.OutputPort): Job {
        return threadTransformer.async {
            listSymbolsInScene.invoke(sceneId, output)
        }
    }

}