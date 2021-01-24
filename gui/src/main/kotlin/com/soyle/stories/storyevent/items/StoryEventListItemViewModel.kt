package com.soyle.stories.storyevent.items

import com.soyle.stories.storyevent.usecases.StoryEventItem

class StoryEventListItemViewModel (
  val id: String,
  val ordinal: Int,
  val name: String
) {
	constructor(item: StoryEventItem) : this(item.storyEventId.toString(), item.influenceOrderIndex + 1, item.storyEventName)
}