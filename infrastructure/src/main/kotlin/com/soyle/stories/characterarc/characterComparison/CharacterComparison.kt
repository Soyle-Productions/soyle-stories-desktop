package com.soyle.stories.characterarc.characterComparison

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.LayoutViewListener
import javafx.collections.ObservableList
import javafx.geometry.Side
import javafx.scene.control.ContextMenu
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import kotlinx.coroutines.runBlocking
import tornadofx.*

class CharacterComparison : View("Character Comparison") {

    override val scope = super.scope as CharacterComparisonScope
    val model = find<CharacterComparisonModel>()
    val characterComparisonViewListener: CharacterComparisonViewListener = resolve()
    val layoutViewListener = resolve<LayoutViewListener>(scope.projectScope)

    init {
        model.subTools.onChangeUntil({ !it.isNullOrEmpty() }) {
            if (it == null || it.isEmpty()) return@onChangeUntil
            model.pageSelection.set(it.first().label)
        }
    }

    override val root = borderpane {
        addClass(WizardStyles.wizard)
        top {
            hbox {
                addClass(WizardStyles.header)
                label {
                    model.pageSelection.onChange {
                        text = if (it == "Character Change") "${model.focusedCharacter.value?.characterName}'s Character Change"
                        else it
                    }
                    model.focusedCharacter.onChange {
                        text = if (model.pageSelection.value == "Character Change") "${it?.characterName}'s Character Change"
                        else model.pageSelection.value
                    }
                }
                spacer()
                combobox<CharacterItemViewModel> {
                    cellFormat {
                        text = item.characterName
                    }
                    var selectionBeingSet = false
                    model.focusedCharacter.onChange {
                        selectionBeingSet = true
                        selectionModel.select(it)
                        selectionBeingSet = false
                    }
                    model.characterOptions.onChange { it: ObservableList<CharacterItemViewModel>? ->
                        selectionBeingSet = true
                        items.setAll(it)
                        selectionModel.select(model.focusedCharacter.value)
                        selectionBeingSet = false
                    }
                    setOnAction {
                        if (selectionBeingSet) return@setOnAction
                        val selectedItem = selectionModel.selectedItem ?: return@setOnAction
                        runAsync {
                            runBlocking {
                                characterComparisonViewListener.getCharacterComparison(selectedItem.characterId)
                            }
                        }
                    }
                }
                button("Add Character") {
                    enableWhen { model.availableCharactersToAdd.emptyProperty().not() }
                    action {
                        optionMenu.show(this, Side.BOTTOM, 0.0, 0.0)
                    }
                }
            }
        }
        left {
            vbox {
                addClass(WizardStyles.stepInfo)
                bindChildren(model.subTools) {
                    hyperlink(it.label) {
                        toggleClass(WizardStyles.bold, model.pageSelection.isEqualTo(it.label))
                        action {
                            model.pageSelection.set(it.label)
                        }
                    }
                }
            }
        }
        center {
            stackpane {
                addClass(WizardStyles.content)
                this += find<ComparisonSubTool>(scope = scope).apply {
                    this.root.visibleWhen { model.pageSelection.isEqualTo("Comparisons") }
                }
                this += find<MoralProblemSubTool>(scope = scope).apply {
                    this.root.visibleWhen { model.pageSelection.isEqualTo("Moral Problem") }
                }
                this += find<CharacterChangeSubTool>(scope = scope).apply {
                    this.root.visibleWhen { model.pageSelection.isEqualTo("Character Change") }
                }
            }
        }
    }

    val optionMenu = ContextMenu().apply {
        isAutoFix = true
        isAutoHide = true
        model.availableCharactersToAdd.onChange { it: ObservableList<CharacterItemViewModel>? ->
            items.clear()
            it?.forEach {
                item(it.characterName) {
                    action {
                        runAsync {
                            runBlocking {
                                characterComparisonViewListener.addCharacterToComparison(it.characterId)
                            }
                        }
                    }
                }
            }
        }
    }
}