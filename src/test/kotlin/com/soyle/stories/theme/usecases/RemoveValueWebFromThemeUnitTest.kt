package com.soyle.stories.theme.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.ValueWeb
import com.soyle.stories.theme.ValueWebDoesNotExist
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.makeValueWeb
import com.soyle.stories.theme.usecases.removeValueWebFromTheme.RemoveValueWebFromTheme
import com.soyle.stories.theme.usecases.removeValueWebFromTheme.RemoveValueWebFromThemeUseCase
import com.soyle.stories.theme.usecases.removeValueWebFromTheme.ValueWebRemovedFromTheme
import com.soyle.stories.theme.valueWebDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RemoveValueWebFromThemeUnitTest {

    private val themeId = Theme.Id()
    private val valueWebId = ValueWeb.Id()

    private var updatedTheme: Theme? = null
    private var result: Any? = null

    @Test
    fun `value web does not exist`() {
        result = assertThrows<ValueWebDoesNotExist> {
            removeValueWebFromTheme()
        }
        assertNull(updatedTheme)
        result shouldBe valueWebDoesNotExist(valueWebId.uuid)
    }

    @Test
    fun `value web exists`() {
        givenValueWeb()
        removeValueWebFromTheme()
        updatedTheme shouldBe ::themeWithoutValueWeb
        result shouldBe ::valueWebRemovedFromTheme
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = { updatedTheme = it })

    private fun givenValueWeb()
    {
        themeRepository.themes[themeId] = makeTheme(themeId, valueWebs = listOf(makeValueWeb(valueWebId)))
    }

    private fun removeValueWebFromTheme()
    {
        val useCase: RemoveValueWebFromTheme = RemoveValueWebFromThemeUseCase(themeRepository)
        val output = object : RemoveValueWebFromTheme.OutputPort {
            override suspend fun removedValueWebFromTheme(response: ValueWebRemovedFromTheme) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(valueWebId.uuid, output)
        }
    }

    private fun themeWithoutValueWeb(actual: Any?)
    {
        actual as Theme
        assertEquals(themeId, actual.id)
        assertNull(actual.valueWebs.find { it.id == valueWebId })
    }

    private fun valueWebRemovedFromTheme(actual: Any?)
    {
        actual as ValueWebRemovedFromTheme
        assertEquals(themeId.uuid, actual.themeId)
        assertEquals(valueWebId.uuid, actual.valueWebId)
    }

}