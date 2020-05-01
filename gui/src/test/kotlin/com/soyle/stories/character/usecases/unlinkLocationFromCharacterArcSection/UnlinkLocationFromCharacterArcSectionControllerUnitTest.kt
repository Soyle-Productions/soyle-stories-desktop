package com.soyle.stories.character.usecases.unlinkLocationFromCharacterArcSection

import com.soyle.stories.characterarc.CharacterArcException
import com.soyle.stories.characterarc.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSectionController
import com.soyle.stories.characterarc.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSectionControllerImpl
import com.soyle.stories.characterarc.usecases.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSection
import com.soyle.stories.gui.ThreadTransformer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class UnlinkLocationFromCharacterArcSectionControllerUnitTest {

	private var unlinkLocationFromCharacterArcSectionUseCaseWasCalledWith: UUID? = null

	@Test
	fun `invalid uuid string`() {
		assertThrows<IllegalArgumentException> {
			whenControllerIsCalledWith("")
		}
	}

	@Test
	fun `valid string`() {
		val sectionId = UUID.randomUUID().toString()
		whenControllerIsCalledWith(sectionId)
		assertEquals(sectionId, unlinkLocationFromCharacterArcSectionUseCaseWasCalledWith?.toString())
	}

	private var threadTransformerAsyncCalled: Boolean = false

	private fun whenControllerIsCalledWith(characterArcSectionId: String)
	{
		val controller: UnlinkLocationFromCharacterArcSectionController = UnlinkLocationFromCharacterArcSectionControllerImpl(
		  object : ThreadTransformer {
			  override fun async(task: suspend CoroutineScope.() -> Unit) {
				  threadTransformerAsyncCalled = true
				  runBlocking {
					  task()
				  }
				  threadTransformerAsyncCalled = false
			  }

			  override fun gui(update: suspend CoroutineScope.() -> Unit) { }
		  },
		  object : UnlinkLocationFromCharacterArcSection {
			override suspend fun invoke(characterArcSectionId: UUID, output: UnlinkLocationFromCharacterArcSection.OutputPort) {
				if (! threadTransformerAsyncCalled) error("not called using ThreadTransformer.async")
				unlinkLocationFromCharacterArcSectionUseCaseWasCalledWith = characterArcSectionId
			}
		}, object : UnlinkLocationFromCharacterArcSection.OutputPort {
			override fun receiveUnlinkLocationFromCharacterArcSectionFailure(failure: CharacterArcException) {}
			override fun receiveUnlinkLocationFromCharacterArcSectionResponse(response: UnlinkLocationFromCharacterArcSection.ResponseModel) {}
		})
		controller.unlinkLocationFromCharacterArcSection(characterArcSectionId)
	}

}