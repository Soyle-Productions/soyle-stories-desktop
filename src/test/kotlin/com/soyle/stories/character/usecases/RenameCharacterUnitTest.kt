package com.soyle.stories.character.usecases

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacterUseCase
import com.soyle.stories.common.EntityId.Companion.asIdOf
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.mustEqual
import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.doubles.ProseRepositoryDouble
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.*
import com.soyle.stories.prose.makeProse
import com.soyle.stories.theme.makeCharacterInTheme
import com.soyle.stories.theme.makeTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class RenameCharacterUnitTest {

    private val character = makeCharacter()
    val themeId = UUID.randomUUID()
    val inputName = NonBlankString.create("Input Name")!!

    private var updatedCharacter: Character? = null
    private var updatedTheme: Theme? = null
    private var updatedProse: Prose? = null

    private val characterRepository = CharacterRepositoryDouble(onUpdateCharacter = ::updatedCharacter::set)
    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = ::updatedTheme::set)
    private val proseRepository = ProseRepositoryDouble(onReplaceProse = ::updatedProse::set)
    private var result: RenameCharacter.ResponseModel? = null

    @Test
    fun `character does not exist`() {
        val error = assertThrows<CharacterDoesNotExist> {
            renameCharacter()
        }
        error.characterId.mustEqual(character.id.uuid)
    }

    @Test
    fun `name is same as first name`() {
        characterRepository.givenCharacter(character.withName(inputName))
        renameCharacter()
        updatedCharacter.mustEqual(null) { "Character should not have been updated" }
        assertNull(result) { "No output should have been received" }
    }

    @Nested
    inner class `When Name is Different from Current Name` {

        init {
            characterRepository.givenCharacter(character)
        }

        @Test
        fun `should update character with new name`() {
            renameCharacter()
            val updatedCharacter = updatedCharacter!!
            updatedCharacter.name.value.mustEqual(inputName.value)
        }

        @Test
        fun `should output character renamed event`() {
            renameCharacter()
            result!!.characterRenamed.let {
                it.characterId.mustEqual(character.id)
                it.newName.mustEqual(inputName.value)
            }
        }

        @Nested
        inner class `When Theme has Character` {

            private val theme =
                makeTheme(includedCharacters = mapOf(character.id to makeCharacterInTheme(character.id)))

            init {
                themeRepository.givenTheme(theme)
            }

            @Test
            fun `should update character in theme name`() {
                renameCharacter()
                updatedTheme!!.getIncludedCharacterById(character.id)!!.name.mustEqual(inputName.value)
            }

            @Test
            fun `should output character in theme renamed event`() {
                renameCharacter()
                result!!.affectedThemeIds.mustEqual(listOf(theme.id.uuid))
            }

        }

    }

    @Nested
    inner class `Rule - All Prose that mention the character should update the mention of that character` {

        private val prose = makeProse(
            content = character.name.value, mentions = listOf(
                ProseMention(character.id.asIdOf(Character::class), ProseMentionRange(0, character.name.length))
            )
        )

        init {
            characterRepository.givenCharacter(character)
        }

        @Test
        fun `should update prose`() {
            proseRepository.givenProse(prose)
            renameCharacter()
            updatedProse!!.let {
                it.content.mustEqual(inputName.value) { "prose with only mention should have entire content replaced" }
                it.mentions.mustEqual(listOf(
                    ProseMention(character.id.asIdOf(Character::class), ProseMentionRange(0, inputName.length))
                ))
            }
        }

        @Test
        fun `should output prose mention text replaced events`() {
            proseRepository.givenProse(prose)
            renameCharacter()
            result!!.mentionTextReplaced.single().let {
                it.deletedText.mustEqual(character.name.value)
                it.entityId.mustEqual(character.id.asIdOf(Character::class))
                it.insertedText.mustEqual(inputName.value)
                it.newContent.mustEqual(updatedProse!!.content)
                it.newMentions.mustEqual(updatedProse!!.mentions)
            }
        }

    }

    private fun renameCharacter(inputName: NonBlankString = this.inputName) {
        val useCase: RenameCharacter = RenameCharacterUseCase(characterRepository, themeRepository, proseRepository)
        val output = object : RenameCharacter.OutputPort {

            override suspend fun receiveRenameCharacterResponse(response: RenameCharacter.ResponseModel) {
                result = response
            }
        }

        runBlocking {
            useCase.invoke(character.id.uuid, inputName, output)
        }
    }

}