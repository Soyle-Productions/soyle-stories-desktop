package com.soyle.stories.characterarc.usecases

import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.*
import com.soyle.stories.common.shouldBe
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.*
import com.soyle.stories.theme.makeTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Created by Brendan
 * Date: 2/25/2020
 * Time: 5:34 PM
 */
class ListAllCharacterArcsTest {

	private val projectId = Project.Id()

	private var result: Any? = null

	@Test
	fun `no characters`() {
		listAllCharacterArcs()
		result shouldBe ::emptyResult
	}

	@Test
	fun `characters without arcs`() {
		givenCharacters(count = 4)
		listAllCharacterArcs()
		result shouldBe responseModel(expectedCount = 4)
	}

	@Test
	fun `characters with arcs`() {
		givenCharacters(count = 3, arcsPerCharacter = 4)
		listAllCharacterArcs()
		result shouldBe responseModel(expectedCount = 3, expectedTotalArcCount = 12)
	}

	private val themeRepository = ThemeRepositoryDouble()
	private val characterArcRepository = CharacterRepositoryDouble()

	private fun givenCharacters(count: Int, arcsPerCharacter: Int = 0)
	{
		repeat(count) { _ ->
			val character = makeCharacter(projectId = projectId, media = Media.Id())
			characterArcRepository.characters[character.id] = character
			repeat(arcsPerCharacter) { _ ->
				val themeId = Theme.Id()
				themeRepository.themes[themeId] = makeTheme(themeId, projectId = projectId)
					.withCharacterIncluded(character.id, character.name, character.media)
					.withCharacterPromoted(character.id)
			}
		}
	}

	private fun listAllCharacterArcs()
	{
		val useCase: ListAllCharacterArcs = ListAllCharacterArcsUseCase(characterArcRepository, themeRepository)
		val output = object : ListAllCharacterArcs.OutputPort {
			override suspend fun receiveCharacterArcList(response: CharacterArcsByCharacter) {
				result = response
			}
		}
		runBlocking {
			useCase.invoke(projectId.uuid, output)
		}
	}

	private fun emptyResult(actual: Any?) {
		actual as CharacterArcsByCharacter
		assertTrue(actual.characters.isEmpty())
	}

	private fun responseModel(expectedCount: Int, expectedTotalArcCount: Int = 0) = fun (actual: Any?) {
		actual as CharacterArcsByCharacter
		assertEquals(expectedCount, actual.characters.size)
		assertEquals(expectedTotalArcCount, actual.characters.flatMap { it.arcs }.size)
		assertCharactersHaveCorrectNames(actual.characters.map { it.character })
		assertCharactersHaveCorrectMediaIds(actual.characters.map { it.character })
		assertCharacterArcsHaveCorrectNames(actual.characters.flatMap { it.arcs })
	}

	private fun assertCharactersHaveCorrectNames(items: List<CharacterItem>) {
		items.forEach {
			assertEquals(
				characterArcRepository.characters[Character.Id(it.characterId)]!!.name,
				it.characterName
			)
		}
	}

	private fun assertCharactersHaveCorrectMediaIds(items: List<CharacterItem>) {
		items.forEach {
			assertEquals(
				characterArcRepository.characters[Character.Id(it.characterId)]!!.media?.uuid,
				it.mediaId
			)
		}
	}

	private fun assertCharacterArcsHaveCorrectNames(items: List<CharacterArcItem>) {
		items.forEach {
			assertEquals(
				themeRepository.themes[Theme.Id(it.themeId)]!!.name,
				it.characterArcName
			)
		}
	}

}