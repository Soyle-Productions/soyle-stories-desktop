package com.soyle.stories.scene.charactersInScene.removeCharacterFromScene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import kotlinx.coroutines.Job

interface RemoveCharacterFromSceneController {

    fun removeCharacterFromScene(
        sceneId: Scene.Id,
        characterId: Character.Id,
        confirmationPrompt: RemoveCharacterConfirmationPrompt
    ): Job

    fun removeCharacterFromScene(
        sceneId: Scene.Id,
        selectCharacterPrompt: SelectCharacterPrompt,
        confirmationPrompt: RemoveCharacterConfirmationPrompt
    ): Job
}