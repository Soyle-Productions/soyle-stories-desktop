package com.soyle.stories.domain.scene


import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.events.*
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.EntitySet
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.domain.validation.entitySetOf
import java.util.*

class Scene private constructor(
    override val id: Id,
    val projectId: Project.Id,
    val name: NonBlankString,
    val storyEventId: StoryEvent.Id,
    val settings: EntitySet<SceneSettingLocation>,
    val proseId: Prose.Id,
    private val charactersInScene: EntitySet<CharacterInScene>,
    private val symbols: Collection<TrackedSymbol>,
    val conflict: SceneConflict,
    val resolution: SceneResolution,

    defaultConstructorMarker: Unit = Unit
) : Entity<Scene.Id> {

    constructor(
        projectId: Project.Id,
        name: NonBlankString,
        storyEventId: StoryEvent.Id,
        proseId: Prose.Id
    ) : this(
        Id(),
        projectId,
        name,
        storyEventId,
        entitySetOf(),
        proseId,
        entitySetOf(),
        listOf(),
        SceneConflict(""),
        SceneResolution("")
    )

    constructor(
        id: Id,
        projectId: Project.Id,
        name: NonBlankString,
        storyEventId: StoryEvent.Id,
        settings: EntitySet<SceneSettingLocation>,
        proseId: Prose.Id,
        charactersInScene: EntitySet<CharacterInScene>,
        symbols: Collection<TrackedSymbol>,
        conflict: SceneConflict,
        resolution: SceneResolution,
    ) : this(
        id,
        projectId,
        name,
        storyEventId,
        settings,
        proseId,
        charactersInScene,
        symbols,
        conflict,
        resolution,
        defaultConstructorMarker = Unit
    ) {
        if (trackedSymbols.size != symbols.size) {
            error(
                "Cannot track the same symbol more than once in a scene.\n${
                    symbols.groupBy { it.symbolId }.filter { it.value.size > 1 }
                }"
            )
        }
    }

    val includedCharacters by lazy { IncludedCharacters() }
    val trackedSymbols: TrackedSymbols by lazy { TrackedSymbols() }

    fun includesCharacter(characterId: Character.Id): Boolean {
        return charactersInScene.containsEntityWithId(characterId)
    }

    fun getMotivationForCharacter(characterId: Character.Id): CharacterMotivation? {
        return charactersInScene.getEntityById(characterId)?.let {
            CharacterMotivation(it.characterId, it.characterName, it.motivation)
        }
    }

    fun getCoveredCharacterArcSectionsForCharacter(characterId: Character.Id): List<CharacterArcSection.Id>? {
        return charactersInScene.getEntityById(characterId)?.coveredArcSections
    }

    private val coveredArcSectionIds by lazy {
        charactersInScene.flatMap { it.coveredArcSections }.toSet()
    }

    fun isCharacterArcSectionCovered(characterArcSectionId: CharacterArcSection.Id): Boolean {
        return coveredArcSectionIds.contains(characterArcSectionId)
    }

    fun hasCharacters(): Boolean = charactersInScene.isNotEmpty()

    operator fun contains(locationId: Location.Id) = settings.containsEntityWithId(locationId)

    private fun copy(
        name: NonBlankString = this.name,
        settings: EntitySet<SceneSettingLocation> = this.settings,
        charactersInScene: EntitySet<CharacterInScene> = this.charactersInScene,
        symbols: Collection<TrackedSymbol> = this.symbols,
        conflict: SceneConflict = this.conflict,
        resolution: SceneResolution = this.resolution
    ) = Scene(
        id,
        projectId,
        name,
        storyEventId,
        settings,
        this.proseId,
        charactersInScene,
        symbols,
        conflict,
        resolution,
        defaultConstructorMarker = Unit
    )

    fun withName(newName: NonBlankString) = copy(name = newName)

    fun withSceneFrameValue(value: SceneFrameValue): SceneUpdate<SceneFrameValueChanged> {
        when (value) {
            is SceneConflict -> {
                if (value == conflict) return WithoutChange(this)
                return Updated(copy(conflict = value), SceneFrameValueChanged(id, value))
            }
            is SceneResolution -> {
                if (value == resolution) return WithoutChange(this)
                return Updated(copy(resolution = value), SceneFrameValueChanged(id, value))
            }
        }
    }

    fun withCharacterIncluded(character: Character): SceneUpdate<IncludedCharacterInScene> {
        if (includesCharacter(character.id)) return noUpdate()
        val characterInScene = CharacterInScene(
            character.id,
            id,
            character.name.value,
            null,
            listOf()
        )
        return Updated(
            copy(
                charactersInScene = charactersInScene + characterInScene
            ),
            IncludedCharacterInScene(id, IncludedCharacter(character.id, character.name.value))
        )
    }

    fun withCharacterRenamed(character: Character): SceneUpdate<RenamedCharacterInScene> {
        val characterInScene = includedCharacters.getOrError(character.id)
        if (characterInScene.characterName == character.name.value) return noUpdate()

        return Updated(
            copy(
                charactersInScene = charactersInScene
                    .minus(character.id)
                    .plus(characterInScene.withName(character.name.value))
            ),
            RenamedCharacterInScene(id, IncludedCharacter(character.id, character.name.value))
        )
    }

    fun withMotivationForCharacter(characterId: Character.Id, motivation: String?): Scene {
        val characterInScene = includedCharacters.getOrError(characterId)
        return copy(
            charactersInScene = charactersInScene
                .minus(characterId)
                .plus(characterInScene.withMotivation(motivation))
        )
    }

    fun withLocationLinked(location: Location): SceneUpdate<LocationUsedInScene> {
        if (settings.containsEntityWithId(location.id)) return noUpdate()
        val sceneSetting = SceneSettingLocation(location)
        return Updated(copy(settings = settings + sceneSetting), LocationUsedInScene(id, sceneSetting))
    }

    fun withoutLocation(locationId: Location.Id): SceneUpdate<LocationRemovedFromScene> {
        val sceneSetting = settings.getEntityById(locationId) ?: return noUpdate()
        return Updated(
            copy(settings = settings.minus(sceneSetting)),
            LocationRemovedFromScene(id, sceneSetting)
        )
    }

    fun withLocationRenamed(location: Location): SceneUpdate<SceneSettingLocationRenamed> {
        val sceneSetting = getSceneSettingOrError(location.id)
        if (sceneSetting.locationName == location.name.value) return noUpdate()
        val newSceneSetting = sceneSetting.copy(locationName = location.name.value)
        return Updated(
            copy(settings = settings.minus(sceneSetting).plus(newSceneSetting)),
            SceneSettingLocationRenamed(id, newSceneSetting)
        )
    }

    private fun getSceneSettingOrError(locationId: Location.Id): SceneSettingLocation {
        return settings.getEntityById(locationId) ?: throw SceneDoesNotUseLocation(id, locationId)
    }

    fun withoutCharacter(characterId: Character.Id) =
        copy(charactersInScene = charactersInScene.minus(characterId))

    fun withCharacterArcSectionCovered(characterArcSection: CharacterArcSection): Scene {
        val characterInScene = includedCharacters.getOrError(characterArcSection.characterId)
        return copy(
            charactersInScene = charactersInScene
                .minus(characterInScene.characterId)
                .plus(characterInScene.withCoveredArcSection(characterArcSection))
        )
    }

    fun withoutCharacterArcSectionCovered(characterArcSection: CharacterArcSection): Scene {
        val characterInScene = includedCharacters.getOrError(characterArcSection.characterId)
        return copy(
            charactersInScene = charactersInScene
                .minus(characterInScene.characterId)
                .plus(characterInScene.withoutCoveredArcSection(characterArcSection))
        )
    }

    fun withSymbolTracked(theme: Theme, symbol: Symbol, pin: Boolean = false): SceneUpdate<SymbolTrackedInScene> {
        theme.symbols.find { it.id == symbol.id }
            ?: throw IllegalArgumentException("Symbol ${symbol.name} is not contained within the ${theme.name} theme")
        val newTrackedSymbol = TrackedSymbol(symbol.id, symbol.name, theme.id, pin)
        return if (trackedSymbols.isSymbolTracked(symbol.id)) noUpdate()
        else {
            Updated(copy(symbols = symbols + newTrackedSymbol), SymbolTrackedInScene(id, theme.name, newTrackedSymbol))
        }
    }

    fun withSymbolRenamed(symbolId: Symbol.Id, newName: String): SceneUpdate<TrackedSymbolRenamed> {
        val existingSymbol = trackedSymbols.getSymbolByIdOrError(symbolId)
        if (existingSymbol.symbolName == newName) return noUpdate()
        val trackedSymbol = trackedSymbols.getSymbolById(symbolId)!!.copy(symbolName = newName)
        return Updated(copy(symbols = symbols + trackedSymbol), TrackedSymbolRenamed(id, trackedSymbol))
    }

    fun withSymbolPinned(symbolId: Symbol.Id): SceneUpdate<SymbolPinnedToScene> {
        val existingSymbol = trackedSymbols.getSymbolByIdOrError(symbolId)
        if (existingSymbol.isPinned) return noUpdate()
        val trackedSymbol = existingSymbol.copy(isPinned = true)
        return Updated(copy(symbols = symbols + trackedSymbol), SymbolPinnedToScene(id, trackedSymbol))
    }

    fun withSymbolUnpinned(symbolId: Symbol.Id): SceneUpdate<SymbolUnpinnedFromScene> {
        val existingSymbol = trackedSymbols.getSymbolByIdOrError(symbolId)
        if (!existingSymbol.isPinned) return noUpdate()
        val trackedSymbol = existingSymbol.copy(isPinned = false)
        return Updated(copy(symbols = symbols + trackedSymbol), SymbolUnpinnedFromScene(id, trackedSymbol))
    }

    fun withoutSymbolTracked(symbolId: Symbol.Id): SceneUpdate<TrackedSymbolRemoved> {
        val trackedSymbol = trackedSymbols.getSymbolById(symbolId) ?: return noUpdate()
        return Updated(copy(symbols = trackedSymbols.withoutSymbol(symbolId)), TrackedSymbolRemoved(id, trackedSymbol))
    }

    fun noUpdate() = WithoutChange(this)

    data class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun toString(): String = "Scene($uuid)"
    }

    class CharacterMotivation(val characterId: Character.Id, val characterName: String, val motivation: String?) {
        fun isInherited() = motivation == null
    }

    class IncludedCharacter(val characterId: Character.Id, val characterName: String)

    inner class IncludedCharacters internal constructor() : Collection<CharacterInScene> by charactersInScene {

        operator fun get(characterId: Character.Id) = charactersInScene.getEntityById(characterId)
        fun getOrError(characterId: Character.Id): CharacterInScene = get(characterId) ?: throw SceneDoesNotIncludeCharacter(id, characterId)
    }

    inner class TrackedSymbols private constructor(private val symbolsById: Map<Symbol.Id, TrackedSymbol>) :
        Collection<TrackedSymbol> by symbolsById.values {
        internal constructor() : this(symbols.associateBy { it.symbolId })

        fun isSymbolTracked(symbolId: Symbol.Id): Boolean = symbolsById.containsKey(symbolId)
        fun getSymbolById(symbolId: Symbol.Id) = symbolsById[symbolId]
        fun getSymbolByIdOrError(symbolId: Symbol.Id) =
            symbolsById.getOrElse(symbolId) { throw SceneDoesNotTrackSymbol(id, symbolId) }

        internal fun withoutSymbol(symbolId: Symbol.Id): Collection<TrackedSymbol> = symbolsById.minus(symbolId).values
    }

    data class TrackedSymbol(
        val symbolId: Symbol.Id,
        val symbolName: String,
        val themeId: Theme.Id,
        val isPinned: Boolean = false
    )
}