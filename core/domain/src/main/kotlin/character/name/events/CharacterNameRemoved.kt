package com.soyle.stories.domain.character.name.events

import com.soyle.stories.domain.character.Character

data class CharacterNameRemoved(
    override val characterId: Character.Id,
    override val name: String
) : CharacterNameChange()