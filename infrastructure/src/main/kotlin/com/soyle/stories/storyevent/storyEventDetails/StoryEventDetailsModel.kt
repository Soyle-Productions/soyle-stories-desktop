package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.soylestories.ApplicationScope
import tornadofx.toProperty

class StoryEventDetailsModel : Model<StoryEventDetailsScope, StoryEventDetailsViewModel>(StoryEventDetailsScope::class) {

	override val applicationScope: ApplicationScope
		get() = scope.projectScope.applicationScope

	val title = bind(StoryEventDetailsViewModel::title)
	val locationSelectionButtonLabel = bind(StoryEventDetailsViewModel::locationSelectionButtonLabel)
	val selectedLocation = bind(StoryEventDetailsViewModel::selectedLocation)
	val locations = bindImmutableList(StoryEventDetailsViewModel::locations)
	val includedCharacters = bindImmutableList(StoryEventDetailsViewModel::includedCharacters)
	val hasLocations = bind { (! item?.locations.isNullOrEmpty()).toProperty() }
	val availableCharacters = bindImmutableList(StoryEventDetailsViewModel::availableCharacters)
	val hasCharacters = bind { (! item?.availableCharacters.isNullOrEmpty()).toProperty() }

}