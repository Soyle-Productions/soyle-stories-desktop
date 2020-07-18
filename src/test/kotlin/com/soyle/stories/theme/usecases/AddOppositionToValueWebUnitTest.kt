package com.soyle.stories.theme.usecases

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.CharacterInTheme
import com.soyle.stories.entities.theme.MinorCharacter
import com.soyle.stories.entities.theme.OppositionValue
import com.soyle.stories.entities.theme.ValueWeb
import com.soyle.stories.theme.*
import com.soyle.stories.theme.doubles.CharacterRepositoryDouble
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.AddOppositionToValueWeb
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.AddOppositionToValueWeb.RequestModel
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.AddOppositionToValueWebUseCase
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.OppositionAddedToValueWeb
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.CharacterAddedToOpposition
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.CharacterId
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.SymbolicItemId
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class AddOppositionToValueWebUnitTest {

    private val valueWebId = ValueWeb.Id()
    val valueWebName = "Wonder bread"

    private var result: AddOppositionToValueWeb.ResponseModel? = null

    @Test
    fun `value web does not exist`() {
        assertThrows<ValueWebDoesNotExist> {
            addOppositionToValueWeb()
        } shouldBe valueWebDoesNotExist(valueWebId.uuid)
    }

    @Test
    fun `value web exists`() {
        val theme = givenValueWebExists(valueWebName)
        addOppositionToValueWeb()
        val createdOpposition = createdOpposition()!!
        assertEquals("$valueWebName 1", createdOpposition.name)
        result shouldBe oppositionAddedToValueWeb(theme.id.uuid, valueWebId.uuid, createdOpposition.id.uuid,
            createdOpposition.name, true
        )
    }

    @Test
    fun `add another opposition`() {
        val theme = givenValueWebExists(valueWebName, 5)
        addOppositionToValueWeb()
        val createdOpposition = createdOpposition()!!
        assertEquals("$valueWebName 6", createdOpposition.name)
        result shouldBe oppositionAddedToValueWeb(theme.id.uuid, valueWebId.uuid, createdOpposition.id.uuid,
            createdOpposition.name, true
        )
    }

    @Test
    fun `provided name is blank`() {
        givenValueWebExists(valueWebName)
        assertThrows<OppositionValueNameCannotBeBlank> {
            addOppositionToValueWeb(name = "")
        }
        assertNull(createdOpposition())
        assertNull(result)
    }

    @Test
    fun `create with name`() {
        val theme = givenValueWebExists(valueWebName)
        addOppositionToValueWeb(name = "Horrible Bread")
        val createdOpposition = createdOpposition()!!
        assertEquals("Horrible Bread", createdOpposition.name)
        result shouldBe oppositionAddedToValueWeb(theme.id.uuid, valueWebId.uuid, createdOpposition.id.uuid,
            createdOpposition.name, false
        )
    }

    @Test
    fun `character does not exist`() {
        givenValueWebExists(valueWebName)
        assertThrows<CharacterDoesNotExist> {
            addOppositionToValueWeb(name = "Horrible Bread", itemId = CharacterId(UUID.randomUUID()))
        }
        assertNull(createdOpposition())
        assertNull(result)
    }

    @Test
    fun `character exists`() {
        val theme = givenValueWebExists(valueWebName)
        val character = givenCharacterExists(UUID.randomUUID())
        addOppositionToValueWeb(name = "Horrible Bread", itemId = CharacterId(character.id.uuid))
        createdOpposition()!! shouldBe {
            assertEquals(character.id.uuid, it.representations.single().entityUUID)
            assertEquals(character.name, it.representations.single().name)
        }
        updatedTheme!! shouldBe {
            assertEquals(character.id, it.characters.single().id)
            assertEquals(character.name, it.characters.single().name)
        }
        result!! shouldBe oppositionAddedToValueWeb(theme.id.uuid, valueWebId.uuid, createdOpposition()!!.id.uuid,
            createdOpposition()!!.name, false
        )
        result!! shouldBe {
            it.symbolicRepresentationAddedToOpposition shouldBe characterAddedToOpposition(
                theme.id.uuid, valueWebId.uuid, valueWebName, createdOpposition()!!.id.uuid, "Horrible Bread",
                character.id.uuid, character.name
            )
            it.characterIncludedInTheme shouldBe characterIncludedInTheme(
                theme.id.uuid, theme.name, character.id.uuid, character.name, false
            )
            assertNotNull(it.characterIncludedInTheme)
        }
    }

    @Test
    fun `character already in theme`() {
        val character = givenCharacterExists(UUID.randomUUID())
        val theme = givenValueWebExists(valueWebName, includeCharacter = character)
        addOppositionToValueWeb(name = "Horrible Bread", itemId = CharacterId(character.id.uuid))
        createdOpposition()!! shouldBe {
            assertEquals(character.id.uuid, it.representations.single().entityUUID)
            assertEquals(character.name, it.representations.single().name)
        }
        result!! shouldBe oppositionAddedToValueWeb(theme.id.uuid, valueWebId.uuid, createdOpposition()!!.id.uuid,
            createdOpposition()!!.name, false
        )
        result!! shouldBe {
            it.symbolicRepresentationAddedToOpposition shouldBe characterAddedToOpposition(
                theme.id.uuid, valueWebId.uuid, valueWebName, createdOpposition()!!.id.uuid, "Horrible Bread",
                character.id.uuid, character.name
            )
            assertNull(it.characterIncludedInTheme)
        }
    }

    private var updatedTheme: Theme? = null
    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = { updatedTheme = it })
    private val characterRepository = CharacterRepositoryDouble()

    private fun givenValueWebExists(valueWebName: String, existingOppositionCount: Int = 0, includeCharacter: Character? = null): Theme {
        val themeId = Theme.Id()
        val theme = makeTheme(
            id = themeId,
            valueWebs = listOf(makeValueWeb(valueWebId, themeId = themeId, name = valueWebName, oppositions = List(existingOppositionCount) { OppositionValue("") })),
            includedCharacters = listOfNotNull(includeCharacter).map { MinorCharacter(it.id, it.name, "", "", listOf()) }
                .associateBy { it.id }
        )
        themeRepository.themes[theme.id] = theme
        return theme
    }

    private fun givenCharacterExists(characterId: UUID): Character
    {
        val character = makeCharacter(Character.Id(characterId))
        characterRepository.characters[character.id] = character
        return character
    }

    private fun createdOpposition(): OppositionValue?
    {
        return updatedTheme?.let {
            it.valueWebs.find { it.id == valueWebId }?.oppositions?.last()
        }
    }

    private fun addOppositionToValueWeb(name: String? = null, itemId: CharacterId? = null)
    {
        val useCase: AddOppositionToValueWeb = AddOppositionToValueWebUseCase(themeRepository, characterRepository)
        val output = object: AddOppositionToValueWeb.OutputPort {
            override suspend fun addedOppositionToValueWeb(response: AddOppositionToValueWeb.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(RequestModel(valueWebId.uuid, name, itemId), output)
        }
    }

    private fun oppositionAddedToValueWeb(
        expectedThemeId: UUID,
        expectedValueWebId: UUID,
        expectedOppositionId: UUID,
        expectedOppositionName: String,
        expectedNeedRename: Boolean
    ): (Any?) -> Unit = { actual ->
        actual as OppositionAddedToValueWeb
        assertEquals(expectedThemeId, actual.themeId) { "OppositionAddedToValueWeb does not have correct themeId" }
        assertEquals(expectedValueWebId, actual.valueWebId) { "OppositionAddedToValueWeb does not have correct valueWebId" }
        assertEquals(expectedOppositionId, actual.oppositionValueId) { "OppositionAddedToValueWeb does not have correct oppositionValueId" }
        assertEquals(expectedOppositionName, actual.oppositionValueName) { "OppositionAddedToValueWeb does not have correct oppositionValueName" }
        assertEquals(expectedNeedRename, actual.needsName) { "OppositionAddedToValueWeb should ${if (! expectedNeedRename) "not " else ""}need a name" }
    }

    private fun characterAddedToOpposition(
        expectedThemeId: UUID,
        expectedValueWebId: UUID,
        expectedValueWebName: String,
        expectedOppositionId: UUID,
        expectedOppositionName: String,
        expectedItemId: UUID,
        expectedItemName: String
    ) = fun (actual: Any?) {
        actual as CharacterAddedToOpposition
        assertEquals(expectedThemeId, actual.themeId) { "CharacterAddedToOpposition does not have correct themeId" }
        assertEquals(expectedValueWebId, actual.valueWebId) { "CharacterAddedToOpposition does not have correct valueWebId" }
        assertEquals(expectedValueWebName, actual.valueWebName) { "CharacterAddedToOpposition does not have correct valueWebName" }
        assertEquals(expectedOppositionId, actual.oppositionId) { "CharacterAddedToOpposition does not have correct oppositionId" }
        assertEquals(expectedOppositionName, actual.oppositionName) { "CharacterAddedToOpposition does not have correct oppositionName" }
        assertEquals(expectedItemId, actual.characterId) { "CharacterAddedToOpposition does not have correct characterId" }
        assertEquals(expectedItemName, actual.itemName) { "CharacterAddedToOpposition does not have correct itemName" }
    }

    private fun characterIncludedInTheme(
        expectedThemeId: UUID,
        expectedThemeName: String,
        expectedCharacterId: UUID,
        expectedCharacterName: String,
        expectedIsMajorCharacter: Boolean
    ) = fun (actual: Any?) {
        actual as CharacterIncludedInTheme
        assertEquals(expectedThemeId, actual.themeId) { "CharacterIncludedInTheme does not have correct themeId" }
        assertEquals(expectedThemeName, actual.themeName) { "CharacterIncludedInTheme does not have correct themeName" }
        assertEquals(expectedCharacterId, actual.characterId) { "CharacterIncludedInTheme does not have correct characterId" }
        assertEquals(expectedCharacterName, actual.characterName) { "CharacterIncludedInTheme does not have correct characterName" }
        assertEquals(expectedIsMajorCharacter, actual.isMajorCharacter) { "CharacterIncludedInTheme should ${if (! expectedIsMajorCharacter)"not " else ""}be a major character" }
    }

}