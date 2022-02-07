package com.soyle.stories.domain.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.SceneUpdate.Successful
import com.soyle.stories.domain.scene.SceneUpdate.UnSuccessful
import com.soyle.stories.domain.scene.character.CharacterInScene
import com.soyle.stories.domain.scene.character.CharacterInSceneOperations
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.scene.character.events.*
import com.soyle.stories.domain.scene.character.exceptions.*
import com.soyle.stories.domain.scene.character.exceptions.CharacterInSceneAlreadyHasDesire
import com.soyle.stories.domain.scene.events.*
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.EntitySet
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.domain.validation.entitySetOf
import java.util.*
import java.util.logging.Logger

class Scene private constructor(
    override val id: Id,
    val projectId: Project.Id,
    val name: NonBlankString,
    val settings: EntitySet<SceneSettingLocation>,
    val proseId: Prose.Id,
    private val charactersInScene: EntitySet<CharacterInScene>,
    private val symbols: Collection<TrackedSymbol>,
    val conflict: SceneConflict,
    val resolution: SceneResolution,

    @Suppress("UNUSED_PARAMETER")
    defaultConstructorMarker: Unit = Unit
) : Entity<Scene.Id> {

    companion object {
        @JvmStatic
        internal fun create(
            projectId: Project.Id,
            name: NonBlankString,
            proseId: Prose.Id
        ): SceneUpdate<SceneCreated> {
            val newScene = Scene(projectId, name, proseId)
            return Successful(newScene, SceneCreated(newScene.id, newScene.name.value, proseId))
        }

        @JvmStatic
        private val equalityProps
            get() = listOf(
                Scene::id,
                Scene::projectId,
                Scene::name,
                Scene::settings,
                Scene::proseId,
                Scene::charactersInScene,
                Scene::symbols,
                Scene::conflict,
                Scene::resolution
            )
    }

    private constructor(
        projectId: Project.Id,
        name: NonBlankString,
        proseId: Prose.Id
    ) : this(
        Id(),
        projectId,
        name,
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
            CharacterMotivation(it.characterId, it.motivation)
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
        resolution: SceneResolution = this.resolution,
    ) = Scene(
        id,
        projectId,
        name,
        settings,
        this.proseId,
        charactersInScene,
        symbols,
        conflict,
        resolution,
        defaultConstructorMarker = Unit
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Scene

        return equalityProps.all { it.get(this) == it.get(other) }
    }

    private val cachedHashCode: Int by lazy {
        equalityProps.drop(1)
            .fold(equalityProps.first().get(this).hashCode()) { result, prop ->
                31 * result + prop.get(this).hashCode()
            }
    }

    override fun hashCode(): Int = cachedHashCode

    override fun toString(): String {
        return "Scene(${equalityProps.joinToString(", ") { "${it.name}=${it.get(this)}" }})"
    }

    fun withName(newName: NonBlankString): SceneUpdate<SceneRenamed> {
        if (newName == name) return noUpdate()
        return Successful(copy(name = newName), SceneRenamed(id, newName.value))
    }

    fun withSceneFrameValue(value: SceneFrameValue): SceneUpdate<SceneFrameValueChanged> {
        when (value) {
            is SceneConflict -> {
                if (value == conflict) return UnSuccessful(this)
                return Successful(copy(conflict = value), SceneFrameValueChanged(id, value))
            }
            is SceneResolution -> {
                if (value == resolution) return UnSuccessful(this)
                return Successful(copy(resolution = value), SceneFrameValueChanged(id, value))
            }
        }
    }

    fun withCharacterIncluded(character: Character): SceneUpdate<CharacterIncludedInScene> {
        if (includesCharacter(character.id)) return noUpdate(CharacterAlreadyIncludedInScene(id, character.id))
        return copy(
            charactersInScene = charactersInScene
                .plus(
                    CharacterInScene(
                        id,
                        character.id
                    )
                )
        )
            .updatedBy(
                CharacterIncludedInScene(
                    id,
                    character.id,
                    character.displayName.value,
                    projectId
                )
            )
    }

    fun withCharacter(characterId: Character.Id): CharacterInSceneOperations? {
        val characterInScene = includedCharacters[characterId] ?: return null

        return object : CharacterInSceneOperations {
            override fun assignedRole(role: RoleInScene?): SceneUpdate<CompoundEvent<CharacterRoleInSceneChanged>> {
                if (characterInScene.roleInScene == null && role == null) {
                    return noUpdate(CharacterAlreadyDoesNotHaveRoleInScene(id, characterId))
                }
                if (characterInScene.roleInScene == role) return noUpdate(CharacterAlreadyHasRoleInScene(id, characterId, role!!))

                val newCharacter = characterInScene.withRoleInScene(role)
                val event = when (role) {
                    null -> CharacterRoleInSceneCleared(id, characterId)
                    else -> CharacterAssignedRoleInScene(id, characterId, role)
                }

                if (role == RoleInScene.IncitingCharacter && includedCharacters.incitingCharacter != null) {
                    return withCharacter(includedCharacters.incitingCharacter!!.id)!!.assignedRole(null)
                        .then { withCharacter(characterId)!!.assignedRole(role) }
                }

                return Successful(
                    copy(charactersInScene = charactersInScene.minus(characterId).plus(newCharacter)),
                    CompoundEvent(listOf(event))
                )
            }

            override fun desireChanged(desire: String): SceneUpdate<CharacterDesireInSceneChanged> {
                if (characterInScene.desire == desire) return noUpdate(
                    CharacterInSceneAlreadyHasDesire(
                        id,
                        characterId,
                        desire
                    )
                )
                return Successful(
                    copy(
                        charactersInScene = charactersInScene.minus(characterId)
                            .plus(characterInScene.withDesire(desire))
                    ),
                    CharacterDesireInSceneChanged(id, characterId, desire)
                )
            }

            override fun motivationChanged(motivation: String?): SceneUpdate<CharacterMotivationInSceneChanged> {
                if (characterInScene.motivation == null && motivation == null) return noUpdate(
                    CharacterInSceneAlreadyDoesNotHaveMotivation(id, characterId)
                )
                if (characterInScene.motivation == motivation) return noUpdate(
                    CharacterInSceneAlreadyHasMotivation(
                        id,
                        characterId,
                        motivation.orEmpty()
                    )
                )
                return copy(
                    charactersInScene = charactersInScene
                        .minus(characterId)
                        .plus(characterInScene.withMotivation(motivation))
                ).updatedBy(
                    motivation?.let { CharacterGainedMotivationInScene(id, characterId, it) }
                        ?: CharacterMotivationInSceneCleared(id, characterId)
                )
            }

            override fun removed(): SceneUpdate<CharacterRemovedFromScene> {
                return copy(charactersInScene = charactersInScene.minus(characterId))
                    .updatedBy(CharacterRemovedFromScene(id, characterId))
            }


        }

    }

    @Deprecated(
        message = "Outdated API",
        replaceWith = ReplaceWith("withCharacter(characterId)?.assignedRole(roleInScene)"),
        level = DeprecationLevel.WARNING
    )
    fun withRoleForCharacter(
        characterId: Character.Id,
        roleInScene: RoleInScene?
    ): SceneUpdate<CompoundEvent<CharacterRoleInSceneChanged>> {
        val op = withCharacter(characterId)!!
        return op.assignedRole(roleInScene)
    }

    @Deprecated(
        message = "Outdated API",
        replaceWith = ReplaceWith("withCharacter(characterId)?.desireChanged(desire)"),
        level = DeprecationLevel.WARNING
    )
    fun withDesireForCharacter(characterId: Character.Id, desire: String): SceneUpdate<CharacterDesireInSceneChanged> {
        val op = withCharacter(characterId)!!
        return op.desireChanged(desire)
    }

    fun withLocationLinked(locationId: Location.Id, locationName: String): SceneUpdate<LocationUsedInScene> {
        if (settings.containsEntityWithId(locationId)) return noUpdate()
        val sceneSetting = SceneSettingLocation(locationId, locationName)
        return Successful(copy(settings = settings + sceneSetting), LocationUsedInScene(id, sceneSetting))
    }

    fun withLocationLinked(location: Location): SceneUpdate<LocationUsedInScene> =
        withLocationLinked(location.id, location.name.value)

    fun withoutLocation(locationId: Location.Id): SceneUpdate<LocationRemovedFromScene> {
        val sceneSetting = settings.getEntityById(locationId) ?: return noUpdate()
        return Successful(
            copy(settings = settings.minus(sceneSetting)),
            LocationRemovedFromScene(id, sceneSetting)
        )
    }

    fun withLocationRenamed(locationId: Location.Id, locationName: String): SceneUpdate<SceneSettingLocationRenamed> {
        val sceneSetting = getSceneSettingOrError(locationId)
        if (sceneSetting.locationName == locationName) return noUpdate()
        val newSceneSetting = sceneSetting.copy(locationName = locationName)
        return Successful(
            copy(settings = settings.minus(sceneSetting).plus(newSceneSetting)),
            SceneSettingLocationRenamed(id, newSceneSetting)
        )
    }

    fun withLocationRenamed(location: Location): SceneUpdate<SceneSettingLocationRenamed> =
        withLocationRenamed(location.id, location.name.value)

    fun withSetting(settingId: Location.Id): SceneSettingLocationOperations? {
        val sceneSetting = settings.getEntityById(settingId) ?: return null
        return object : SceneSettingLocationOperations {
            override fun replacedWith(location: Location): SceneUpdate<LocationRemovedFromScene> {
                if (location.id == sceneSetting.id) return noUpdate(
                    reason = SceneSettingCannotBeReplacedBySameLocation(id, location.id)
                )
                return Successful(
                    copy(
                        settings = settings
                            .minus(sceneSetting)
                            .plus(SceneSettingLocation(location.id, location.name.value))
                    ),
                    LocationRemovedFromScene(
                        id,
                        sceneSetting.id,
                        replacedBy = LocationUsedInScene(id, location.id, location.name.value)
                    )
                )
            }
        }
    }

    private fun getSceneSettingOrError(locationId: Location.Id): SceneSettingLocation {
        return settings.getEntityById(locationId) ?: throw SceneDoesNotUseLocation(id, locationId)
    }

    @Deprecated(
        message = "Outdated API",
        replaceWith = ReplaceWith("withCharacter(characterId)?.removed()"),
        level = DeprecationLevel.WARNING
    )
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
            Successful(
                copy(symbols = symbols + newTrackedSymbol),
                SymbolTrackedInScene(id, theme.name, newTrackedSymbol)
            )
        }
    }

    fun withSymbolRenamed(symbolId: Symbol.Id, newName: String): SceneUpdate<TrackedSymbolRenamed> {
        val existingSymbol = trackedSymbols.getSymbolByIdOrError(symbolId)
        if (existingSymbol.symbolName == newName) return noUpdate()
        val trackedSymbol = trackedSymbols.getSymbolById(symbolId)!!.copy(symbolName = newName)
        return Successful(copy(symbols = symbols + trackedSymbol), TrackedSymbolRenamed(id, trackedSymbol))
    }

    fun withSymbolPinned(symbolId: Symbol.Id): SceneUpdate<SymbolPinnedToScene> {
        val existingSymbol = trackedSymbols.getSymbolByIdOrError(symbolId)
        if (existingSymbol.isPinned) return noUpdate()
        val trackedSymbol = existingSymbol.copy(isPinned = true)
        return Successful(copy(symbols = symbols + trackedSymbol), SymbolPinnedToScene(id, trackedSymbol))
    }

    fun withSymbolUnpinned(symbolId: Symbol.Id): SceneUpdate<SymbolUnpinnedFromScene> {
        val existingSymbol = trackedSymbols.getSymbolByIdOrError(symbolId)
        if (!existingSymbol.isPinned) return noUpdate()
        val trackedSymbol = existingSymbol.copy(isPinned = false)
        return Successful(copy(symbols = symbols + trackedSymbol), SymbolUnpinnedFromScene(id, trackedSymbol))
    }

    fun withoutSymbolTracked(symbolId: Symbol.Id): SceneUpdate<TrackedSymbolRemoved> {
        val trackedSymbol = trackedSymbols.getSymbolById(symbolId) ?: return noUpdate()
        return Successful(
            copy(symbols = trackedSymbols.withoutSymbol(symbolId)),
            TrackedSymbolRemoved(id, trackedSymbol)
        )
    }

    private infix fun <E : SceneEvent> updatedBy(event: E) = Successful<E>(
        this,
        event
    )

    fun noUpdate(reason: Throwable? = null) = UnSuccessful(this, reason)

    data class Id(val uuid: UUID = UUID.randomUUID()) {

        override fun toString(): String = "Scene($uuid)"
    }

    class CharacterMotivation(val characterId: Character.Id, val motivation: String?) {

        fun isInherited() = motivation == null
    }

    data class IncludedCharacter(val characterId: Character.Id)

    inner class IncludedCharacters internal constructor() : Collection<CharacterInScene> by charactersInScene {

        val incitingCharacter by lazy { includedCharacters.find { it.roleInScene == RoleInScene.IncitingCharacter } }

        operator fun get(characterId: Character.Id) = charactersInScene.getEntityById(characterId)
        fun getOrError(characterId: Character.Id): CharacterInScene =
            get(characterId) ?: throw SceneDoesNotIncludeCharacter(id, characterId)
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

    interface SceneSettingLocationOperations {

        fun replacedWith(location: Location): SceneUpdate<LocationRemovedFromScene>
    }

    data class TrackedSymbol(
        val symbolId: Symbol.Id,
        val symbolName: String,
        val themeId: Theme.Id,
        val isPinned: Boolean = false
    )
}
