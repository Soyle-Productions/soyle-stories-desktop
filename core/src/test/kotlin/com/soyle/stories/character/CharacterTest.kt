package com.soyle.stories.character

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Created by Brendan
 * Date: 2/6/2020
 * Time: 8:18 PM
 */
class CharacterTest {

	val projectId = Project.Id()

	@Test
	fun `characters are built`() {
		Character.buildNewCharacter(projectId, NonBlankString.create("Bob")!!)
	}

	@Test
	fun `characters can be renamed`() {
		val character = Character.buildNewCharacter(projectId, NonBlankString.create("Bob")!!)
		  .withName(NonBlankString.create("Frank")!!)
		assertEquals("Frank", character.name.value)
	}
}