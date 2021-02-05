package com.soyle.stories.characterarc.usecases.listAllCharacterArcs


class CharacterArcsByCharacter(
    val characters: List<Pair<CharacterItem, List<CharacterArcItem>>>
)

val Pair<CharacterItem, List<CharacterArcItem>>.character get() = first
val Pair<CharacterItem, List<CharacterArcItem>>.arcs get() = second