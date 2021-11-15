package com.soyle.stories.domain.scene.order

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.Updated
import com.soyle.stories.domain.scene.WithoutChange
import com.soyle.stories.domain.scene.events.SceneCreated
import com.soyle.stories.domain.scene.sceneName
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Scene Order Service Unit Test` {

    private val service = SceneOrderService()
    private val projectId = Project.Id()
    private val proseId = Prose.Id()
    private val inputName = sceneName()

    @Nested
    inner class `Given No Scenes Exist Yet` {

        val sceneOrder = SceneOrder(projectId, setOf())

        @Test
        fun `should create new scene in project`() {
            val (_, update) = runBlocking { service.createScene(sceneOrder, inputName, proseId) }

            update as Updated<*>
            val event = update.event
            event.name.mustEqual(inputName.value)
            event.proseId.mustEqual(proseId)
            event.storyEventId.mustEqual(update.scene.storyEventId)
        }

        @Test
        fun `first scene should be at the first index`() {
            val (update, sceneUpdate) = runBlocking { service.createScene(sceneOrder, inputName, proseId) }

            update as SuccessfulSceneOrderUpdate
            update.sceneOrder.order.size.mustEqual(1)
            update.sceneOrder.order.single().mustEqual(sceneUpdate!!.scene.id)
        }

    }

    @Nested
    inner class `Given Scenes Exist Already` {

        val sceneOrder = SceneOrder(projectId, List(4) { Scene.Id() }.toSet())

        @Test
        fun `scene should be added to the end of the project`() {
            val (update, sceneUpdate) = runBlocking { service.createScene(sceneOrder, inputName, proseId) }

            update as SuccessfulSceneOrderUpdate
            update.sceneOrder.order.size.mustEqual(5)
            update.sceneOrder.order.last().mustEqual(sceneUpdate!!.scene.id)
        }

        @Test
        fun `new scene should be at provided index`() {
            val (update, sceneUpdate) = runBlocking { service.createScene(sceneOrder, inputName, proseId, 2) }

            update as SuccessfulSceneOrderUpdate
            update.sceneOrder.order.size.mustEqual(5)
            update.sceneOrder.order.toList()[2].mustEqual(sceneUpdate!!.scene.id)
        }

        @Test
        fun `if scene insertion fails, should not create new scene`() {
            val (update, sceneUpdate) = runBlocking { service.createScene(sceneOrder, inputName, proseId, -4) }

            update as UnSuccessfulSceneOrderUpdate
            assertNull(sceneUpdate)
        }

    }

}