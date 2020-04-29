package com.soyle.stories.location.locationList

import com.soyle.stories.di.resolve
import com.soyle.stories.location.deleteLocationDialog.deleteLocationDialog
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.Dialog
import com.soyle.stories.project.layout.LayoutViewListener
import javafx.geometry.Insets
import javafx.geometry.Pos
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 10:02 AM
 */
internal class ActionBar : View() {

    override val scope: ProjectScope = super.scope as ProjectScope

    private val model by inject<LocationListModel>()
    private val layoutViewListener: LayoutViewListener = resolve()

    override val root = hbox(alignment = Pos.CENTER, spacing = 10.0) {
        isFillHeight = false
        padding = Insets(5.0, 0.0, 5.0, 0.0)
        button("New Location") {
            id = "actionBar_createLocation"
            isDisable = false
            action {
                layoutViewListener.openDialog(Dialog.CreateLocation)
            }
            isMnemonicParsing = false
        }
        button("Delete") {
            id = "actionBar_deleteLocation"
            enableWhen { model.selectedItem.isNotNull }
            action {
                when (val selectedItem = model.selectedItem.value) {
                    is LocationItemViewModel -> deleteLocationDialog(scope, selectedItem)
                }
            }
            isMnemonicParsing = false
        }
    }
}
