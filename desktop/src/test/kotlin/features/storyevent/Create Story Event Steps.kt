package com.soyle.stories.desktop.config.features.storyevent

import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.storyevent.*
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.storyevent.list.`Story Event List Tool Assertions`.Companion.assertThis
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.fail

class `Create Story Event Steps` : StoryEventFeatureSteps {

    init {
        Given("I have created a story event named {string}") { name: String ->
            storyEvents.givenStoryEventExists(withName = NonBlankString.create(name)!!)
        }
        Given("I have created a story event named {string} at time {int}") { name: String, time: Int ->
            storyEvents.givenStoryEventExists(withName = NonBlankString.create(name)!!, atTime = time)
        }
        Given("I have created the following story events") { data: DataTable ->
            data.asLists().drop(1).forEach { (name, time) ->
                storyEvents.givenStoryEventExists(withName = NonBlankString.create(name)!!, atTime = time.toInt())
            }
        }


        When("I create a story event named {string}") { name: String ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCreateStoryEventDialogHasBeenOpened()
                .createStoryEventNamed(name)
        }
        When("I create a story event named {string} at time {int}") { name: String, time: Int ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCreateStoryEventDialogHasBeenOpened()
                .createStoryEventNamed(name, time)
            runBlocking { delay(100) } // given events time to propagate
        }
        When(
            "I create a story event named {string} {} the {story event}"
        ) { name: String, placement: String, relativeStoryEvent: StoryEvent ->
            val workBench = soyleStories.getAnyOpenWorkbenchOrError()
            workBench
                .givenStoryEventListToolHasBeenOpened()
                .givenStoryEventHasBeenSelected(relativeStoryEvent)
                .openCreateRelativeStoryEventDialog(placement = placement)

            workBench
                .getOpenCreateStoryEventDialogOrError()
                .createStoryEventNamed(name)
            runBlocking { delay(100) } // given events time to propagate
        }


        Then("a story event named {string} should have been created") { expectedName: String ->
            val storyEvent = storyEvents.getStoryEventByName(expectedName)
                ?: fail("Story event named \"$expectedName\" was not created")

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenStoryEventListToolHasBeenOpened()
                .assertThis {
                    hasStoryEvent(storyEvent)
                }
        }
    }

}