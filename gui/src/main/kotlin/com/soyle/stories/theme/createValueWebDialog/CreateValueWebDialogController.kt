package com.soyle.stories.theme.createValueWebDialog

import com.soyle.stories.theme.addValueWebToTheme.AddValueWebToThemeController

class CreateValueWebDialogController(
    private val presenter: CreateValueWebDialogPresenter,
    private val addValueWebToThemeController: AddValueWebToThemeController
) : CreateValueWebDialogViewListener {

    override fun getValidState() {
        presenter.presentDialog()
    }

    override fun createValueWeb(themeId: String, name: String) {
        addValueWebToThemeController.addValueWebToTheme(themeId, name, presenter::presentError)
    }

    override fun createValueWebAndLinkCharacter(themeId: String, name: String, characterId: String) {
        addValueWebToThemeController.addValueWebToThemeWithCharacter(themeId, name, characterId, presenter::presentError)
    }

}