package com.soyle.stories.theme.characterConflict

import com.soyle.stories.characterarc.characterComparison.CharacterItemViewModel
import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.onChange

class CharacterConflictModel : Model<CharacterConflictScope, CharacterConflictViewModel>(CharacterConflictScope::class) {

    val centralConflictFieldLabel = bind(CharacterConflictViewModel::centralConflictFieldLabel)
    val centralConflict = bind(CharacterConflictViewModel::centralConflict)
    val perspectiveCharacterLabel = bind(CharacterConflictViewModel::perspectiveCharacterLabel)
    val selectedPerspectiveCharacter = SimpleObjectProperty<CharacterItemViewModel?>(null)
    val availablePerspectiveCharacters = SimpleObjectProperty<List<AvailablePerspectiveCharacterViewModel>?>(null)
    val desireLabel = bind(CharacterConflictViewModel::desireLabel)
    val desire = bind(CharacterConflictViewModel::desire)
    val psychologicalWeaknessLabel = bind(CharacterConflictViewModel::psychologicalWeaknessLabel)
    val psychologicalWeakness = bind(CharacterConflictViewModel::psychologicalWeakness)
    val moralWeaknessLabel = bind(CharacterConflictViewModel::moralWeaknessLabel)
    val moralWeakness = bind(CharacterConflictViewModel::moralWeakness)
    val characterChangeLabel = bind(CharacterConflictViewModel::characterChangeLabel)
    val characterChange = bind(CharacterConflictViewModel::characterChange)

    val opponentSectionsLabel = bind(CharacterConflictViewModel::opponentSectionsLabel)
    val attackSectionLabel = bind(CharacterConflictViewModel::attackSectionLabel)
    val similaritiesSectionLabel = bind(CharacterConflictViewModel::similaritiesSectionLabel)
    val powerStatusOrAbilitiesLabel = bind(CharacterConflictViewModel::powerStatusOrAbilitiesLabel)
    val opponents = bindImmutableList(CharacterConflictViewModel::opponents)
    val availableOpponents = SimpleObjectProperty<List<AvailableOpponentViewModel>?>(null)

    override fun viewModel(): CharacterConflictViewModel? {
        item = item?.copy(
            selectedPerspectiveCharacter = selectedPerspectiveCharacter.value,
            availablePerspectiveCharacters = availablePerspectiveCharacters.value,
            availableOpponents = availableOpponents.value
        )
        return item
    }

    init {
        itemProperty.onChange {
            selectedPerspectiveCharacter.value = it?.selectedPerspectiveCharacter
            availablePerspectiveCharacters.value = it?.availablePerspectiveCharacters
            availableOpponents.value = it?.availableOpponents
        }
    }

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope

}