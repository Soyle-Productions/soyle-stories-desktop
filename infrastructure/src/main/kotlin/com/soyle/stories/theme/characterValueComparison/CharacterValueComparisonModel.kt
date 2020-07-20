package com.soyle.stories.theme.characterValueComparison

import com.soyle.stories.characterarc.characterComparison.CharacterItemViewModel
import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.theme.usecases.listOppositionsInValueWeb.OppositionValueItem
import com.soyle.stories.theme.valueOppositionWebs.OppositionValueViewModel
import javafx.beans.property.SimpleObjectProperty
import tornadofx.onChange
import tornadofx.toObservable

class CharacterValueComparisonModel : Model<CharacterValueComparisonScope, CharacterValueComparisonViewModel>(CharacterValueComparisonScope::class) {

    val addCharacterButtonLabel = bind(CharacterValueComparisonViewModel::addCharacterButtonLabel)
    val openValueWebToolButtonLabel = bind(CharacterValueComparisonViewModel::openValueWebToolButtonLabel)
    val characters = bindImmutableList(CharacterValueComparisonViewModel::characters)
    val availableCharacters = SimpleObjectProperty<List<CharacterItemViewModel>?>(null)
    val availableOppositionValues = SimpleObjectProperty<List<AvailableValueWebViewModel>?>(null)

    override fun viewModel(): CharacterValueComparisonViewModel? {
        item = item?.copy(availableCharacters = availableCharacters.value, availableOppositionValues = availableOppositionValues.value)
        return item
    }

    init {
        itemProperty.onChange {
            availableCharacters.value = it?.availableCharacters
            availableOppositionValues.value = it?.availableOppositionValues
        }
    }

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope

}