package com.soyle.stories.theme.changeThemeDetails

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.changeThemeDetails.RenameTheme
import java.util.*

class RenameThemeControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val renameTheme: RenameTheme,
    private val renameThemeOutputPort: RenameTheme.OutputPort
) : RenameThemeController {

    override fun renameTheme(themeId: String, newName: String) {
        val preparedThemeId = UUID.fromString(themeId)
        threadTransformer.async {
            renameTheme.invoke(
                preparedThemeId,
                newName,
                renameThemeOutputPort
            )
        }
    }

}