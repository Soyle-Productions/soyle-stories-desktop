package com.soyle.stories.theme.deleteThemeDialog

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class DeleteThemeDialog : Fragment() {

    override val scope: ProjectScope = super.scope as ProjectScope

    val model = resolve<DeleteThemeDialogModel>()

    private val alert = Alert(Alert.AlertType.CONFIRMATION)

    override val root: Parent = alert.dialogPane.apply {
        headerTextProperty().bind(model.message)
        content = vbox {
            checkbox {
                textProperty().bind(model.doNotShowLabel)
                selectedProperty().bindBidirectional(model.doDefaultAction)
            }
        }
        model.itemProperty.onChange { viewModel ->
            if (viewModel == null) {
                buttonTypes.clear()
                return@onChange
            }
            buttonTypes.setAll(
                ButtonType(viewModel.deleteButtonLabel, Delete),
                ButtonType(viewModel.cancelButtonLabel, Cancel)
            )
        }
    }

    init {
        titleProperty.bind(model.title)
        model.itemProperty.onChangeUntil({ it?.doDefaultAction != null }) {
            if (it?.doDefaultAction == false) {
                openModal(StageStyle.DECORATED, Modality.APPLICATION_MODAL)
            } else if (it?.doDefaultAction == true) {
                alert.result = ButtonType("", Delete)
            }
        }
    }

    private var internalScope: DeleteThemeDialogScope? = null

    override fun onUndock() {
        internalScope?.close()
    }

    fun show(themeId: String, themeName: String) {
        val internalScope = DeleteThemeDialogScope(scope, themeId, themeName)
        val viewListener = resolve<DeleteThemeDialogViewListener>(internalScope)
        alert.resultProperty().onChangeOnce {
            when (it?.buttonData) {
                Delete -> viewListener.deleteTheme(! model.doDefaultAction.value)
                else -> {}
            }
            internalScope.close()
            close()
        }
        this.internalScope = internalScope
        viewListener.getValidState()
    }

    companion object {
        private inline val Delete get() = ButtonBar.ButtonData.FINISH
        private inline val Cancel get() = ButtonBar.ButtonData.CANCEL_CLOSE
    }

    /*


	val viewListener = resolve<DeleteSceneDialogViewListener>()
	val model = resolve<DeleteSceneDialogModel>()


	override val root: Parent = alert.dialogPane.apply {
		headerTextProperty().bind(model.header)
		content = vbox {
			label(model.content)
			checkbox("Do not show this dialog again.") {
				model.showAgain.onChange {
					if (it != null) isSelected = ! it
				}
				selectedProperty().onChange {
					model.showAgain.value = ! it
				}
			}
		}
		model.itemProperty.onChange { viewModel ->
			if (viewModel == null) {
				buttonTypes.clear()
				return@onChange
			}
			buttonTypes.setAll(
			  ButtonType(viewModel.deleteButtonLabel, Delete),
			  ButtonType("Show Ramifications", Ramifications),
			  ButtonType(viewModel.cancelButtonLabel, Cancel)
			)
		}
	}

	init {
		titleProperty.bind(model.title)
		alert.resultProperty().onChangeOnce {
			when (it?.buttonData) {
				Delete -> viewListener.deleteScene(sceneId, model.showAgain.value)
				Ramifications -> viewListener.viewRamifications(sceneId, model.showAgain.value)
				else -> {}
			}
			close()
		}
		model.itemProperty.onChangeUntil({ it?.showAgain != null }) {
			if (it?.showAgain == true) {
				openModal(StageStyle.DECORATED, Modality.APPLICATION_MODAL)
			} else if (it?.showAgain == false) {
				alert.result = ButtonType("", Delete)
			}
		}
	}

	fun show(sceneItemViewModel: SceneItemViewModel)
	{
		sceneId = sceneItemViewModel.id
		viewListener.getValidState(sceneItemViewModel)
	}
     */
}