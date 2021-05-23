package com.soyle.stories.usecase.prose

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.prose.*
import com.soyle.stories.domain.prose.events.EntityMentionedInProse
import com.soyle.stories.domain.prose.events.ProseEvent
import com.soyle.stories.domain.prose.events.TextInsertedIntoProse
import com.soyle.stories.usecase.prose.bulkUpdateProse.*
import com.soyle.stories.usecase.repositories.ProseRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Bulk Update Prose Unit Test` {

    private val prose = makeProse()
    private var updatedProse: Prose? = null
    private val proseRepository = ProseRepositoryDouble(onReplaceProse = ::updatedProse::set)
    private var result: BulkUpdateProse.ResponseModel? = null

    @Test
    fun `prose doesn't exist should throw error`() {
        val error = assertThrows<ProseDoesNotExist> {
            bulkUpdate(listOf())
        }
        error.proseId.mustEqual(prose.id)
    }

    // apply events from the revision sent up to the current version to the operations.
    // don't apply events generated by the operations the group
    // save all the generated events and the final resulting prose
    // output all the generated events
    // invalid combinations fail the entire update

    @Nested
    inner class `Apply all operations` {

        init {
            proseRepository.givenProse(prose)
        }

        private val georgeId = Character.Id().mentioned()
        private val frankId = Character.Id().mentioned()
        private val operations = listOf(
            InsertText("I like George", 0),
            MentionEntity(georgeId, 7, 6),
            InsertText(" because he's a cool guy.  Unlike Frank", 13),
            MentionEntity(frankId, 47, 5),
            InsertText(" who thinks he owns the place.", 52)
        )
        private val expectedEvents = listOf(
            textInsertedIntoProse(prose.id, prose.revision + 1L, "I like George", 0),
            entityMentionedInProse(prose.id, prose.revision + 2L, georgeId, ProseMentionRange(7, 6)),
            textInsertedIntoProse(prose.id, prose.revision + 3L, " because he's a cool guy.  Unlike Frank", 13),
            entityMentionedInProse(prose.id, prose.revision + 4L, frankId, ProseMentionRange(47, 5)),
            textInsertedIntoProse(prose.id, prose.revision + 5L, " who thinks he owns the place.", 52)
        )

        @Test
        fun `should update text`() {
            bulkUpdate(operations)
            val updatedProse = updatedProse!!
            updatedProse.id.mustEqual(prose.id)
            updatedProse.content.mustEqual("I like George because he's a cool guy.  Unlike Frank who thinks he owns the place.")
            updatedProse.revision.mustEqual(prose.revision + 5L)
        }

        @Test
        fun `should add all mentions`() {
            bulkUpdate(operations)
            val updatedProse = updatedProse!!
            updatedProse.id.mustEqual(prose.id)
            updatedProse.mentions.mustEqual(
                listOf(
                    ProseMention(georgeId, ProseMentionRange(7, 6)),
                    ProseMention(frankId, ProseMentionRange(47, 5))
                )
            )
        }

        @Test
        fun `should save all events`() {
            bulkUpdate(operations)
            val events = runBlocking {
                proseRepository.getProseEvents(prose.id, prose.revision)
            }
            events.size.mustEqual(5)
            events.zip(expectedEvents).forEach {
                it.second(it.first)
            }
        }

        @Test
        fun `should output all events`() {
            bulkUpdate(operations)
            val result = result!!
            result.events.size.mustEqual(expectedEvents.size)
            result.events.zip(expectedEvents).forEach {
                it.second(it.first)
            }
        }

    }

    @Nested
    inner class `Update from the past` {


        init {
            proseRepository.givenProse(prose)
        }

        private val georgeId = Character.Id().mentioned()
        private val frankId = Character.Id().mentioned()
        private val operations = listOf(
            InsertText("I am a fan of George", 0),
            MentionEntity(georgeId, 14, 6),
            InsertText(" because he is a cool guy.  However, Frank", 20),
            MentionEntity(frankId, 57, 5),
            InsertText(" thinks he owns the place and isn't very cool.", 62)
        )
        private val expectedEvents = listOf(
            textInsertedIntoProse(prose.id, prose.revision + 6L, "I am a fan of George", 82),
            entityMentionedInProse(prose.id, prose.revision + 7L, georgeId, ProseMentionRange(96, 6)),
            textInsertedIntoProse(prose.id, prose.revision + 8L, " because he is a cool guy.  However, Frank", 102),
            entityMentionedInProse(prose.id, prose.revision + 9L, frankId, ProseMentionRange(139, 5)),
            textInsertedIntoProse(prose.id, prose.revision + 10L, " thinks he owns the place and isn't very cool.", 144)
        )

        init {
            bulkUpdate(listOf(
                InsertText("I like George", 0),
                MentionEntity(georgeId, 7, 6),
                InsertText(" because he's a cool guy.  Unlike Frank", 13),
                MentionEntity(frankId, 47, 5),
                InsertText(" who thinks he owns the place.", 52)
            ))
            updatedProse = null
            result = null
        }

        @Test
        fun `should update text`() {
            bulkUpdate(operations)
            val updatedProse = updatedProse!!
            updatedProse.id.mustEqual(prose.id)
            updatedProse.content.mustEqual("I like George because he's a cool guy.  Unlike Frank who thinks he owns the place.I am a fan of George because he is a cool guy.  However, Frank thinks he owns the place and isn't very cool.")
            updatedProse.revision.mustEqual(prose.revision + 10L)
        }

        @Test
        fun `should add all mentions`() {
            bulkUpdate(operations)
            val updatedProse = updatedProse!!
            updatedProse.id.mustEqual(prose.id)
            updatedProse.mentions.mustEqual(
                listOf(
                    ProseMention(georgeId, ProseMentionRange(7, 6)),
                    ProseMention(frankId, ProseMentionRange(47, 5)),
                    ProseMention(georgeId, ProseMentionRange(96, 6)),
                    ProseMention(frankId, ProseMentionRange(139, 5))
                )
            )
        }

        @Test
        fun `should save events from operations`() {
            bulkUpdate(operations)
            val events = runBlocking {
                proseRepository.getProseEvents(prose.id, prose.revision + 5L)
            }
            events.size.mustEqual(5)
            events.zip(expectedEvents).forEach {
                it.second(it.first)
            }
        }

        @Test
        fun `should output events from operations`() {
            bulkUpdate(operations)
            val result = result!!
            result.events.size.mustEqual(expectedEvents.size)
            result.events.zip(expectedEvents).forEach {
                it.second(it.first)
            }
        }

    }

    private fun bulkUpdate(operations: List<Operation>) {
        val useCase: BulkUpdateProse = BulkUpdateProseUseCase(proseRepository)
        runBlocking {
            useCase.invoke(prose.id, prose.revision, operations, object : BulkUpdateProse.OutputPort {
                override suspend fun receiveBulkUpdateResponse(response: BulkUpdateProse.ResponseModel) {
                    result = response
                }
            })
        }
    }


    fun textInsertedIntoProse(proseId: Prose.Id, revision: Long, insertedText: String, index: Int) =
        fun(event: ProseEvent) {
            event as TextInsertedIntoProse
            Assertions.assertEquals(proseId, event.proseId)
            Assertions.assertEquals(revision, event.revision)
            Assertions.assertEquals(insertedText, event.insertedText)
            Assertions.assertEquals(index, event.index)
        }

    fun entityMentionedInProse(proseId: Prose.Id, revision: Long, entityId: MentionedEntityId<*>, range: ProseMentionRange) =
        fun(event: ProseEvent) {
            event as EntityMentionedInProse
            Assertions.assertEquals(proseId, event.proseId)
            Assertions.assertEquals(revision, event.revision)
            Assertions.assertEquals(entityId, event.entityId)
            Assertions.assertEquals(range, event.position)
        }
}