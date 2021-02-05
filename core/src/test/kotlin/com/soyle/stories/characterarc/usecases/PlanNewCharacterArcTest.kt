package com.soyle.stories.characterarc.usecases

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArc
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArcUseCase
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.shouldBe
import com.soyle.stories.common.str
import com.soyle.stories.doubles.CharacterArcRepositoryDouble
import com.soyle.stories.storyevent.characterDoesNotExist
import com.soyle.stories.doubles.CharacterArcSectionRepositoryDouble
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PlanNewCharacterArcTest {

    // provided data
    private val projectId = Project.Id()
    private val characterId = Character.Id()
    private val characterArcName = "Character Arc ${str()}"

    // persisted data
    private var createdTheme: Theme? = null
    private var updatedCharacter: Character? = null
    private var createdCharacterArc: CharacterArc? = null

    // output data
    private var result: PlanNewCharacterArc.ResponseModel? = null

    @Test
    fun `character doesn't exist`() {
        assertThrows<CharacterDoesNotExist> {
            planNewCharacterArc()
        } shouldBe characterDoesNotExist(characterId.uuid)
        assertNull(createdTheme)
        assertNull(updatedCharacter)
        assertNull(createdCharacterArc)
        assertNull(result)
    }

    @Nested
    inner class WhenCharacterExists {

        val characterName = NonBlankString.create("Bob the Builder")!!
        val character = makeCharacter(
            characterId, projectId, characterName
        )

        init {
            givenCharacter(character)
        }

        @Test
        fun `new theme is created`() {
            planNewCharacterArc()
            createdTheme!! shouldBe {
                assertEquals(projectId, it.projectId)
                assertEquals(characterArcName, it.name)
            }
        }

        @Test
        fun `character is major character in theme`() {
            planNewCharacterArc()
            createdTheme!!.getMajorCharacterById(characterId)!!
        }

        @Test
        fun `character arc has name provided`() {
            planNewCharacterArc()
            createdCharacterArc!! shouldBe {
                assertEquals(characterArcName, it.name)
            }
        }

        @Test
        fun `character in theme has name of character`() {
            planNewCharacterArc()
            assertEquals(characterName.value, createdTheme!!.getIncludedCharacterById(characterId)!!.name)
        }

        @Test
        fun `character arc should have required story sections and thematic sections`() {
            planNewCharacterArc()
            val addedArc = createdCharacterArc!!
            assertEquals(addedArc.template.sections.filter { it.isRequired }.size, addedArc.arcSections.size)
        }

        @Test
        fun `output should have name provided`() {
            planNewCharacterArc()
            assertEquals(characterArcName, result!!.createdCharacterArc.characterArcName)
        }

        @Test
        fun `output should have id of created theme`() {
            planNewCharacterArc()
            assertEquals(createdTheme!!.id.uuid, result!!.createdCharacterArc.themeId)
        }
    }

    private val themeRepository = ThemeRepositoryDouble(onAddTheme = {
        createdTheme = it
    })
    private val characterRepository =
        CharacterRepositoryDouble(onUpdateCharacter = {
            updatedCharacter = it
        })
    private val characterArcRepositoryDouble = CharacterArcRepositoryDouble(onAddNewCharacterArc = {
        createdCharacterArc = it
    })

    private fun givenCharacter(character: Character) {
        characterRepository.characters[character.id] = character
    }

    private fun planNewCharacterArc() {
        val useCase: PlanNewCharacterArc = PlanNewCharacterArcUseCase(
            characterRepository,
            themeRepository,
            characterArcRepositoryDouble
        )
        val output = object : PlanNewCharacterArc.OutputPort {
            override suspend fun characterArcPlanned(response: PlanNewCharacterArc.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(
                characterId.uuid,
                characterArcName,
                output
            )
        }
    }
}