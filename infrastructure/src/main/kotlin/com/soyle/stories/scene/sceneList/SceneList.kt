package com.soyle.stories.scene.sceneList

import com.soyle.stories.common.components.emptyListDisplay
import com.soyle.stories.common.makeEditable
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.createSceneDialog.createSceneDialog
import com.soyle.stories.scene.items.SceneItemViewModel
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
import javafx.scene.control.TreeItem
import javafx.scene.layout.Priority
import tornadofx.*

class SceneList : View() {

	override val scope: ProjectScope = super.scope as ProjectScope

	private val model = resolve<SceneListModel>()

	private val viewListener = resolve<SceneListViewListener>()

	override val root: Parent = stackpane {
		hgrow = Priority.SOMETIMES
		vgrow = Priority.ALWAYS
		emptyListDisplay(
		  model.hasScenes,
		  model.emptyLabel,
		  model.createSceneButtonLabel
		) {
			createSceneDialog(scope)
		}
		vbox {
			visibleWhen { model.hasScenes }
			managedProperty().bind(visibleProperty())
			minWidth = 200.0
			minHeight = 100.0
			vgrow = Priority.ALWAYS
			treeview<SceneItemViewModel?>(TreeItem(null)) {
				isShowRoot = false
				vgrow = Priority.ALWAYS
				makeEditable { newName, oldValue ->
					// rename item
					if (oldValue != null) {
						viewListener.renameScene(oldValue.id, newName)
					}

					oldValue
				}
				selectionModel.selectedItemProperty().onChange { model.selectedItem.value = it?.value }
				model.selectedItem.onChange { newSelection -> selectionModel.select(root.children.find { it.value?.id == newSelection?.id }) }
				model.selectedItem.onChange {
					contextMenu = when (it) {
						is SceneItemViewModel -> sceneContextMenu
						else -> null
					}
				}
				cellFormat {
					text = when (it) {
						is SceneItemViewModel -> it.name
						else -> throw IllegalArgumentException("Invalid value type")
					}
				}
				populate { parentItem: TreeItem<SceneItemViewModel?> ->
					when (parentItem.value) {
						null -> model.scenes
						else -> emptyList()
					}
				}
				onDoubleClick {
				}
			}
			hbox(alignment = Pos.CENTER, spacing = 10.0) {
				isFillHeight = false
				padding = Insets(5.0, 0.0, 5.0, 0.0)
				button(model.createSceneButtonLabel) {
					id = "actionBar_createScene"
					isDisable = false
					action {
						createSceneDialog(scope)
					}
					isMnemonicParsing = false
				}
				button("Delete") {
					id = "actionBar_deleteLocation"
					enableWhen { model.selectedItem.isNotNull }
					action {
						when (val selectedItem = model.selectedItem.value) {
							is SceneItemViewModel -> {}
						}
					}
					isMnemonicParsing = false
				}
			}
		}
	}

	private val sceneContextMenu = ContextMenu().apply {

	}

	init {
		titleProperty.bind(model.toolTitle)

		viewListener.getValidState()
	}

}