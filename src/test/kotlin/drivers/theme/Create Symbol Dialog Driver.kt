package com.soyle.stories.desktop.config.drivers.theme

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.theme.createSymbolDialog.CreateSymbolDialogDriver
import com.soyle.stories.desktop.view.theme.themeList.ThemeListDriver
import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialog
import com.soyle.stories.theme.themeList.ThemeList
import javafx.event.ActionEvent
import tornadofx.uiComponent

fun ThemeList.openCreateSymbolDialogForThemeNamed(themeName: String): CreateSymbolDialog {
    val driver = ThemeListDriver(this)
    val tree = driver.getTree()
    val themeItem = driver.getThemeItemOrError(themeName)
    val createSymbolItem = themeItemContextMenu.items.find { it.text == "Create Symbol" }!!
    robot.interact {
        tree.selectionModel.select(themeItem)
        createSymbolItem.fire()
    }
    return getCreateSymbolDialog() ?: error("Theme list did not properly open Create Symbol Dialog for theme \"$themeName\"")
}

fun getCreateSymbolDialog(): CreateSymbolDialog? =
    robot.listWindows().asSequence()
        .mapNotNull { it.scene.root.uiComponent<CreateSymbolDialog>() }
        .firstOrNull { it.currentStage?.isShowing == true }


fun CreateSymbolDialog.createSymbolWithName(symbolName: String) {
    val driver = CreateSymbolDialogDriver(this)
    val nameInput = driver.getNameInput()
    driver.interact {
        nameInput.text = symbolName
        nameInput.fireEvent(ActionEvent())
    }
}