package com.soyle.stories.characterarc.usecases

import arrow.core.Either
import arrow.core.right
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.character.characterName
import com.soyle.stories.characterarc.TestContext
import com.soyle.stories.characterarc.repositories.ThemeRepository
import com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructure
import com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructureUseCase
import com.soyle.stories.entities.*
import com.soyle.stories.theme.*
import com.soyle.stories.theme.repositories.CharacterArcRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class ViewBaseStoryStructureTest {

    val characterUUID = UUID.randomUUID()
    val themeUUID = UUID.randomUUID()
    val locationUUID = UUID.randomUUID()
    val character = makeCharacter(
      Character.Id(characterUUID), Project.Id(), characterName()
    )

    private var result: Any? = null

    @Test
    fun `theme does not exist`() {
        given(NoThemes)
        whenUseCaseExecuted()
        val result = result as ThemeDoesNotExist
        assertEquals(themeUUID, result.themeId)
    }

    @Test
    fun `character not in theme`() {
        given(
          themesWithIdsOf(themeUUID),
          NoIncludedCharacters
        )
        whenUseCaseExecuted()
        val result = result as CharacterNotInTheme
        assertEquals(characterUUID, result.characterId)
        assertEquals(themeUUID, result.themeId)
    }

    @Test
    fun `character is not major character`() {
        given(
          themesWithIdsOf(themeUUID),
          andIncludedCharactersWithIdsOf(characterUUID)
        )
        whenUseCaseExecuted()
        val result = result as CharacterIsNotMajorCharacterInTheme
        assertEquals(characterUUID, result.characterId)
        assertEquals(themeUUID, result.themeId)
    }

    @Test
    fun `character is major character in theme`() {
        given(
          themesWithIdsOf(themeUUID),
          andPromotedCharactersWithIdsOf(characterUUID)
        )
        whenUseCaseExecuted()
        assertValidResponseModel(result)
    }

    @Test
    fun `linked locations are output`() {
        given(
          themesWithIdsOf(themeUUID),
          andPromotedCharactersWithIdsOf(characterUUID),
          andArcSectionsLinkedToLocationWithIdOf(locationUUID)
        )
        whenUseCaseExecuted()
        assertValidResponseModel(result)
    }

    val NoThemes: List<UUID> = emptyList()
    private fun themesWithIdsOf(vararg ids: UUID) = listOf(*ids)
    val NoIncludedCharacters: List<Pair<UUID, Boolean>> = emptyList()
    private fun andIncludedCharactersWithIdsOf(vararg ids: UUID) = listOf(*(ids.map { it to false }.toTypedArray()))
    private fun andPromotedCharactersWithIdsOf(vararg ids: UUID) = listOf(*(ids.map { it to true }.toTypedArray()))
    private fun andArcSectionsLinkedToLocationWithIdOf(id: UUID) = id
    private lateinit var themeRepository: ThemeRepository
    private lateinit var characterArcRepository: CharacterArcRepository

    private fun given(
      themeIds: List<UUID>, includedCharacterIds: List<Pair<UUID, Boolean>> = emptyList(), linkedLocationId: UUID? = null
    ) {
        val themes = themeIds.map { uuid ->
            val initialTheme = takeNoteOfTheme(uuid)
            if (includedCharacterIds.isNotEmpty()) {
                includedCharacterIds.fold(initialTheme) { theme, (id, isPromoted) ->
                    val character1 = makeCharacter(Character.Id(id), Project.Id(), characterName())
                    val included = theme.withCharacterIncluded(character1.id, character1.name.value, character1.media).right()
                    ((if (isPromoted) {
                        included.map {
                            it.withCharacterPromoted(Character.Id(id))
                        }
                    } else included) as Either.Right).b
                }
            } else initialTheme
        }
        val context = TestContext(initialThemes = themes)
        themeRepository = context.themeRepository
        val themeContext = setupContext(initialCharacterArcs = themes.flatMap { theme ->
            theme.characters.map { character ->
                CharacterArc.planNewCharacterArc(
                    character.id,
                    theme.id,
                    theme.name
                )
            }
        })
        characterArcRepository = themeContext.characterArcRepository
    }

    private fun whenUseCaseExecuted() {
        val useCase: ViewBaseStoryStructure = ViewBaseStoryStructureUseCase(
          themeRepository, characterArcRepository
        )
        runBlocking {
            useCase.invoke(characterUUID, themeUUID, object : ViewBaseStoryStructure.OutputPort {
                override fun receiveViewBaseStoryStructureResponse(response: ViewBaseStoryStructure.ResponseModel) {
                    result = response
                }

                override fun receiveViewBaseStoryStructureFailure(failure: Exception) {
                    result = failure
                }
            })
        }
    }

    private fun assertValidResponseModel(actual: Any?)
    {
        actual as ViewBaseStoryStructure.ResponseModel
        assertEquals(characterUUID, actual.characterId)
        assertEquals(themeUUID, actual.themeId)
        assertOnlyRequiredSectionsInOutput(actual)
        assertStoredValuesAreInOutput(actual)
        assertLinkedLocationsAreInOutput(actual)
    }

    private fun assertOnlyRequiredSectionsInOutput(response: ViewBaseStoryStructure.ResponseModel)
    {
        val requiredSections = CharacterArcTemplate.default().sections.filter { it.isRequired }.map { it.name }.toSet()
        assertEquals(requiredSections, response.sections.map { it.templateName }.toSet())
    }

    private fun assertStoredValuesAreInOutput(response: ViewBaseStoryStructure.ResponseModel)
    {
        val values = runBlocking {
            characterArcRepository.getCharacterArcByCharacterAndThemeId(Character.Id(response.characterId), Theme.Id(response.themeId))!!
                .arcSections
        }.associate { it.template.name to it.value }
        val templateToValues = response.sections.associate { it.templateName to it.value }
        templateToValues.forEach { (templateName, value) ->
            assertEquals(values[templateName] ?: "", value)
        }
    }

    private fun assertLinkedLocationsAreInOutput(response: ViewBaseStoryStructure.ResponseModel)
    {
        val locations = runBlocking {
            characterArcRepository.getCharacterArcByCharacterAndThemeId(Character.Id(response.characterId), Theme.Id(response.themeId))!!
                .arcSections
        }.associate { it.id.uuid to it.linkedLocation?.uuid }
        response.sections.forEach {
            assertEquals(locations.getValue(it.arcSectionId), it.linkedLocation, "Linked location id does not match")
        }
    }

}