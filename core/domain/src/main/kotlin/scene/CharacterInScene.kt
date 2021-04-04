package com.soyle.stories.domain.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.entities.Entity

class CharacterInScene(
    override val id: Character.Id,
    val sceneId: Scene.Id,
    val characterName: String,
    val roleInScene: RoleInScene?,
    val motivation: String?,
    val coveredArcSections: List<CharacterArcSection.Id>
) : Entity<Character.Id> {

    constructor(sceneId: Scene.Id, id: Character.Id, name: String) : this(
        id, sceneId, name, null, null, emptyList()
    )

    val characterId
        get() = id

    private fun copy(
        characterName: String = this.characterName,
        roleInScene: RoleInScene? = this.roleInScene,
        motivation: String? = this.motivation,
        coveredArcSections: List<CharacterArcSection.Id> = this.coveredArcSections
    ) = CharacterInScene(characterId, sceneId, characterName, roleInScene, motivation, coveredArcSections)

    internal fun withName(name: String): CharacterInScene = copy(characterName = name)

    internal fun withRoleInScene(roleInScene: RoleInScene?) = copy(roleInScene = roleInScene)

    internal fun withMotivation(motivation: String?) = copy(motivation = motivation)

    internal fun withCoveredArcSection(characterArcSection: CharacterArcSection): CharacterInScene
    {
        if (characterArcSection.characterId != characterId) throw CharacterArcSectionIsNotPartOfCharactersArc(
            characterId.uuid,
            characterArcSection.id.uuid,
            characterArcSection.characterId.uuid
        )
        if (characterArcSection.id in coveredArcSections) throw SceneAlreadyCoversCharacterArcSection(
            sceneId.uuid,
            characterId.uuid,
            characterArcSection.id.uuid
        )
        return copy(coveredArcSections = coveredArcSections + characterArcSection.id)
    }

    internal fun withoutCoveredArcSection(characterArcSection: CharacterArcSection): CharacterInScene
    {
        if (characterArcSection.characterId != characterId) throw CharacterArcSectionIsNotPartOfCharactersArc(
            characterId.uuid,
            characterArcSection.id.uuid,
            characterArcSection.characterId.uuid
        )
        return copy(coveredArcSections = coveredArcSections.filter { it != characterArcSection.id })
    }

}