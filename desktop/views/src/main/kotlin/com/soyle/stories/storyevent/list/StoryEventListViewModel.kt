package com.soyle.stories.storyevent.list

import javafx.beans.binding.ObjectExpression
import javafx.collections.ObservableList
import javafx.scene.control.MenuItem
import tornadofx.booleanProperty
import tornadofx.objectProperty
import tornadofx.observableListOf
import tornadofx.getValue
import tornadofx.setValue
import tornadofx.sizeProperty

sealed class StoryEventListViewModel {

    open val isPopulated = false
    open val isLoaded = false
}

object LoadingStoryEventListViewModel : StoryEventListViewModel()
object FailedStoryEventListViewModel : StoryEventListViewModel()

sealed interface LoadedStoryEventListViewModel {
    fun addStoryEvent(storyEvent: StoryEventListItemViewModel): PopulatedStoryEventListViewModel
}

object EmptyStoryEventListViewModel : StoryEventListViewModel(), LoadedStoryEventListViewModel {

    override val isLoaded: Boolean = true
    override fun addStoryEvent(storyEvent: StoryEventListItemViewModel): PopulatedStoryEventListViewModel = PopulatedStoryEventListViewModel(
        observableListOf(storyEvent))
}

class PopulatedStoryEventListViewModel(val items: ObservableList<StoryEventListItemViewModel>) :
    StoryEventListViewModel(), LoadedStoryEventListViewModel {

    override val isPopulated: Boolean = true
    override val isLoaded: Boolean = true

    val selectedItems: ObservableList<StoryEventListItemViewModel> = observableListOf()
    val hasSingleSelection = selectedItems.sizeProperty.isEqualTo(1)

    override fun addStoryEvent(storyEvent: StoryEventListItemViewModel): PopulatedStoryEventListViewModel {
        items.add(storyEvent)
        return this
    }

    private val requestingScenesToCoverProperty = booleanProperty(false)
    fun requestingScenesToCover() = requestingScenesToCoverProperty
    var isRequestingScenesToCover: Boolean by requestingScenesToCoverProperty

    private val scenesToCoverProperty = objectProperty<List<MenuItem>?>(null)
    fun scenesToCover(): ObjectExpression<List<MenuItem>?> = scenesToCoverProperty
    var scenesToCover: List<MenuItem>? by scenesToCoverProperty

}