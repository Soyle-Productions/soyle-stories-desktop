package com.soyle.stories.theme.createSymbolDialog

import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.common.ValidationException
import com.soyle.stories.gui.View
import com.soyle.stories.theme.ThemeNameCannotBeBlank
import com.soyle.stories.theme.usecases.SymbolNameCannotBeBlank
import com.soyle.stories.theme.usecases.addSymbolToTheme.AddSymbolToTheme
import com.soyle.stories.theme.usecases.addSymbolToTheme.SymbolAddedToTheme
import com.soyle.stories.theme.usecases.createTheme.CreateTheme
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme
import com.soyle.stories.theme.usecases.deleteTheme.DeleteTheme
import com.soyle.stories.theme.usecases.deleteTheme.DeletedTheme
import com.soyle.stories.theme.usecases.listThemes.ListThemes
import com.soyle.stories.theme.usecases.listThemes.ThemeList
import com.soyle.stories.theme.usecases.renameTheme.RenameTheme
import com.soyle.stories.theme.usecases.renameTheme.RenamedTheme

class CreateSymbolDialogPresenter(
    private val view: View.Nullable<CreateSymbolDialogViewModel>
) : ListThemes.OutputPort, CreateTheme.OutputPort, DeleteTheme.OutputPort, RenameTheme.OutputPort, AddSymbolToTheme.OutputPort {

    override suspend fun themesListed(response: ThemeList) {
        view.update {
            CreateSymbolDialogViewModel(
                title = "Create New Symbol",
                nameFieldLabel = "Name",
                errorMessage = null,
                errorCause = null,
                themes = response.themes.map {
                    ThemeItemViewModel(it.themeId.toString(), it.themeName)
                }.sortedBy { it.themeName },
                createdId = null
            )
        }
    }

    override suspend fun themeCreated(response: CreatedTheme) {
        val newItem = ThemeItemViewModel(response.themeId.toString(), response.themeName)
        view.updateOrInvalidated {
            copy(
                themes = (themes + newItem).sortedBy { it.themeName }
            )
        }
    }

    override fun themeDeleted(response: DeletedTheme) {
        val themeId = response.themeId.toString()
        view.updateOrInvalidated {
            copy(
                themes = themes.filterNot { it.themeId == themeId }
            )
        }
    }

    override fun themeRenamed(response: RenamedTheme) {
        val themeId = response.themeId.toString()
        view.updateOrInvalidated {
            copy(
                themes = themes.map {
                    if (it.themeId != themeId) it
                    else ThemeItemViewModel(it.themeId, response.newName)
                }.sortedBy { it.themeName }
            )
        }
    }

    internal fun presentError(t: Throwable) {
        view.updateOrInvalidated {
            copy(
                errorMessage = when (t) {
                    is ThemeNameCannotBeBlank -> "Theme name cannot be blank"
                    is SymbolNameCannotBeBlank -> "Symbol name cannot be blank"
                    else -> t.localizedMessage?.takeUnless { it.isBlank() }
                        ?: "Something went wrong: ${t::class.simpleName}"
                },
                errorCause = when (t) {
                    is ThemeNameCannotBeBlank -> "ThemeName"
                    is SymbolNameCannotBeBlank -> "SymbolName"
                    else -> null
                }
            )
        }
    }

    override suspend fun addedSymbolToTheme(response: SymbolAddedToTheme) {
        view.update {
            CreateSymbolDialogViewModel("", "", null, null, emptyList(), createdId = response.symbolId.toString())
        }
    }

    override suspend fun characterArcsDeleted(response: List<DeletedCharacterArc>) {
        // do nothing
    }

}