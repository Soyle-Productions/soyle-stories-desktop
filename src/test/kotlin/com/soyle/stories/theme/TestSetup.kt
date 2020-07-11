/**
 * Created by Brendan
 * Date: 3/11/2020
 * Time: 10:18 AM
 */
package com.soyle.stories.theme

import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.OppositionValue
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.entities.theme.ValueWeb
import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import com.soyle.stories.theme.repositories.CharacterRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

fun setupContext(
    initialThemes: List<Theme> = emptyList(),
    initialCharacters: List<Character> = emptyList(),
    initialCharacterArcs: List<CharacterArc> = emptyList(),
    initialCharacterArcSections: List<CharacterArcSection> = emptyList(),

    addNewCharacterArc: (CharacterArc) -> Unit = {},
    removeCharacterArc: (Theme.Id, Character.Id) -> Unit = { _, _ -> },
    updateCharacterArcSection: (CharacterArcSection) -> Unit = {},
    addNewCharacterArcSections: (List<CharacterArcSection>) -> Unit = {},
    removeArcSections: (List<CharacterArcSection>) -> Unit = {},
    updateTheme: (Theme) -> Unit = {},
    deleteTheme: (Theme) -> Unit = {}
): Context = object : Context {
    override val characterArcRepository: CharacterArcRepository = object : CharacterArcRepository {
        val arcs = mutableMapOf<Pair<Theme.Id, Character.Id>, CharacterArc>()
        init {
            arcs.putAll(initialCharacterArcs.map{ (it.themeId to it.characterId) to it })
        }
        override suspend fun getCharacterArcByCharacterAndThemeId(
            characterId: Character.Id,
            themeId: Theme.Id
        ): CharacterArc? = arcs[themeId to characterId]

        override suspend fun listCharacterArcsForTheme(themeId: Theme.Id): List<CharacterArc> =
            arcs.filterKeys { it.first == themeId }.map { it.value }

        override suspend fun addNewCharacterArc(characterArc: CharacterArc) {
            addNewCharacterArc.invoke(characterArc)
            arcs[characterArc.themeId to characterArc.characterId] = characterArc
        }

        override suspend fun removeCharacterArc(themeId: Theme.Id, characterId: Character.Id) {
            removeCharacterArc.invoke(themeId, characterId)
            arcs.remove(themeId to characterId)
        }
    }
    override val characterArcSectionRepository: CharacterArcSectionRepository = object : CharacterArcSectionRepository {
        val arcSections = mutableMapOf<CharacterArcSection.Id, CharacterArcSection>()
        init {
            arcSections.putAll(initialCharacterArcSections.map { it.id to it })
        }
        override suspend fun getCharacterArcSectionById(characterArcSectionId: CharacterArcSection.Id): CharacterArcSection? =
            arcSections[characterArcSectionId]

        override suspend fun updateCharacterArcSection(characterArcSection: CharacterArcSection) {
            updateCharacterArcSection.invoke(characterArcSection)
            arcSections[characterArcSection.id] = characterArcSection
        }

        override suspend fun addNewCharacterArcSections(characterArcSections: List<CharacterArcSection>) {
            addNewCharacterArcSections.invoke(characterArcSections)
            arcSections.putAll(characterArcSections.map { it.id to it })
        }

        override suspend fun removeArcSections(sections: List<CharacterArcSection>) {
            removeArcSections.invoke(sections)
            arcSections.keys.removeAll(sections.map { it.id })
        }

        override suspend fun getCharacterArcSectionsForCharacterInTheme(
            characterId: Character.Id,
            themeId: Theme.Id
        ): List<CharacterArcSection> =
            arcSections.filterValues { it.characterId == characterId && it.themeId == themeId }
                .values.toList()

        override suspend fun getCharacterArcSectionsForCharacter(characterId: Character.Id): List<CharacterArcSection> =
            arcSections.filterValues { it.characterId == characterId }
                .values.toList()

        override suspend fun getCharacterArcSectionsById(characterArcSectionIds: Set<CharacterArcSection.Id>): List<CharacterArcSection> =
            arcSections.filterKeys { it in characterArcSectionIds }.values.toList()

        override suspend fun getCharacterArcSectionsForTheme(themeId: Theme.Id): List<CharacterArcSection> =
            arcSections.filterValues { it.themeId == themeId }.values.toList()

    }
    override val characterRepository: CharacterRepository = object : CharacterRepository {
        val characters = mutableMapOf<Character.Id, Character>()
        init {
            characters.putAll(initialCharacters.map { it.id to it })
        }
        override suspend fun getCharacterById(characterId: Character.Id): Character? = characters[characterId]

    }
    override val themeRepository: ThemeRepository = object : ThemeRepository {
        val themes = mutableMapOf<Theme.Id, Theme>()
        init {
            themes.putAll(initialThemes.map { it.id to it })
        }
        override suspend fun updateThemes(themes: List<Theme>) {
            TODO("Not yet implemented")
        }


        override suspend fun addTheme(theme: Theme) {
            TODO("Not yet implemented")
        }

        override suspend fun getThemeContainingOppositionsWithSymbolicEntityId(symbolicId: UUID): List<Theme> {
            TODO("Not yet implemented")
        }


        override suspend fun getThemeContainingOppositionValueWithId(oppositionValueId: OppositionValue.Id): Theme? {
            TODO("Not yet implemented")
        }

        override suspend fun getThemeContainingValueWebWithId(valueWebId: ValueWeb.Id): Theme? {
            TODO("Not yet implemented")
        }

        override suspend fun listThemesInProject(projectId: Project.Id): List<Theme> {
            TODO("Not yet implemented")
        }

        override suspend fun getThemeContainingSymbolWithId(symbolId: Symbol.Id): Theme? {
            TODO("Not yet implemented")
        }
        override suspend fun getThemeById(id: Theme.Id): Theme? = themes[id]

        override suspend fun updateTheme(theme: Theme) {
            updateTheme.invoke(theme)
            themes[theme.id] = theme
        }

        override suspend fun deleteTheme(theme: Theme) {
            deleteTheme.invoke(theme)
            themes.remove(theme.id)
        }

    }
}