package com.soyle.stories.theme.deleteThemeDialog

import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.gui.View
import com.soyle.stories.theme.usecases.deleteTheme.DeleteTheme
import com.soyle.stories.theme.usecases.deleteTheme.DeletedTheme
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences

class DeleteThemeDialogPresenter(
    private val themeId: String,
    private val themeName: String,
    private val view: View.Nullable<DeleteThemeDialogViewModel>
) : GetDialogPreferences.OutputPort, DeleteTheme.OutputPort {

    override fun gotDialogPreferences(response: DialogPreference) {
        view.update {
            DeleteThemeDialogViewModel(
                title = "Confirm Delete Theme",
                message = "Are you sure you want to delete $themeName?",
                doNotShowLabel = "Do not show this dialog again",
                deleteButtonLabel = "Delete",
                cancelButtonLabel = "Cancel",
                doDefaultAction = ! response.shouldShow,
                errorMessage = null
            )
        }
    }

    override fun themeDeleted(response: DeletedTheme) {
        if (response.themeId.toString() != themeId) return
        view.updateOrInvalidated {
            this
        }
    }

    override fun failedToGetDialogPreferences(failure: Exception) {
        view.update {
            DeleteThemeDialogViewModel(
                title = "Confirm Delete Theme",
                message = "Are you sure you want to delete $themeName?",
                doNotShowLabel = "Do not show this dialog again",
                deleteButtonLabel = "Delete",
                cancelButtonLabel = "Cancel",
                doDefaultAction = false,
                errorMessage = failure.localizedMessage?.takeUnless { it.isBlank() } ?: "Failed to retrieve dialog preferences: ${failure::class.simpleName ?: failure::class}"
            )
        }
    }

    override suspend fun characterArcsDeleted(response: List<DeletedCharacterArc>) {
        // do nothing
    }

}